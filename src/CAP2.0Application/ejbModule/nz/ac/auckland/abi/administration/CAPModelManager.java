package nz.ac.auckland.abi.administration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.businesslogic.ModelBean;
import nz.ac.auckland.abi.businesslogic.StudyBean;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.businesslogic.UserProvenanceBean;
import nz.ac.auckland.abi.entities.Model;
import nz.ac.auckland.abi.helper.CAP2XML;
import nz.auckland.abi.archive.CompressionManager;

/**
 * Session Bean implementation class CAPModelManager
 */
@Stateless
@LocalBean
public class CAPModelManager {
	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	@EJB
	ModelBean modelBean;

	@EJB
	StudyBean studyBean;

	@EJB
	UserProvenanceBean provenance;

	@EJB
	SystemNotificationBean syslog;

	@EJB
	DownloadsManagerRemote downloadsManager;

	@EJB
	PACSCAPDatabaseSynchronizerRemote sync;

	/**
	 * Default constructor.
	 */
	public CAPModelManager() {

	}

	public void updateComments(String user, String modelID, String comment) {
		provenance.logUsage(user, "UPDATEMODELCOMMENTS", "Target:" + modelID, 1.0);
		Model model = modelBean.getModel(modelID);
		syslog.log("UPDATEMODELCOMMENTS", "USER:" + user + ";MODEL:" + modelID + ";OLDCOMMENT:" + model.getModelComments());
		model.setModelComments(comment);
		entityManager.flush();
	}

	public Model addModel(String user, File directory) throws Exception {
		provenance.logUsage(user, "ADDMODEL", "Directory " + directory, 1.0);
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			File xmlFile = null;
			ArrayList<File> exnodes = new ArrayList<File>();
			ArrayList<File> vtp = new ArrayList<File>();
			ArrayList<File> meta = new ArrayList<File>();

			for (File f : files) {
				String filename = f.getName();
				// System.out.print("Checking file "+filename+" ");
				if ((filename.indexOf("exelem") > -1) || (filename.indexOf("exnode") > -1) || (filename.indexOf("exregion") > -1)) {
					exnodes.add(f);
				} else if (filename.indexOf("vtp") > -1) {
					vtp.add(f);
				} else if (filename.indexOf("xml") > -1) {
					xmlFile = f;
				} else if (f.isFile()) {// Only add other files ignore
										// directories
					meta.add(f);
				}
			}
			if (exnodes.size() > 0 && xmlFile != null) {
				CAP2XML xml = new CAP2XML(xmlFile);
				byte[] metData = null;
				byte[] exData = null;
				byte[] vtpData = null;
				// If there is metaData then compress and store
				if (meta.size() > 0) {

					CompressionManager comp = new CompressionManager();
					comp.addFileList("/", meta);

					metData = comp.compressToBytes();

				}

				{
					CompressionManager comp = new CompressionManager();
					comp.addFileList("/", exnodes);

					exData = comp.compressToBytes();
				}

				if (vtp.size() > 0) {

					CompressionManager comp = new CompressionManager();
					comp.addFileList("/", vtp);

					vtpData = comp.compressToBytes();

				}
				if (exData != null) {
					Model newModel = new Model();
					newModel.setStudyID(xml.getStudyUID().trim());
					newModel.setModelName(xml.getModelName());
					newModel.setModelComments(xml.toString());
					newModel.setModelXml(xml.getXmlBytes());
					newModel.setModelExArchive(exData);
					newModel.setModelVtpArchive(vtpData);
					newModel.setModelMetadata(metData);

					if (sync.constrainModelsToPACSStudies()) {
						// Check if the study associated with the model exists
						
						if (studyBean.getStudyByUID(newModel.getStudyID()) != null) {
							return modelBean.addModel(newModel);
						} else
							throw new Exception("Model addition failed, Study " + newModel.getStudyID() + " does not exist in the database");
					}else{
						return modelBean.addModel(newModel);
					}
				} else {
					throw new Exception("Model directory does not have exdata!!");
				}

			} else {
				throw new Exception("Model directory does not have FEM files!!");
			}
		} else {
			throw new Exception(directory.getName() + " is a file!!");
		}
	}

	public void removeModel(String user, String modelID) {
		provenance.logUsage(user, "REMOVEMODEL", "modelid" + modelID, 1.0);
		modelBean.removeModel(modelBean.getModel(modelID));
	}

	public void addModelMetaData(String user, String modelID, File resource) throws Exception {
		provenance.logUsage(user, "ADDMETADATATOMODEL", "modelid" + modelID, 1.0);
		Model myModel = modelBean.getModel(modelID);
		if (myModel != null) {
			byte[] metaData = myModel.getModelMetadata();
			CompressionManager comp = new CompressionManager();
			if (metaData != null) {
				comp.setMetaData(metaData);
				if (resource.isFile())
					comp.addFile("/", resource);
				else {
					ArrayList<File> files = new ArrayList<File>();
					for (File file : resource.listFiles()) {
						if (file.isFile())
							files.add(file);
					}
					comp.addFileList("/", files);
				}
				myModel.setModelMetadata(comp.recompressToBytes());
			} else {// Create new
				comp.addFile("/", resource);
				myModel.setModelMetadata(comp.compressToBytes());
			}
			entityManager.flush();
		}
	}

	public void replaceModelMetaData(String user, String modelID, File resource) throws Exception {
		provenance.logUsage(user, "REPLACEMODELMETADATA", "modelid" + modelID, 1.0);
		Model myModel = modelBean.getModel(modelID);
		if (myModel != null) {
			CompressionManager comp = new CompressionManager();
			if (resource.isFile())
				comp.addFile("/", resource);
			else {
				ArrayList<File> files = new ArrayList<File>();
				for (File file : resource.listFiles()) {
					if (file.isFile())
						files.add(file);
				}
				comp.addFileList("/", files);
			}
			myModel.setModelMetadata(comp.compressToBytes());
			entityManager.flush();
		}

	}

	public void removeModelMetaData(String user, String modelID) throws Exception {
		provenance.logUsage(user, "REMOVEMODELMETADATA", "modelid" + modelID, 1.0);
		Model myModel = modelBean.getModel(modelID);
		if (myModel != null) {
			myModel.setModelMetadata(null);
			entityManager.flush();
		}
	}

	public String getModelMetaData(String user, String modelID) throws Exception {
		provenance.logUsage(user, "GETMODELMETADATA", "modelid" + modelID, 1.0);
		Model myModel = modelBean.getModel(modelID);
		if (myModel != null) {
			byte[] metadata = myModel.getModelMetadata();
			if (metadata == null) {
				throw new NullPointerException(modelID + " does not have any metadata");
			}
			Object[] resource = downloadsManager.createDownloadResource("metadata.tar.gz");
			File td = (File) resource[1];
			FileOutputStream fos = new FileOutputStream(td);
			fos.write(metadata);
			fos.close();
			return (String) resource[0];
		}
		return null;
	}
}
