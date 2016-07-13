package nz.ac.auckland.abi.administration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import org.json.simple.JSONArray;

import nz.ac.auckland.abi.businesslogic.StudyBean;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.businesslogic.UserProvenanceBean;
import nz.ac.auckland.abi.entities.Study;
import nz.ac.auckland.abi.entities.StudyMetaData;
import nz.auckland.abi.archive.CompressionManager;

/**
 * Session Bean implementation class CAPStudyManager
 */
@Stateless
@LocalBean
public class CAPStudyManager {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	@EJB
	StudyBean studyBean;

	@EJB
	UserProvenanceBean provenance;

	@EJB
	SystemNotificationBean syslog;

	@EJB
	DownloadsManagerRemote downloadsManager;

	/**
	 * Default constructor.
	 */
	public CAPStudyManager() {

	}

	public void replaceStudyMetaData(String user, String studyUID, String descriptor, File resource) throws Exception {
		// Check if the study exists
		double filesize = 1;
		try {
			Study myStudy = studyBean.getStudyByUID(studyUID);
			if (myStudy != null) {
				List<StudyMetaData> metaData = studyBean.getStudyMetaData(studyUID, descriptor);
				int size = metaData.size();
				if (size < 2) {
					int resourceNameLength = resource.getAbsolutePath().length() + 1;
					// The resource points to the directory where the uploaded
					// files
					// are stored
					File[] myFiles = resource.listFiles();
					String filename = null;
					byte[] data = null;
					if (myFiles.length == 1 && myFiles[0].isFile()) {
						filename = myFiles[0].getName();
						data = Files.readAllBytes(myFiles[0].toPath());
					} else {// Compress the files
						CompressionManager comp = new CompressionManager();
						FindAllFiles fileLister = new FindAllFiles();
						Files.walkFileTree(resource.toPath(), fileLister);
						ArrayList<Path> targetFiles = fileLister.getFiles();
						for (Path p : targetFiles) {
							String fileleaf = p.getParent().toFile().getAbsolutePath().substring(resourceNameLength);
							comp.addFile(fileleaf, p.toFile());
						}
						filename = descriptor + ".tar.gz";
						data = comp.compressToBytes();
					}
					filesize = data.length;
					if (size == 1) {// Replace
						StudyMetaData sm = metaData.get(0);
						sm.setData(data);
						sm.setDescriptor(descriptor);
						sm.setFilename(filename);
						entityManager.merge(sm);
					} else {// Add
						StudyMetaData sm = new StudyMetaData();
						sm.setData(data);
						sm.setDescriptor(descriptor);
						sm.setFilename(filename);
						sm.setStudyID(studyUID);
						entityManager.persist(sm);
					}
					updateStudyMetaDataField(studyUID);
					entityManager.flush();

				} else {
					throw new Exception("Multiple files matching descriptor " + descriptor + " for study " + studyUID);
				}
			} else {
				throw new ConstraintViolationException("Study " + studyUID + " does not exist in the database", null);
			}
		} catch (Exception exx) {
			exx.printStackTrace();
		} finally {
			provenance.logUsage(user, "REPLACESTUDYMETADATA","Descriptor:" + descriptor, filesize);
		}
	}

	public void removeStudyMetaData(String user, String studyUID, String descriptor) throws Exception {
		provenance.logUsage(user, "REMOVESTUDYMETADATA","Descriptor:" + descriptor, 1.0);
		List<StudyMetaData> metaData = studyBean.getStudyMetaData(studyUID, descriptor);
		if (metaData.size() > 0) {
			for (StudyMetaData sm : metaData) {
				entityManager.remove(sm);
			}
			updateStudyMetaDataField(studyUID);
			entityManager.flush();
		}
	}

	public String getStudyMetaData(String user, String studyUID, String descriptor) throws Exception {
		provenance.logUsage(user, "GETSTUDYMETADATA","studyUID:" + studyUID + " DESCRIPTOR:" + descriptor, 1.0);
		List<StudyMetaData> metaData = studyBean.getStudyMetaData(studyUID, descriptor);
		if (metaData.size() > 0) {
			File resource = null;
			String rid = null;
			if (metaData.size() == 1) {
				StudyMetaData sm = metaData.get(0);
				Object[] cr = downloadsManager.createDownloadResource(sm.getFilename());
				resource = (File) cr[1];
				rid = (String) cr[0];
				FileOutputStream fos = new FileOutputStream(resource);
				fos.write(sm.getData());
				fos.close();
			} else {
				Object[] cr = downloadsManager.createDownloadResource("METADATA.tar.gz");
				resource = (File) cr[1];
				rid = (String) cr[0];
				CompressionManager comp = new CompressionManager();
				comp.setFileUser(user); //Name of the file owner
				for (StudyMetaData sm : metaData) {
					comp.addData(sm.getFilename(), sm.getData());
				}
				comp.compressTo(resource);
			}
			return rid;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void updateStudyMetaDataField(String studyUID) {
		Study myStudy = studyBean.getStudyByUID(studyUID);
		if (myStudy != null) {
			List<StudyMetaData> metaData = studyBean.getStudyMetaData(studyUID, null);
			JSONArray metaArray = new JSONArray();
			for (StudyMetaData sm : metaData) {
				metaArray.add(sm.getDescriptor());
			}
			if (metaArray.size() > 0) {
				myStudy.setStudyMetaData(metaArray.toJSONString().getBytes(Charset.forName("UTF-8")));
			} else {
				myStudy.setStudyMetaData(null);
			}
			entityManager.merge(myStudy);
			entityManager.flush();
		}
	}

	private static class FindAllFiles extends SimpleFileVisitor<Path> {

		private HashSet<Path> result;

		public FindAllFiles() {
			result = new HashSet<Path>();
		}

		public ArrayList<Path> getFiles() {
			return new ArrayList<Path>(result);
		}

		// the file or directory name.
		void find(Path file) {
			if (file.toFile().isFile()) {
				result.add(file);
			}
		}

		// Invoke the pattern matching
		// method on each file.
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			find(file);
			return FileVisitResult.CONTINUE;
		}

		// Invoke the pattern matching
		// method on each directory.
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			find(dir);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			return FileVisitResult.CONTINUE;
		}
	}

}
