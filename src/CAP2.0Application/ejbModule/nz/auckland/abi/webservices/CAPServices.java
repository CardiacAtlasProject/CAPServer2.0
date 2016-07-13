package nz.auckland.abi.webservices;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.ws.soap.MTOM;

import nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.businesslogic.UserProvenanceBean;
import nz.ac.auckland.abi.dcm4chee.RetriveStudyFromPACS;
import nz.ac.auckland.abi.entities.Model;
import nz.ac.auckland.abi.entities.Series;
import nz.ac.auckland.abi.entities.Study;
import nz.ac.auckland.abi.entities.StudyMetaData;
import nz.ac.auckland.abi.entities.StudyPACSData;
import nz.ac.auckland.abi.entities.Subject;
import nz.auckland.abi.archive.CompressionManager;

/**
 * Session Bean implementation class CAPServices Following
 * https://docs.jboss.org/author/display/JBWS/Advanced+User+Guide
 */
@Stateless
@WebService(name="CAPWebservices",targetNamespace="www.cardiacatlasproject.org")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT,use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
@MTOM(enabled=true,threshold=10240)
public class CAPServices{

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	@EJB
	UserProvenanceBean provenance;

	@EJB
	PACSCAPDatabaseSynchronizerRemote sync;

	@EJB
	SystemNotificationBean syslog;

	/**
	 * Default constructor.
	 */
	public CAPServices() {

	}

	/**
	 * Attempts to login by checking the user against the pass, if login fails a
	 * security exception is thrown
	 * 
	 * @param user
	 *            - CAP userid
	 * @param pass - base64 encode string
	 * @param method
	 *            - Method called
	 * @param desc
	 *            - Usage description
	 * @param quantity
	 *            - bytes used
	 * @throws Exception
	 */
	private void logUsage(String user, String pass, String method, String desc, double quantity) throws Exception {
		provenance.logUsage(user, pass, method, desc, quantity);
		sync.recordLogin(user);
	}

	/**
	 * Get the list of subjects matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return a list of matching subjects
	 * @throws Exception
	 */
	public CAPSubject[] getSubjects(String user, String pass, CAPSubject search, boolean match) throws Exception {
		logUsage(user, pass, "WSGETSUBJECTS", search + " match=" + match, 1.0);
		StringBuffer buf = new StringBuffer();
		boolean andq = false;
		if (match) {
			if (search.id != null) {
				buf.append("p.id = '" + search.id + "' ");
				andq = true;
			}
			if (search.name != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.name = '" + search.name + "' ");
				andq = true;
			}
			if (search.gender != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.gender = '" + search.gender + "' ");
				andq = true;
			}
			if (search.birthdate != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.birthdate = '%" + search.birthdate + "%' ");
				andq = true;
			}
		} else {
			if (search.id != null) {
				buf.append("p.id like '%" + search.id + "' ");
				andq = true;
			}
			if (search.name != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.name like '%" + search.name + "%' ");
				andq = true;
			}
			if (search.gender != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.gender like '%" + search.gender + "%' ");
				andq = true;
			}
			if (search.birthdate != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.birthdate like '%" + search.birthdate + "%'");
				andq = true;
			}
		}
		String query = "FROM Subject p ";
		if (buf.length() > 0) {
			query = query + " where " + buf.toString() + " order by id";
		}
		List<Subject> result = entityManager.createQuery(query, Subject.class).getResultList();
		if(result.isEmpty())
			return null;
		CAPSubject subjects[] = new CAPSubject[result.size()];
		int ctr = 0;
		for (Subject s : result) {
			CAPSubject sj = new CAPSubject();
			sj.id = s.getId();
			sj.name = s.getName();
			sj.birthdate = s.getBirthdate();
			sj.gender = s.getGender();
			subjects[ctr++] = sj;
		}

		return subjects;
	}

