package nz.ac.auckland.abi.administration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import nz.ac.auckland.abi.businesslogic.ModelBean;
import nz.ac.auckland.abi.businesslogic.StudyBean;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.businesslogic.UserProvenanceBean;
import nz.ac.auckland.abi.dcm4chee.RetriveStudyFromPACS;
import nz.ac.auckland.abi.entities.Model;
import nz.ac.auckland.abi.entities.StudyMetaData;
import nz.ac.auckland.abi.entities.StudyPACSData;
import nz.auckland.abi.archive.CompressionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Session Bean implementation class DownloadsManager
 */
@Stateless
@LocalBean
public class DownloadsManager implements DownloadsManagerRemote {
	private String scratchSpace;

	private Properties pacsAccessProperties;

	@EJB
	PACSCAPDatabaseSynchronizerRemote sync;

	@EJB
	FileResourcesManager packageManger;

	@EJB
	ModelBean modelBean;

	@EJB
	StudyBean studyBean;

	@EJB
	UserProvenanceBean provenance;

	@EJB
	SystemNotificationBean syslog;

	/**
	 * Default constructor.
	 */
	public DownloadsManager() {

	}

	@PostConstruct
	// All injected beans and values are available post the creation of the bean
	public void init() {

	}

	public File getResourceLocation(String id) {
		return packageManger.getResourceLocation(id);
	}

	public String getFailedResourceMessage(String id) {
		return packageManger.checkFailure(id);
	}

	public String getImageData(String user, ArrayList<String> studyinstanuids) throws Exception {
		return getData(user, studyinstanuids, null);
	}

	public String getModelData(String user, ArrayList<String> modelids) throws Exception {
		return getData(user, null, modelids);
	}

	public Object[] createDownloadResource(String filename) {
		scratchSpace = sync.getTempDir();
		File tempDir = new File(scratchSpace, "querydata" + Math.random());
		while (tempDir.exists())
			tempDir = new File(scratchSpace, "querydata" + Math.random());
		tempDir.mkdirs();
		File myFile = new File(tempDir, filename);
		// Get a resouce id
		String rid = packageManger.consumeResource(myFile, 0);
		Object[] resources = new Object[2];
		resources[0] = rid;
		resources[1] = myFile;
		return resources;
	}

	public String getData(String user, ArrayList<String> studyinstanuids, ArrayList<String> modelids) throws Exception {
		pacsAccessProperties = new Properties(sync.getPacsAccessProperties());
		scratchSpace = sync.getTempDir();
		File tempDir = new File(scratchSpace, "querydata" + Math.random());
		while (tempDir.exists())
			tempDir = new File(scratchSpace, "querydata" + Math.random());
		// Get a resouce id
		String rid = packageManger.consumeResource(tempDir, 0);
		Thread resourcegen = new Thread(new ResourceCreator(user, packageManger, pacsAccessProperties, studyBean, modelBean, sync.cachePACSImageData(), rid, tempDir, studyinstanuids, modelids,
				provenance, syslog));
		resourcegen.start();
		return rid;
	}

	private class ResourceCreator implements Runnable {
		RetriveStudyFromPACS rsfp;
		ArrayList<String> studyinstanuids;
		ArrayList<String> modelids;
		String userID;
		String rid;
		File td;
		boolean useCache;
		FileResourcesManager manager;
		ModelBean modelBean;
		StudyBean studyBean;
		UserProvenanceBean provenance;
		SystemNotificationBean syslog;

