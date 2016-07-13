package nz.ac.auckland.abi.administration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import nz.ac.auckland.abi.businesslogic.InstanceBean;
import nz.ac.auckland.abi.businesslogic.StudyBean;
import nz.ac.auckland.abi.businesslogic.SubjectBean;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.dcm4chee.DCMAccessManager;
import nz.ac.auckland.abi.dcm4chee.InstanceRecord;
import nz.ac.auckland.abi.dcm4chee.PatientRecord;
import nz.ac.auckland.abi.dcm4chee.RetriveStudyFromPACS;
import nz.ac.auckland.abi.dcm4chee.StudyRecord;
import nz.ac.auckland.abi.entities.Instance;
import nz.ac.auckland.abi.entities.Series;
import nz.ac.auckland.abi.entities.Study;
import nz.ac.auckland.abi.entities.Subject;
import nz.auckland.abi.archive.CompressionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A class that checks if the DB (accessed via the EntityManager) records of a
 * Subject (identified by pid) are consistent with the PACS This class
 * implements the checking process via a thread, to avoid using a jboss parent
 * thread (which is expected to return quickly)
 * 
 * @author jagir
 *
 */

public class PACSSubjectSynchornizer implements Runnable {
	private SynchronizationManager syncManager;
	private EntityManager CAPDBManger;
	private SubjectBean subjectBean;
	private StudyBean studyBean;
	private InstanceBean instanceBean;
	private SystemNotificationBean syslog;
	private String id;
	private Thread th;
	int excludeTags[];
	private Exception exx;

	public PACSSubjectSynchornizer(SynchronizationManager manager, String pid) {
		syncManager = manager;
		CAPDBManger = manager.getEntityManager();
		subjectBean = manager.getSubjectBean();
		studyBean = manager.getStudyBean();
		instanceBean = manager.getInstanceBean();
		syslog = manager.getSystemNotificationBean();
		id = pid;
		exx = null;
		// The list of tags that should not be processed by the xml parser
		excludeTags = new int[1];
		excludeTags[0] = 0x7FE00010; // Pixel Data
	}

	private void removePatient(String patientID) throws Exception {
		subjectBean.removeSubject(patientID);
	}

	@SuppressWarnings("unchecked")
	private void addPatientFromPACS(String patientID) throws Exception {
		/**
		 * The plan is to check download all the studies and instances for the
		 * patient Note: DCMQR downloads all the instances related to a study
		 * Load the study record and process all pacs instances All beans are
		 * stored in memory and then processed through a single call to
		 * subjectBean Since this class is a POJO, a bean is required for JTA
		 */
		PatientRecord pr = DCMAccessManager.getPatient(patientID);
		Subject newSubject = new Subject(pr.getPatientID(), pr.getPatientName(), pr.getPatientBirthDate(), pr.getPatientSex());
		Vector<Study> studyEntitiesVector = new Vector<Study>();
		Vector<Series> seriesEntitiesVector = new Vector<Series>();
		Vector<Instance> instancesEntitiesVector = new Vector<Instance>();
/*		boolean cacheImages = syncManager.cachePACSImageData();
		RetriveStudyFromPACS rsfp = null;
		if (cacheImages)
			rsfp = new RetriveStudyFromPACS(syncManager.getPacsConnectionDetails());*/

		try {
			Vector<StudyRecord> studies1 = DCMAccessManager.getPatientStudies(pr.getPatientID(), -1);
			for (StudyRecord sr : studies1) {
				Study newStudy = new Study(pr.getPatientID(), sr.getStudyInstanceUID(), sr.getStudyModalities(), sr.getStudyDate(), sr.getStudyDescription());

				Vector<InstanceRecord> instances = DCMAccessManager.getStudyInstances(sr.getStudyInstanceUID(), -1);
				HashMap<String, Series> existingSeries = new HashMap<String, Series>();
				HashMap<String, JSONArray> seriesJSON = new HashMap<String, JSONArray>();

				JSONArray studySeries = new JSONArray();
				for (InstanceRecord ir : instances) {
					Series newSeries = existingSeries.get(ir.getSeriesID());
					if (newSeries == null) {
						newSeries = new Series(sr.getStudyInstanceUID(), ir.getSeriesID(), ir.getModality());
						seriesEntitiesVector.add(newSeries);
						existingSeries.put(ir.getSeriesID(), newSeries);

						// Add series information to JSON
						JSONObject sej = new JSONObject();
						sej.put("UID", ir.getSeriesID());
						sej.put("MODALITY", ir.getModality());
						JSONArray seriesInstances = new JSONArray();
						sej.put("INSTANCES", seriesInstances);
						seriesJSON.put(ir.getSeriesID(), seriesInstances);
						studySeries.add(sej);
					}
					JSONArray seriesInstances = seriesJSON.get(ir.getSeriesID());
					Instance newInstance = new Instance(ir.getSeriesID(), ir.getSopIuid(), "".getBytes(Charset.forName("UTF-8")));
					instancesEntitiesVector.add(newInstance);
					JSONObject inst = new JSONObject();
					inst.put("UID", ir.getSopIuid());
					// inst.add(xml.getJSON());
					seriesInstances.add(inst);

				}
				newStudy.setJson(studySeries.toJSONString().getBytes(Charset.forName("UTF-8")));
				// Update the record
				studyEntitiesVector.add(newStudy);
/*				if (cacheImages) {
					createImageCache(sr.getStudyInstanceUID(), rsfp);
				}*/

			}
			// Update the database if all was success
			subjectBean.addSubject(newSubject, studyEntitiesVector, seriesEntitiesVector, instancesEntitiesVector);

		} catch (Exception exx) {
			syslog.log("PACSSSUBJECTSYNCHRONIZER:ADDPATIENTFROMPACS", "SubjectID:" + patientID + ". Failed. Exception:" + exx.getMessage());
			exx.printStackTrace();
			throw exx;
		}
	}