	/**
	 * 
	 /** Get the list of studies matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param lessthan
	 *            - If not null, studies before this date are chosen
	 * @param greaterthan
	 *            - If not null, studies after this date are chosen
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return a list of matching subjects
	 * @throws Exception
	 */
	public CAPStudy[] getStudies(String user, String pass, CAPStudy search, Date lessthan, Date greaterthan, boolean match) throws Exception {
		logUsage(user, pass, "WSGETSTUDIES", search + " match=" + match + " less than=" + lessthan + " greater than " + greaterthan, 1.0);
		StringBuffer buf = new StringBuffer();
		boolean andq = false;
		if (match) {
			if (search.studyID != null) {
				buf.append("p.studyID = '" + search.studyID + "' ");
				andq = true;
			}
			if (search.subjectID != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.id = '" + search.subjectID + "' ");
				andq = true;
			}
			if (andq)
				buf.append(" and ");
			if (lessthan != null && greaterthan != null) {
				buf.append("( p.studyDate < '" + lessthan + "' and p.studyDate > '" + greaterthan + "')");
				andq = true;
			} else if (lessthan != null && greaterthan == null) {
				buf.append(" p.studyDate < '" + lessthan + "' ");
				andq = true;
			} else if (lessthan == null && greaterthan != null) {
				buf.append(" p.studyDate > '" + greaterthan + "')");
				andq = true;
			} else {
				buf.append("p.studyDate = '" + search.studyDate + "' ");
				andq = true;
			}

			if (search.studyModalities != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.studyModalities = '" + search.studyModalities + "' ");
				andq = true;
			}
			if (search.studyDescription != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.studyDescription = '" + search.studyDescription + "' ");
				andq = true;
			}
		} else {
			if (search.studyID != null) {
				buf.append("p.studyID like '%" + search.studyID + "%' ");
				andq = true;
			}
			if (search.subjectID != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.id like '%" + search.subjectID + "%' ");
				andq = true;
			}
			if (andq)
				buf.append(" and ");
			if (lessthan != null && greaterthan != null) {
				buf.append("( p.studyDate < '" + lessthan + "' and p.studyDate > '" + greaterthan + "')");
				andq = true;
			} else if (lessthan != null && greaterthan == null) {
				buf.append(" p.studyDate < '" + lessthan + "' ");
				andq = true;
			} else if (lessthan == null && greaterthan != null) {
				buf.append(" p.studyDate > '" + greaterthan + "')");
				andq = true;
			} else {
				buf.append("p.studyDate like '%" + search.studyDate + "%' ");
				andq = true;
			}
			if (search.studyModalities != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.studyModalities like '%" + search.studyModalities + "%' ");
				andq = true;
			}
			if (search.studyDescription != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.studyDescription like '%" + search.studyDescription + "%' ");
				andq = true;
			}
		}

		String query = "FROM Study p ";
		if (buf.length() > 0) {
			query = query + " where " + buf.toString() + " order by studyID";
		}
		List<Study> result = entityManager.createQuery(query, Study.class).getResultList();
		if(result.isEmpty())
			return null;
		CAPStudy[] studies = new CAPStudy[result.size()];
		int ctr = 0;
		for (Study s : result) {
			CAPStudy sj = new CAPStudy();
			sj.subjectID = s.getSubject();
			sj.studyID = s.getStudyID();
			sj.studyDate = new Date(s.getStudyDate().getTime());
			sj.studyModalities = s.getStudyModalities();
			sj.studyDescription = s.getStudyDescription();
			studies[ctr++]= sj;
		}

		return studies;
	}

	/**
	 * 
	 * Get the list of series matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return a list of matching series
	 */
	public CAPSeries[] getSeries(String user, String pass, CAPSeries search, boolean match) throws Exception {
		logUsage(user, pass, "WSGETSERIES", search + " match=" + match, 1.0);
		StringBuffer buf = new StringBuffer();
		boolean andq = false;
		if (match) {
			if (search.studyID != null) {
				buf.append("p.studyID = '" + search.studyID + "' ");
				andq = true;
			}
			if (search.seriesID != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.seriesID = '" + search.seriesID + "' ");
				andq = true;
			}
			if (search.modality != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.modality = '" + search.modality + "' ");
				andq = true;
			}
		} else {
			if (search.studyID != null) {
				buf.append("p.studyID like '%" + search.studyID + "%' ");
				andq = true;
			}
			if (search.seriesID != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.seriesID like '%" + search.seriesID + "%' ");
				andq = true;
			}
			if (search.modality != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.modality like '%" + search.modality + "%' ");
				andq = true;
			}
		}
		String query = "FROM Series p ";
		if (buf.length() > 0) {
			query = query + " where " + buf.toString() + " order by seriesID";
		}
		List<Series> result = entityManager.createQuery(query, Series.class).getResultList();
		if(result.isEmpty())
			return null;
		CAPSeries[] series = new CAPSeries[result.size()];
		int ctr = 0;
		for (Series s : result) {
			CAPSeries sj = new CAPSeries();
			sj.seriesID = s.getSeriesID();
			sj.studyID = s.getStudyID();
			sj.modality = s.getModality();
			series[ctr++]= sj;
		}

		return series;
	}