		public ResourceCreator(String user, FileResourcesManager pman, Properties pacs, StudyBean sb, ModelBean be, boolean cache, String rid, File dir,
				ArrayList<String> sids, ArrayList<String> mids, UserProvenanceBean up, SystemNotificationBean sys) {
			rsfp = new RetriveStudyFromPACS(pacs);
			studyinstanuids = sids;
			modelids = mids;
			this.rid = rid;
			td = dir;
			studyBean = sb;
			manager = pman;
			modelBean = be;
			userID = user;
			provenance = up;
			syslog = sys;
			useCache = cache;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			if (studyinstanuids == null && modelids == null) {
				manager.releaseResource(rid);
				manager.setResouceFailure(rid, "No study or model id information provided");
				return;
			}
			try {
				JSONParser parser = new JSONParser();
				// Determine workitems
				int numberOfTasks = 0;
				double tasksCompleted = 0;
				StringBuffer uq = new StringBuffer();
				packageManger.setProgress(rid, 0.0);
				final int DICOMTASKBIAS = 10;
				if (studyinstanuids != null) {
					for (String studyjson : studyinstanuids) {
						JSONObject obj = (JSONObject) parser.parse(studyjson);
						uq.append("STUDY:" + obj.get("UID"));
						if (obj.containsKey("DICOM")) {
							numberOfTasks += DICOMTASKBIAS;
							uq.append(":DICOM");
						}
						if (obj.containsKey("META")) {
							numberOfTasks++;
							uq.append(":DICOMMETADATA");
						}
						if (obj.containsKey("EX")) {
							numberOfTasks++;
							uq.append(":EX");
						}
						if (obj.containsKey("VTP")) {
							numberOfTasks++;
							uq.append(":VTP");
						}
						if (obj.containsKey("MODELMETA")) {
							numberOfTasks++;
							uq.append(":MODELMETADATA");
						}
					}
				}
				if (modelids != null) {
					for (String modeljson : modelids) {
						JSONObject obj = (JSONObject) parser.parse(modeljson);
						uq.append("MODEL" + obj.get("ID"));
						if (obj.containsKey("EX")) {
							numberOfTasks++;
							uq.append(":EX");
						}
						if (obj.containsKey("VTP")) {
							numberOfTasks++;
							uq.append(":VTP");
						}
						if (obj.containsKey("MODELMETA")) {
							numberOfTasks++;
							uq.append(":MODELMETADATA");
						}
						if (obj.containsKey("DICOM")) {
							numberOfTasks += DICOMTASKBIAS;
							uq.append(":DICOM");
						}
						if (obj.containsKey("META")) {
							numberOfTasks++;
							uq.append(":DICOMMETA");
						}
					}
				}

				// Create an appropriate temporary directory and download
				// all
				// the necessary instance DICOM
				if (!td.exists())
					td.mkdirs();
				if (studyinstanuids != null) {
					for (String studyjson : studyinstanuids) {
						JSONObject obj = (JSONObject) parser.parse(studyjson);
						boolean dicom = obj.containsKey("DICOM");
						boolean metaData = obj.containsKey("META");
						boolean exData = obj.containsKey("EX");
						boolean vtpData = obj.containsKey("VTP");
						boolean modelmetaData = obj.containsKey("MODELMETA");
						String studyUID = (String) obj.get("UID");
						if (dicom) {
							if (!useCache) {
								File dicomDir = new File(td, studyUID);
								rsfp.setStudyid(studyUID);
								rsfp.retriveFilesTo(dicomDir.getAbsolutePath());
							}else{
								StudyPACSData data = studyBean.getStudyPACSData(studyUID);
								if(data!=null)
									CompressionManager.uncompressArchive(data.getStudyPACSData(), td);
								else{
									File dicomDir = new File(td, studyUID);
									rsfp.setStudyid(studyUID);
									rsfp.retriveFilesTo(dicomDir.getAbsolutePath());
									//Load the data into cache
									CompressionManager comp = new CompressionManager();
									comp.addDirectory(studyUID + "/", dicomDir);
									studyBean.setStudyPACSData(studyUID, comp.compressToBytes());
								}
							}
							tasksCompleted += DICOMTASKBIAS;
							packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
							if (packageManger.getTerminate(rid)) {
								cleanUp();
								return;
							}
						}
						if (metaData) {
							String metaDatakey = (String) obj.get("STUDYMETA");
							List<StudyMetaData> data = studyBean.getStudyMetaData(studyUID, metaDatakey);
							if (data.size() > 0) {
								File metaroot = new File(td, "STUDYMETADATA");
								if(!metaroot.exists())
									metaroot.mkdirs();
								File meta = new File(metaroot, studyUID);
								for (StudyMetaData sm : data) {
									File exfile = new File(meta, sm.getFilename());
									exfile.mkdirs();
									ByteArrayInputStream is = new ByteArrayInputStream(sm.getData());
									Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
								}
							}
							tasksCompleted += 1.0;
							packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
							if (packageManger.getTerminate(rid)) {
								cleanUp();
								return;
							}
						}
						if (exData || vtpData || modelmetaData) {
							// Get the models associated with the study
							List<Model> studyModels = modelBean.getModels(studyUID);
							if (studyModels.size() > 0) {
								File modelDir = new File(td, "models");

								for (Model model : studyModels) {
									File myData = new File(modelDir, model.getModelName());
									myData.mkdirs();
									if (exData) {
/*										File exfile = new File(myData, "EXFILES.tar.gz");
										ByteArrayInputStream is = new ByteArrayInputStream(model.getModelExArchive());
										Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
										File exfile = new File(myData, "EXFILES");
										CompressionManager.uncompressArchive(model.getModelExArchive(), exfile);
									}
									if (vtpData && model.getModelVtpArchive() != null) {
/*										File exfile = new File(myData, "VTPFILES.tar.gz");
										ByteArrayInputStream is = new ByteArrayInputStream(model.getModelVtpArchive());
										Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
										File exfile = new File(myData, "VTPFILES");
										CompressionManager.uncompressArchive(model.getModelVtpArchive(), exfile);
									}
									if (metaData && model.getModelMetadata() != null) {
/*										File exfile = new File(myData, "METADATA.tar.gz");
										ByteArrayInputStream is = new ByteArrayInputStream(model.getModelMetadata());
										Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
										File exfile = new File(myData, "METADATA");
										CompressionManager.uncompressArchive(model.getModelMetadata(), exfile);
									}
									if (exData || vtpData) {
										File xml = new File(myData, model.getModelName() + ".xml");
										ByteArrayInputStream is = new ByteArrayInputStream(model.getModelXml());
										Files.copy(is, xml.toPath(), StandardCopyOption.REPLACE_EXISTING);
									}
								}

								if (packageManger.getTerminate(rid)) {
									cleanUp();
									return;
								}
							}
							if (exData)
								tasksCompleted += 1.0;
							if (vtpData)
								tasksCompleted += 1.0;
							if (modelmetaData)
								tasksCompleted += 1.0;
							packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
							if (packageManger.getTerminate(rid)) {
								cleanUp();
								return;
							}
						}
					}
				}
				if (modelids != null) {
					if (modelids.size() > 0) {
						//File modelDir = new File(td, "models");
						File modelDir = td;
						for (String modeljson : modelids) {
							JSONObject obj = (JSONObject) parser.parse(modeljson);
							boolean exData = obj.containsKey("EX");
							boolean vtpData = obj.containsKey("VTP");
							boolean modelmetaData = obj.containsKey("MODELMETA");
							boolean dicom = obj.containsKey("DICOM");
							boolean metaData = obj.containsKey("META");
							String modelID = (String) obj.get("ID");
							Model model = modelBean.getModel(modelID);
							File myData = new File(modelDir, model.getModelName());
							myData.mkdirs();
							if (exData) {
/*								File exfile = new File(myData, "EXFILES.tar.gz");
								ByteArrayInputStream is = new ByteArrayInputStream(model.getModelExArchive());
								Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
								File exfile = new File(myData, "EXFILES");
								CompressionManager.uncompressArchive(model.getModelExArchive(), exfile);
								tasksCompleted += 1.0;
								packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
								if (packageManger.getTerminate(rid)) {
									cleanUp();
									return;
								}
							}
							if (vtpData && model.getModelVtpArchive() != null) {
/*								File exfile = new File(myData, "VTPFILES.tar.gz");
								ByteArrayInputStream is = new ByteArrayInputStream(model.getModelVtpArchive());
								Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
								File exfile = new File(myData, "VTPFILES");
								CompressionManager.uncompressArchive(model.getModelVtpArchive(), exfile);
								tasksCompleted += 1.0;
								packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
								if (packageManger.getTerminate(rid)) {
									cleanUp();
									return;
								}
							}
							if (modelmetaData && model.getModelMetadata() != null) {
/*								File exfile = new File(myData, "METADATA.tar.gz");
								ByteArrayInputStream is = new ByteArrayInputStream(model.getModelMetadata());
								Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
								File exfile = new File(myData, "METADATA");
								CompressionManager.uncompressArchive(model.getModelMetadata(), exfile);
								tasksCompleted += 1.0;
								packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
								if (packageManger.getTerminate(rid)) {
									cleanUp();
									return;
								}
							}
							if (dicom) {
								String studyUID = model.getStudyID();
								if (!useCache) {
									File dicomDir = new File(td, studyUID);
									rsfp.setStudyid(studyUID);
									rsfp.retriveFilesTo(dicomDir.getAbsolutePath());
								}else{
									StudyPACSData data = studyBean.getStudyPACSData(studyUID);
									if(data!=null)
										CompressionManager.uncompressArchive(data.getStudyPACSData(), td);
									else{
										File dicomDir = new File(td, studyUID);
										rsfp.setStudyid(studyUID);
										rsfp.retriveFilesTo(dicomDir.getAbsolutePath());
										//Load the data into cache
										CompressionManager comp = new CompressionManager();
										comp.addDirectory(studyUID + "/", dicomDir);
										studyBean.setStudyPACSData(studyUID, comp.compressToBytes());
									}
								}
								tasksCompleted += DICOMTASKBIAS;
								packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
								if (packageManger.getTerminate(rid)) {
									cleanUp();
									return;
								}
							}
							if (metaData) {
								String metaDatakey = (String) obj.get("STUDYMETA");
								List<StudyMetaData> data = studyBean.getStudyMetaData(model.getStudyID(), metaDatakey);
								if (data.size() > 0) {
									File metaroot = new File(td, "STUDYMETADATA");
									if(!metaroot.exists())
										metaroot.mkdirs();
									File meta = new File(metaroot, model.getStudyID());
									for (StudyMetaData sm : data) {
										File exfile = new File(meta, sm.getFilename());
										exfile.mkdirs();
										ByteArrayInputStream is = new ByteArrayInputStream(sm.getData());
										Files.copy(is, exfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
									}
								}
								tasksCompleted += 1.0;
								packageManger.setProgress(rid, tasksCompleted / numberOfTasks);
								if (packageManger.getTerminate(rid)) {
									cleanUp();
									return;
								}
							}
							if (exData || vtpData) {
								File xml = new File(myData, model.getModelName() + ".xml");
								ByteArrayInputStream is = new ByteArrayInputStream(model.getModelXml());
								Files.copy(is, xml.toPath(), StandardCopyOption.REPLACE_EXISTING);
							}
						}
					}
				}
				packageManger.createCompressedResource(rid, td, 0);
				long fileSize = 0;
				// Get the size
				{
					final AtomicLong size = new AtomicLong(0);
					Path path = td.toPath();

					Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							size.addAndGet(attrs.size());
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
							// Skip folders that can't be traversed
							// System.out.println("skipped: " + file + "e=" +
							// exc);

							return FileVisitResult.CONTINUE;
						}
					});

					fileSize = size.get();
				}
				packageManger.setProgress(rid, 1.0);
				provenance.logUsage(userID, "GETDATA", uq.toString(), fileSize);
			} catch (Exception exx) {
				manager.releaseResource(rid);
				manager.setResouceFailure(rid, exx.toString());
				exx.printStackTrace();
				JSONObject obj = new JSONObject();

				if (studyinstanuids != null) {
					JSONArray array = new JSONArray();
					for (String studyjson : studyinstanuids) {
						array.add(studyjson);
					}
					obj.put("STUDY", array);
				}
				if (modelids != null) {
					JSONArray array = new JSONArray();
					for (String modeljson : modelids) {
						array.add(modeljson);
					}
					obj.put("MODEL", array);
				}
				obj.put("EXCEPTION", exx.getMessage());
				syslog.log("GETDATA", obj.toJSONString());
			}

		}

		public void cleanUp() {
			try {
				manager.releaseResource(rid);
				manager.setResouceFailure(rid, "TERMINATED BY ADMIN");
				manager.setProgress(rid, 1.0);
				Files.walkFileTree(td.toPath(), new SimpleFileVisitor<Path>() {
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
				syslog.log("TERMINATECLEANUP", "Failed to remove directory " + td.getAbsolutePath() + "; EXCEPTION:" + exx.getMessage());
			}
		}

	}

}