	private void createImageCache(String studyuid, RetriveStudyFromPACS rsfp) throws Exception {
		File dicomDir = new File(syncManager.getTempDir(), "dicomdownload" + Math.random());
		while (dicomDir.exists())
			dicomDir = new File(syncManager.getTempDir(), "dicomdownload" + Math.random());
		dicomDir.mkdirs();
		rsfp.setStudyid(studyuid);
		rsfp.retriveFilesTo(dicomDir.getAbsolutePath());
		CompressionManager comp = new CompressionManager();
		comp.addDirectory(studyuid + "/", dicomDir);
		studyBean.setStudyPACSData(studyuid, comp.compressToBytes());
		//Delete the files
		try {
			Files.walkFileTree(dicomDir.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					// try to delete the file anyway, even if its attributes
					// could not be read, since delete-only access is
					// theoretically possible
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed; propagate exception
						throw exc;
					}
				}
			});
		} catch (Exception exx) {
			syslog.log("PACSSSUBJECTSYNCHRONIZER:CREATEIMAGECACHE", "DIR:" + dicomDir.getAbsolutePath() + " Failed to delete." + exx.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void addNewInstances(String patientid, Vector<InstanceRecord> newInstances) throws Exception {
		/**
		 * The plan is to check for the active studies and instances from the
		 * list of new instances Note: DCMQR downloads all the instances related
		 * to a study Load the study record and process all active instances All
		 * beans are stored in memory and then processed through a single call
		 * to subjectBean Since this class is a POJO, a bean is required for JTA
		 */
		Vector<Study> studyEntitiesVector = new Vector<Study>();
		Vector<Series> seriesEntitiesVector = new Vector<Series>();
		Vector<Instance> instancesEntitiesVector = new Vector<Instance>();
		boolean cacheImages = syncManager.cachePACSImageData();
		RetriveStudyFromPACS rsfp = null;
		if (cacheImages)
			rsfp = new RetriveStudyFromPACS(syncManager.getPacsConnectionDetails());

		try {
			Vector<StudyRecord> studies1 = DCMAccessManager.getPatientStudies(patientid, -1);

			HashSet<String> activeStudies = new HashSet<String>();
			HashSet<String> activeInstances = new HashSet<String>();
			// Get the list of active studies and records
			for (InstanceRecord rec : newInstances) {
				activeStudies.add(rec.getStudyInstanceUID());
				activeInstances.add(rec.getSopIuid());
			}
			for (StudyRecord sr : studies1) {
				if (!activeStudies.contains(sr.getStudyInstanceUID()))
					continue;
				// If this study active
				Study newStudy = new Study(patientid, sr.getStudyInstanceUID(), sr.getStudyModalities(), sr.getStudyDate(), sr.getStudyDescription());

				Vector<InstanceRecord> allinstances = DCMAccessManager.getStudyInstances(sr.getStudyInstanceUID(), -1);
				Vector<InstanceRecord> instances = new Vector<InstanceRecord>();
				// Get the list of active instances
				for (InstanceRecord rec : allinstances) {
					if (activeInstances.contains(rec.getSopIuid())) {
						instances.add(rec);
					}
				}
				HashMap<String, Series> existingSeries = new HashMap<String, Series>();
				HashMap<String, JSONArray> seriesJSON = new HashMap<String, JSONArray>();

				JSONArray studySeries = new JSONArray();
				for (InstanceRecord ir : instances) {
					Series newSeries = existingSeries.get(ir.getSeriesID());
					if (newSeries == null) {
						newSeries = new Series(sr.getStudyInstanceUID(), ir.getSeriesID(), ir.getModality());

						seriesEntitiesVector.add(newSeries);
						existingSeries.put(ir.getSeriesID(), newSeries);

						// Add series information to JSON
						JSONObject sej = new JSONObject();
						sej.put("UID", ir.getSeriesID());
						sej.put("MODALITY", ir.getModality());
						JSONArray seriesInstances = new JSONArray();
						sej.put("INSTANCES", seriesInstances);
						seriesJSON.put(ir.getSeriesID(), seriesInstances);
						studySeries.add(sej);
					}
					JSONArray seriesInstances = seriesJSON.get(ir.getSeriesID());
					Instance newInstance = new Instance(ir.getSeriesID(), ir.getSopIuid(), "".getBytes(Charset.forName("UTF-8")));
					instancesEntitiesVector.add(newInstance);
					JSONObject inst = new JSONObject();
					inst.put("UID", ir.getSopIuid());
					// inst.add(xml.getJSON());
					seriesInstances.add(inst);
				}
				newStudy.setJson(studySeries.toJSONString().getBytes(Charset.forName("UTF-8")));
				// Update the record
				studyEntitiesVector.add(newStudy);
				if (cacheImages) {
					createImageCache(sr.getStudyInstanceUID(), rsfp);
				}
			}
			// Update the database if all was success
			subjectBean.addSubject(null, studyEntitiesVector, seriesEntitiesVector, instancesEntitiesVector);

		} catch (Exception exx) {
			exx.printStackTrace();
			syslog.log("PACSSSUBJECTSYNCHRONIZER:ADDNEWINSTANCES", "SubjectID:" + patientid + ". Failed. Exception:" + exx.getMessage());
			throw exx;
		}
	}

	private void consolidatePatient(String patientID) throws Exception {
		HashSet<String> ids = new HashSet<String>();
		HashMap<String, InstanceRecord> instanceMap = new HashMap<String, InstanceRecord>();
		Vector<StudyRecord> studies = DCMAccessManager.getPatientStudies(patientID, -1);
		for (StudyRecord sr : studies) {
			Vector<InstanceRecord> instances = DCMAccessManager.getStudyInstances(sr.getStudyInstanceUID(), -1);
			for (InstanceRecord rec : instances) {
				ids.add(rec.getSopIuid());
				instanceMap.put(rec.getSopIuid(), rec);
			}
		}
		String q = "SELECT p.instance_id from  InstanceView p  where p.subject_id = \"" + patientID + "\"";
		Query query = CAPDBManger.createNativeQuery(q);
		@SuppressWarnings("unchecked")
		List<String> dbids = query.getResultList();

		// Check if the patient has been deleted in PACS, if so delete from db
		if (studies.size() < 1) {
			if (dbids.size() > 0) {
				removePatient(patientID);
			}
		}
		// Check if the patient has been added to PACS, if so add to db
		else if (dbids.size() < 1) {
			addPatientFromPACS(patientID);
		} else {
			// Check for modifications
			ArrayList<String> deletedInstances = new ArrayList<String>();
			// Perform set subtraction
			deletedInstances.addAll(dbids);
			deletedInstances.removeAll(ids);
			if (deletedInstances.size() > 0) { // Remove instances
				for (String id : deletedInstances) {
					InstanceRecord ir = instanceMap.get(id);
					Instance oldInstance = new Instance(ir.getSeriesID(), ir.getSopIuid(), "".getBytes());
					instanceBean.removeInstance(oldInstance);
				}
			}
			ArrayList<String> addedInstances = new ArrayList<String>();
			addedInstances.addAll(ids);
			addedInstances.removeAll(dbids);

			if (addedInstances.size() > 0) { // Add new instances
				Vector<InstanceRecord> aInst = new Vector<InstanceRecord>();
				for (String id : addedInstances) {
					aInst.add(instanceMap.get(id));
				}
				addNewInstances(patientID, aInst);
			}
		}
	}

	@Override
	public void run() {
		try {
			consolidatePatient(id);
		} catch (Exception ex) {
			exx = ex;
		}
		// Required for the thread to progress
		syncManager.setCompletion();
	}

	public void start() {
		if (th == null) {
			th = new Thread(this);
			th.start();
		}
	}

	public void join() throws Exception {
		th.join();
	}

	public Exception getException() {
		return exx;
	}
}