	/**
	 * 
	 * Get the list of models matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return a list of matching Models
	 * @throws Exception
	 */
	public CAPModel[] getModel(String user,String pass, CAPModel search, boolean match) throws Exception {
		logUsage(user, pass,"WSGETMODEL", search + " match=" + match, 1.0);
		StringBuffer buf = new StringBuffer();
		boolean andq = false;
		if (match) {
			if (search.modelID != null) {
				buf.append("p.modelID = '" + search.modelID + "' ");
				andq = true;
			}
			if (search.studyID != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.studyID = '" + search.studyID + "' ");
				andq = true;
			}
			if (search.modelName != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.modelName = '" + search.modelName + "' ");
				andq = true;
			}
			if (search.modelComments != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.modelComments = '" + search.modelComments + "' ");
				andq = true;
			}
		} else {
			if (search.modelID != null) {
				buf.append("p.modelID like '%" + search.modelID + "%' ");
				andq = true;
			}
			if (search.studyID != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.studyID like '%" + search.studyID + "%' ");
				andq = true;
			}
			if (search.modelName != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.modelName like '%" + search.modelName + "%' ");
				andq = true;
			}
			if (search.modelComments != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.modelComments like '%" + search.modelComments + "%' ");
				andq = true;
			}
		}

		String query = "FROM Model p ";
		if (buf.length() > 0) {
			query = query + " where " + buf.toString() + " order by modelID";
		}
		List<Model> result = entityManager.createQuery(query, Model.class).getResultList();
		if(result.isEmpty())
			return null;
		CAPModel[] models = new CAPModel[result.size()];
		int ctr = 0;
		for (Model s : result) {
			CAPModel sj = new CAPModel();
			sj.modelID = s.getModelID();
			sj.modelName = s.getModelName();
			sj.studyID = s.getStudyID();
			sj.modelComments = s.getModelComments();
			models[ctr++] = sj;
		}

		return models;
	}

	/**
	 * 
	 * Get the list of meta data associated with a study and matching 'search'
	 * terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return a list of matching subjects
	 * @throws Exception
	 */
	public CAPStudyMetaData[] getStudyMetaData(String user, String pass, CAPStudyMetaData search, boolean match) throws Exception {
		logUsage(user, pass, "WSGETSTUDYMETADATA", search + " match=" + match, 1.0);
		StringBuffer buf = new StringBuffer();
		boolean andq = false;
		if (match) {
			if (search.studyID != null) {
				buf.append("p.studyID = '" + search.studyID + "' ");
				andq = true;
			}
			if (search.descriptor != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.descriptor = '" + search.descriptor + "' ");
				andq = true;
			}
			if (search.filename != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.filename = '" + search.filename + "%' ");
				andq = true;
			}
		} else {
			if (search.studyID != null) {
				buf.append("p.studyID like '%" + search.studyID + "%' ");
				andq = true;
			}
			if (search.descriptor != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.descriptor like '%" + search.descriptor + "%' ");
				andq = true;
			}
			if (search.filename != null) {
				if (andq)
					buf.append(" and ");
				buf.append("p.filename like '%" + search.filename + "%' ");
				andq = true;
			}
		}
		String query = "FROM StudyMetaData p ";
		if (buf.length() > 0) {
			query = query + " where " + buf.toString() + " order by seriesID";
		}
		List<StudyMetaData> result = entityManager.createQuery(query, StudyMetaData.class).getResultList();
		if(result.isEmpty())
			return null;
		CAPStudyMetaData[] metaData = new CAPStudyMetaData[result.size()];
		int ctr = 0;
		for (StudyMetaData study : result) {
			CAPStudyMetaData sj = new CAPStudyMetaData();
			sj.studyID = study.getStudyID();
			sj.descriptor = study.getDescriptor();
			sj.filename = study.getFilename();
			metaData[ctr++]= sj;
		}
		return metaData;
	}

	/**
	 * Get the archive of DICOM instance data for study matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return tar.gz archive with a filename
	 * @throws Exception
	 */
	public CAPData getStudyInstancesArchive(String user,String pass, CAPStudy search) throws Exception {
		if (search.studyID != null) {
			List<StudyPACSData> studyData = entityManager.createQuery("FROM StudyPACSData where studyID='" + search.studyID + "'", StudyPACSData.class)
					.getResultList();
			if (!studyData.isEmpty()) {
				StudyPACSData data = studyData.get(0);
				CAPData result = new CAPData();
				result.dataType = search.studyID + ".tar.gz";
				result.data = data.getStudyPACSData();
				logUsage(user, pass, "WSGETSTUDYINSTANCESARCHIVE", search.toString(), result.data.length * 1.0);
				return result;
			} else {// Fetch
				File dicomDir = new File(sync.getTempDir(), search.studyID);
				RetriveStudyFromPACS rsfp = new RetriveStudyFromPACS(sync.getPacsAccessProperties());
				rsfp.setStudyid(search.studyID);
				rsfp.retriveFilesTo(dicomDir.getAbsolutePath());
				// Load the data into cache
				CompressionManager comp = new CompressionManager();
				comp.addDirectory(search.studyID + "/", dicomDir);
				CAPData result = new CAPData();
				result.dataType = search.studyID + ".tar.gz";
				result.data = comp.compressToBytes();
				logUsage(user, pass, "WSGETSTUDYINSTANCESARCHIVE", search.toString(), result.data.length * 1.0);
				// Clean up
				try {
					Files.walkFileTree(dicomDir.toPath(), new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
							// try to delete the file anyway, even if its
							// attributes
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
								// directory iteration failed; propagate
								// exception
								throw exc;
							}
						}
					});
				} catch (Exception exx) {
					syslog.log("TERMINATECLEANUP", "Failed to remove directory " + dicomDir.getAbsolutePath() + "; EXCEPTION:" + exx.getMessage());
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * Get the archive of EX data for model matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return tar.gz archive with a filename
	 * @throws Exception
	 */
	public CAPData getModelEXFiles(String user, String pass,CAPModel model) throws Exception {
		if (model.modelID != null) {
			List<Model> models = entityManager.createQuery("FROM Model where modelID='" + model.modelID + "'", Model.class).getResultList();
			if (!models.isEmpty()) {
				CAPData data = new CAPData();
				Model mod = models.get(0);
				data.dataType = mod.getModelName() + ".EX.tar.gz";
				data.data = mod.getModelExArchive();
				logUsage(user, pass, "WSGETMODELEXFILES", model.toString(), data.data.length * 1.0);
				return data;
			}
		}
		return null;
	}

	/**
	 * Get the archive of VTP data for model matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return tar.gz archive with a filename
	 * @throws Exception
	 */
	public CAPData getModelVTPFiles(String user, String pass,CAPModel model) throws Exception {
		if (model.modelID != null) {
			List<Model> models = entityManager.createQuery("FROM Model where modelID='" + model.modelID + "'", Model.class).getResultList();
			if (!models.isEmpty()) {
				CAPData data = new CAPData();
				Model mod = models.get(0);
				data.dataType = mod.getModelName() + ".VTP.tar.gz";
				data.data = mod.getModelVtpArchive();
				logUsage(user, pass, "WSGETMODELVTPFILES", model.toString(), data.data.length * 1.0);
				return data;
			}
		}
		return null;
	}

	/**
	 * Get the CAP XML for model matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return xml filename as bytes
	 * @throws Exception
	 */
	public CAPData getModelXML(String user, String pass, CAPModel model) throws Exception {
		if (model.modelID != null) {
			List<Model> models = entityManager.createQuery("FROM Model where modelID='" + model.modelID + "'", Model.class).getResultList();
			if (!models.isEmpty()) {
				CAPData data = new CAPData();
				Model mod = models.get(0);
				data.dataType = mod.getModelName() + ".xml";
				data.data = mod.getModelXml();
				logUsage(user, pass,"WSGETMODELXML", model.toString(), data.data.length * 1.0);
				return data;
			}
		}
		return null;
	}

	/**
	 * Get the archive of model meta data for model matching 'search' terms
	 * 
	 * @param user
	 *            - CAP userid
	 * 
	 * @param search
	 *            - An object with search terms, null terms are not searched,
	 *            others are anded
	 * @param match
	 *            - Should the terms be matched or should a regularexpression be
	 *            used
	 * @return tar.gz archive with a filename
	 * @throws Exception
	 */
	public CAPData getModelMetaData(String user, String pass, CAPModel model) throws Exception {
		if (model.modelID != null) {
			List<Model> models = entityManager.createQuery("FROM Model where modelID='" + model.modelID + "'", Model.class).getResultList();
			if (!models.isEmpty()) {
				CAPData data = new CAPData();
				Model mod = models.get(0);
				data.dataType = mod.getModelName() + ".meta.tar.gz";
				data.data = mod.getModelMetadata();
				logUsage(user, pass, "WSGETMODELMETADATA", model.toString(), data.data.length * 1.0);
				return data;
			}
		}
		return null;
	}
}
