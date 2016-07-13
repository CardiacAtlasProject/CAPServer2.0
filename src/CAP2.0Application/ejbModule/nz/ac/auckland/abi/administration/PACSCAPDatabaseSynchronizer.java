package nz.ac.auckland.abi.administration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import nz.ac.auckland.abi.businesslogic.InstanceBean;
import nz.ac.auckland.abi.businesslogic.SeriesBean;
import nz.ac.auckland.abi.businesslogic.StudyBean;
import nz.ac.auckland.abi.businesslogic.SubjectBean;
import nz.ac.auckland.abi.businesslogic.SubjectViewBeanRemote;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.businesslogic.UserProvenanceBean;
import nz.ac.auckland.abi.dcm4chee.DCMAccessManager;
import nz.ac.auckland.abi.dcm4chee.InstanceRecord;
import nz.ac.auckland.abi.dcm4chee.PatientRecord;
import nz.ac.auckland.abi.entities.CAPAdministration;
import nz.ac.auckland.abi.entities.SystemNotification;
import nz.ac.auckland.abi.job.JobManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Singleton Bean implementation class PACSCAPDatabaseSynchronizer The class
 * instance is instantiated at the beginning of the EJB Module deployment
 */
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
@Startup
public class PACSCAPDatabaseSynchronizer implements SynchronizationManager, PACSCAPDatabaseSynchronizerRemote {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	@EJB
	private SubjectViewBeanRemote subjectViewBean;

	@EJB
	private SubjectBean subjectBean;

	@EJB
	private StudyBean studyBean;

	@EJB
	private SeriesBean seriesBean;

	@EJB
	private InstanceBean instanceBean;

	@EJB
	private JobManager jman;

	@EJB
	UserProvenanceBean provanence;

	@EJB
	SystemNotificationBean syslog;

	@EJB
	FileResourcesManager fileManager;

	private String tempDir;

	private String aetitle;

	private String hostname;
	private String port;
	private String caetitle;
	private String caetname;
	private String caeport;
	private String getModalities;
	private String pacsProtocol;
	private String wadoPort;
	private boolean cachePACSImageData = false;
	private long maximumStoredSubjectTableLifeTime;
	private long temporaryFileLifeTime;
	private int maxIdleTime;
	private boolean constrainModelsToPACSStudies = false;
	private int idleCounter;

	private long downloadTokenSize;

	// This flag is true when the synchronization process is in progress
	private boolean synchronizationInProgress = false;

	private Properties pacsAccessProperties;

	private ArrayList<String> sortedSubjectIDs; // List of subjets in PACS
	private int currentSubjectIndex;// The index of the subject currently being
									// synced

	private Logger log;

	/**
	 * Default constructor.
	 */
	public PACSCAPDatabaseSynchronizer() {
		log = Logger.getLogger(this.getClass().getSimpleName());
		currentSubjectIndex = 0;
		// Delay all sync processing for 5 minutes to allow the server to load
		// and perform other critical tasks
		idleCounter = 1;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	// All injected beans and values are available post the creation of the bean
	public void init() {
		synchronizationInProgress = true;// Ensure that sync doesnt start

		try {
			// Load configuration from db
			CAPAdministration aetDB = entityManager.find(CAPAdministration.class, "PACSAETITLE");
			aetitle = aetDB.getValue();
			CAPAdministration aetportDB = entityManager.find(CAPAdministration.class, "PACSPORT");
			port = aetportDB.getValue();
			CAPAdministration aethostDB = entityManager.find(CAPAdministration.class, "PACSHOSTNAME");
			hostname = aethostDB.getValue();
			CAPAdministration caetDB = entityManager.find(CAPAdministration.class, "CALLINGAETITLE");
			caetitle = caetDB.getValue();
			CAPAdministration caeportDB = entityManager.find(CAPAdministration.class, "CALLINGAEPORT");
			caeport = caeportDB.getValue();
			CAPAdministration caethostDB = entityManager.find(CAPAdministration.class, "CALLINGHOSTNAME");
			caetname = caethostDB.getValue();

			CAPAdministration capprotoDB = entityManager.find(CAPAdministration.class, "PACSDCMPROTOCOL");
			pacsProtocol = capprotoDB.getValue();
			CAPAdministration capwadoDB = entityManager.find(CAPAdministration.class, "PACSWADOPORT");
			wadoPort = capwadoDB.getValue();

			CAPAdministration CONSTRAINMODELPACSSDB = entityManager.find(CAPAdministration.class, "CONSTRAINMODELTOPACS");
			constrainModelsToPACSStudies = Boolean.parseBoolean(CONSTRAINMODELPACSSDB.getValue());

			CAPAdministration CACHEPACSIMAGEDATADB = entityManager.find(CAPAdministration.class, "CACHEPACSIMAGEDATA");
			cachePACSImageData = Boolean.parseBoolean(CACHEPACSIMAGEDATADB.getValue());

			CAPAdministration SCRATCHDISKSPACEDB = entityManager.find(CAPAdministration.class, "SCRATCHDISKSPACE");
			tempDir = SCRATCHDISKSPACEDB.getValue();
			CAPAdministration SYNCALLWITHPACSGAPDB = entityManager.find(CAPAdministration.class, "SYNCALLWITHPACSGAP");
			maximumStoredSubjectTableLifeTime = Long.parseLong(SYNCALLWITHPACSGAPDB.getValue());
			CAPAdministration MAXPACSSYNCDELAYDB = entityManager.find(CAPAdministration.class, "MAXPACSSYNCDELAY");
			maxIdleTime = Integer.parseInt(MAXPACSSYNCDELAYDB.getValue());

			CAPAdministration MAXDOWNLOADRESOURCELIFEDB = entityManager.find(CAPAdministration.class, "MAXDOWNLOADRESOURCELIFE");
			temporaryFileLifeTime = Long.parseLong(MAXDOWNLOADRESOURCELIFEDB.getValue());

			CAPAdministration MAXDOWNLOADTOKENSIZE = entityManager.find(CAPAdministration.class, "DOWNLOADTOKENSIZE");
			downloadTokenSize = Long.parseLong(MAXDOWNLOADTOKENSIZE.getValue());

			CAPAdministration GETMODALITIESDB = entityManager.find(CAPAdministration.class, "RETRIVEMODALITIES");
			getModalities = GETMODALITIESDB.getValue();

			pacsAccessProperties = new Properties();
			pacsAccessProperties.put("AET", aetitle);
			pacsAccessProperties.put("AETPort", port);
			pacsAccessProperties.put("AETHost", hostname);
			pacsAccessProperties.put("CAET", caetitle);
			pacsAccessProperties.put("CAETPort", caeport);
			pacsAccessProperties.put("CAETHost", caetname);
			if (getModalities != null)
				pacsAccessProperties.put("Modalities", getModalities);
			// Load sync data if available
			CAPAdministration lastSyncTime = entityManager.find(CAPAdministration.class, "LASTSYNCTIME");
			if (lastSyncTime != null) {
				// Get the subject list
				CAPAdministration lastPatient = entityManager.find(CAPAdministration.class, "IDOFLASTSYNCPATIENT");
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(lastSyncTime.getData()));
				sortedSubjectIDs = (ArrayList<String>) ois.readObject();
				currentSubjectIndex = Integer.parseInt(lastPatient.getValue());
				ois.close();
				log.log(Level.INFO, "sorted listed loaded from past sync check: Number of subjects " + sortedSubjectIDs.size());
				log.log(Level.INFO, "Last subject Index " + currentSubjectIndex);
			}
		} catch (Exception exx) {
			log.log(Level.INFO, exx.toString());
			syslog.log("PACSCAPDATABASESYNCHRONIZER:INIT", exx.getMessage());
			exx.printStackTrace();
		}

		synchronizationInProgress = false;
	}

	@Schedule(hour = "*/1", persistent = false)
	public void cleanup() throws Exception {
		File wDir = new File(tempDir);
		File[] tempFiles = wDir.listFiles();
		for (File file : tempFiles) {
			if (!file.getName().endsWith("downloadsstage")) {
				BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				long idleTime = System.currentTimeMillis() - attr.lastAccessTime().toMillis();
				if (idleTime > temporaryFileLifeTime) {
					try {
						// Remove all files including directories
						Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								Files.delete(file);
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
								// try to delete the file anyway, even if its
								// attributes
								// could not be read, since delete-only access
								// is
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
						log.log(Level.WARNING, "Unable to delete temporary file " + file.getAbsolutePath());
						syslog.log("PACSCAPDATABASESYNCHRONIZER:CLEANUP",
								"Unable to delete temporary file " + file.getAbsolutePath() + " Exception:" + exx.getMessage());
					}
				}
			}
		}
	}

	@Schedule(minute = "*/1", hour = "*", persistent = false)
	public void synchronizeWithPACS() throws Exception {
		if(maximumStoredSubjectTableLifeTime<0) //If no sync is requested maximumStoredSubjectTableLifeTime is negative
			return;
		if (idleCounter-- > 0)// If the idle counter is set then idle until it
								// is zero
			return;
		// Initiate a synchronization operation every minute if synchronization
		// is not already in progress
		if (!synchronizationInProgress) {
			CAPAdministration lastPatient = entityManager.find(CAPAdministration.class, "IDOFLASTSYNCPATIENT");
			CAPAdministration lastSyncTime = entityManager.find(CAPAdministration.class, "LASTSYNCTIME");

			synchronizationInProgress = true;// Reset by worker thread
			if (lastPatient == null) {
				lastPatient = new CAPAdministration("IDOFLASTSYNCPATIENT");
				entityManager.persist(lastPatient);
			}
			long currentTime = (long) (new Date().getTime());
			if (lastSyncTime != null) {
				currentTime -= Long.parseLong(lastSyncTime.getValue());
				// If time to refresh
				if (currentTime > maximumStoredSubjectTableLifeTime || sortedSubjectIDs == null) {
					log.log(Level.INFO, "Synching all PACS subjects conditions Time : " + (currentTime > maximumStoredSubjectTableLifeTime) + " list : "
							+ (sortedSubjectIDs == null));
					syslog.log("PACSCAPDATABASESYNCHRONIZER:SYNCHRONIZEWITHPACS", "Synching all PACS subjects conditions Time : "
							+ (currentTime > maximumStoredSubjectTableLifeTime) + " list : " + (sortedSubjectIDs == null));
					Vector<PatientRecord> patients = DCMAccessManager.getPatients(-1);
					sortedSubjectIDs = new ArrayList<String>();
					for (PatientRecord rec : patients) {
						sortedSubjectIDs.add(rec.getPatientID());
					}

					Collections.sort(sortedSubjectIDs);
					currentSubjectIndex = 0;
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					oos.writeObject(sortedSubjectIDs);
					oos.close();
					bos.close();
					lastSyncTime.setData(bos.toByteArray());
					lastPatient.setValue("0");
				}
				// If a refresh is completed within an hour (a static database),
				// check if subjects have changed
				if (currentTime < 1000 * 60 * maxIdleTime && currentSubjectIndex > sortedSubjectIDs.size() - 1) {
					Vector<InstanceRecord> instances = DCMAccessManager.getStudyInstances(null, Long.parseLong(lastSyncTime.getValue()));
					if (instances.size() == 0) {// No change
						idleCounter = maxIdleTime;
						return;
					} else {
						// Load the new patients
						HashSet<String> subjects = new HashSet<String>();
						for (InstanceRecord rec : instances) {
							subjects.add(rec.getSubjectID());
						}
						sortedSubjectIDs = new ArrayList<String>(subjects);
						Collections.sort(sortedSubjectIDs);
						currentSubjectIndex = 0;
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(bos);
						oos.writeObject(sortedSubjectIDs);
						oos.close();
						bos.close();
						lastSyncTime.setData(bos.toByteArray());
						lastPatient.setValue("0");
					}
				}
			} else {
				lastSyncTime = new CAPAdministration("LASTSYNCTIME");
				lastSyncTime.setValue("" + currentTime);
				if (sortedSubjectIDs == null) {
					Vector<PatientRecord> patients = DCMAccessManager.getPatients(-1);
					sortedSubjectIDs = new ArrayList<String>();
					for (PatientRecord rec : patients) {
						sortedSubjectIDs.add(rec.getPatientID());
					}

					Collections.sort(sortedSubjectIDs);
					currentSubjectIndex = 0;
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					oos.writeObject(sortedSubjectIDs);
					oos.close();
					bos.close();
					lastSyncTime.setData(bos.toByteArray());
					lastPatient.setValue("0");
				}
				entityManager.persist(lastSyncTime);
			}

			String sid = null;
			try {
				sid = sortedSubjectIDs.get(currentSubjectIndex++);
				PACSSubjectSynchornizer psync = new PACSSubjectSynchornizer(this, sid);
				log.log(Level.INFO, "Initiating synchronization of subject " + sid);
				psync.start();
				// Set the last patients ID
				lastSyncTime.setValue("" + (long) (new Date().getTime()));
				lastPatient.setValue("" + (currentSubjectIndex - 1));
			} catch (Exception exx) {
				log.log(Level.INFO, exx.toString());
				syslog.log("PACSCAPDATABASESYNCHRONIZER:SYNCHRONIZEWITHPACS", "synchronization of subject " + sid + " failed. Exception: " + exx.getMessage());
			}
			entityManager.persist(lastSyncTime);
			entityManager.persist(lastPatient);
		}
	}

	@Asynchronous
	public void recordLogin(String user){
		
	}
	
	public void consolidateSubject(String pid) {
		PACSSubjectSynchornizer psync = new PACSSubjectSynchornizer(this, pid);
		log.log(Level.INFO, "Initiating synchronization of subject " + pid);
		psync.start();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Lock(LockType.READ)
	public SubjectBean getSubjectBean() {
		return subjectBean;
	}

	@Lock(LockType.READ)
	public StudyBean getStudyBean() {
		return studyBean;
	}

	@Lock(LockType.READ)
	public SeriesBean getSeriesBean() {
		return seriesBean;
	}

	@Lock(LockType.READ)
	public InstanceBean getInstanceBean() {
		return instanceBean;
	}

	@Lock(LockType.READ)
	public SystemNotificationBean getSystemNotificationBean() {
		return syslog;
	}

	public void setCompletion() {
		synchronizationInProgress = false;
	}

	@Lock(LockType.READ)
	public Properties getPacsConnectionDetails() {
		return pacsAccessProperties;
	}

	@Lock(LockType.READ)
	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETTEMPDIR", "Value changed from " + this.tempDir + " to " + tempDir);
		this.tempDir = tempDir;
		CAPAdministration SCRATCHDISKSPACEDB = entityManager.find(CAPAdministration.class, "SCRATCHDISKSPACE");
		SCRATCHDISKSPACEDB.setValue(tempDir);
		entityManager.merge(SCRATCHDISKSPACEDB);
		entityManager.flush();
	}

	@Lock(LockType.READ)
	public String getAetitle() {
		return aetitle;
	}

	public void setAetitle(String aetitle) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETAETITLE", "Value changed from " + this.aetitle + " to " + aetitle);
		this.aetitle = aetitle;
		CAPAdministration aetDB = entityManager.find(CAPAdministration.class, "PACSAETITLE");
		aetDB.setValue(aetitle);
		entityManager.merge(aetDB);
		entityManager.flush();
		pacsAccessProperties.put("AET", aetitle);
	}

	@Lock(LockType.READ)
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETHOSTNAME", "Value changed from " + this.hostname + " to " + hostname);
		this.hostname = hostname;
		CAPAdministration aethostDB = entityManager.find(CAPAdministration.class, "PACSHOSTNAME");
		aethostDB.setValue(hostname);
		entityManager.merge(aethostDB);
		entityManager.flush();
		pacsAccessProperties.put("AETHost", hostname);
	}

	@Lock(LockType.READ)
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETPORT", "Value changed from " + this.port + " to " + port);
		this.port = port;
		CAPAdministration aetportDB = entityManager.find(CAPAdministration.class, "PACSPORT");
		aetportDB.setValue(port);
		entityManager.merge(aetportDB);
		entityManager.flush();
		pacsAccessProperties.put("AETPort", port);
	}

	@Lock(LockType.READ)
	public String getCaetitle() {
		return caetitle;
	}

	public void setCaetitle(String caetitle) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETCAETITLE", "Value changed from " + this.caetitle + " to " + caetitle);
		this.caetitle = caetitle;
		CAPAdministration caetDB = entityManager.find(CAPAdministration.class, "CALLINGAETITLE");
		caetDB.setValue(caetname);
		entityManager.merge(caetDB);
		entityManager.flush();
		pacsAccessProperties.put("CAET", caetitle);
	}

	@Lock(LockType.READ)
	public String getCaetname() {
		return caetname;
	}

	public void setCaetname(String caetname) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETCAETNAME", "Value changed from " + this.caetname + " to " + caetname);
		this.caetname = caetname;
		CAPAdministration caethostDB = entityManager.find(CAPAdministration.class, "CALLINGHOSTNAME");
		caethostDB.setValue(caetname);
		entityManager.merge(caethostDB);
		entityManager.flush();
		pacsAccessProperties.put("CAETHost", caetname);
	}

	@Lock(LockType.READ)
	public String getCaeport() {
		return caeport;
	}

	public void setCaeport(String caeport) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETCAEPORT", "Value changed from " + this.caeport + " to " + caeport);
		this.caeport = caeport;
		CAPAdministration caeportDB = entityManager.find(CAPAdministration.class, "CALLINGAEPORT");
		caeportDB.setValue(caeport);
		entityManager.merge(caeportDB);
		entityManager.flush();
		pacsAccessProperties.put("CAETPort", caeport);
	}

	@Lock(LockType.READ)
	public String getPacsProtocol() {
		return pacsProtocol;
	}

	public void setPacsProtocol(String pacsProtocol) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETPACSPROTOCOL", "Value changed from " + this.pacsProtocol + " to " + pacsProtocol);
		this.pacsProtocol = pacsProtocol;
		CAPAdministration capprotoDB = entityManager.find(CAPAdministration.class, "PACSDCMPROTOCOL");
		capprotoDB.setValue(pacsProtocol);
		entityManager.merge(capprotoDB);
		entityManager.flush();
	}

	@Lock(LockType.READ)
	public String getWadoPort() {
		return wadoPort;
	}

	public void setWadoPort(String wadoPort) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETWADOPORT", "Value changed from " + this.wadoPort + " to " + wadoPort);
		this.wadoPort = wadoPort;
		CAPAdministration capwadoDB = entityManager.find(CAPAdministration.class, "PACSWADOPORT");
		capwadoDB.setValue(wadoPort);
		entityManager.merge(capwadoDB);
		entityManager.flush();
	}

	public boolean cachePACSImageData() {
		return cachePACSImageData;
	}

	public void setCachePACSImageData(boolean cachePACSImageData) {
		if (this.cachePACSImageData != cachePACSImageData) {
			syslog.log("PACSCAPDATABASESYNCHRONIZER:SETCACHEPACSIMAGEDATA", "Value changed from " + this.cachePACSImageData + " to " + cachePACSImageData);
			this.cachePACSImageData = cachePACSImageData;
			CAPAdministration CACHEPACSIMAGEDATADB = entityManager.find(CAPAdministration.class, "CACHEPACSIMAGEDATA");
			CACHEPACSIMAGEDATADB.setValue("" + cachePACSImageData);
			entityManager.merge(CACHEPACSIMAGEDATADB);
			entityManager.flush();
		}
	}

	@Lock(LockType.READ)
	public long getMaximumStoredSubjectTableLifeTime() {
		return maximumStoredSubjectTableLifeTime;
	}

	public void setMaximumStoredSubjectTableLifeTime(long maximumStoredSubjectTableLifeTime) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETMAXIMUMSTOREDSUBJECTTABLELIFETIME", "Value changed from " + this.maximumStoredSubjectTableLifeTime + " to "
				+ maximumStoredSubjectTableLifeTime);
		this.maximumStoredSubjectTableLifeTime = maximumStoredSubjectTableLifeTime;
		CAPAdministration SYNCALLWITHPACSGAPDB = entityManager.find(CAPAdministration.class, "SYNCALLWITHPACSGAP");
		SYNCALLWITHPACSGAPDB.setValue("" + maximumStoredSubjectTableLifeTime);
		entityManager.merge(SYNCALLWITHPACSGAPDB);
		entityManager.flush();
	}

	@Lock(LockType.READ)
	public long getTemporaryFileLifeTime() {
		return temporaryFileLifeTime;
	}

	public void setTemporaryFileLifeTime(long temporaryFileLifeTime) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETTEMPORARYFILELIFETIME", "Value changed from " + this.temporaryFileLifeTime + " to " + temporaryFileLifeTime);
		this.temporaryFileLifeTime = temporaryFileLifeTime;
		CAPAdministration MAXDOWNLOADRESOURCELIFEDB = entityManager.find(CAPAdministration.class, "MAXDOWNLOADRESOURCELIFE");
		MAXDOWNLOADRESOURCELIFEDB.setValue("" + temporaryFileLifeTime);
		entityManager.merge(MAXDOWNLOADRESOURCELIFEDB);
		entityManager.flush();
	}

	@Lock(LockType.READ)
	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETMAXIDLETIME", "Value changed from " + this.maxIdleTime + " to " + maxIdleTime);
		this.maxIdleTime = maxIdleTime;
		CAPAdministration MAXPACSSYNCDELAYDB = entityManager.find(CAPAdministration.class, "MAXPACSSYNCDELAY");
		MAXPACSSYNCDELAYDB.setValue("" + maxIdleTime);
		entityManager.merge(MAXPACSSYNCDELAYDB);
		entityManager.flush();
	}

	public boolean constrainModelsToPACSStudies() {
		return constrainModelsToPACSStudies;
	}

	public void setConstrainModelsToPACSStudies(boolean constrainModelsToPACSStudies) {
		syslog.log("PACSCAPDATABASESYNCHRONIZER:SETCONSTRAINMODELSTOPACSSTUDIES", "Value changed from " + this.constrainModelsToPACSStudies + " to "
				+ constrainModelsToPACSStudies);
		this.constrainModelsToPACSStudies = constrainModelsToPACSStudies;
		CAPAdministration CONSTRAINMODELPACSSDB = entityManager.find(CAPAdministration.class, "CONSTRAINMODELTOPACS");
		CONSTRAINMODELPACSSDB.setValue("" + constrainModelsToPACSStudies);
		entityManager.merge(CONSTRAINMODELPACSSDB);
		entityManager.flush();

	}

	@Lock(LockType.READ)
	public String getModalities() {
		return getModalities;
	}

	public void setModalities(String getModalities) {
		CAPAdministration GETMODALITIESDB = entityManager.find(CAPAdministration.class, "RETRIVEMODALITIES");
		GETMODALITIESDB.setValue(getModalities);
		entityManager.merge(GETMODALITIESDB);
		entityManager.flush();
		this.getModalities = getModalities;
	}

	@Lock(LockType.READ)
	public long getDownloadTokenSize() {
		return downloadTokenSize;
	}

	public void setDownloadTokenSize(long downloadTokenSize) {
		CAPAdministration MAXDOWNLOADTOKENSIZE = entityManager.find(CAPAdministration.class, "DOWNLOADTOKENSIZE");
		MAXDOWNLOADTOKENSIZE.setValue("" + downloadTokenSize);
		entityManager.merge(MAXDOWNLOADTOKENSIZE);
		entityManager.flush();
		this.downloadTokenSize = downloadTokenSize;
	}

	@Lock(LockType.READ)
	public Properties getPacsAccessProperties() {
		return pacsAccessProperties;
	}

	public void setPacsAccessProperties(Properties pacsAccessProperties) {
		this.pacsAccessProperties = pacsAccessProperties;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getActiveTasks(String actor, HashMap<String, String> queryParameters) {
		JSONObject result = new JSONObject();
		JSONArray activity = new JSONArray();
		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String draw = queryParameters.get("draw");
		String searchTerm = queryParameters.get("sSearch");

		result.put("start", sStart);
		result.put("count", sAmount);
		int start = 0;
		int amount = 0;
		try {
			start = Integer.parseInt(sStart);
		} catch (Exception exx) {

		}
		try {
			amount = Integer.parseInt(sAmount);
			if (amount < 0 || amount > 100)
				amount = 10;
		} catch (Exception exx) {

		}
		HashMap<String, Double> tasks = fileManager.getActiveTasks();
		String myKeys[] = new String[tasks.size()];
		tasks.keySet().toArray(myKeys);
		Arrays.sort(myKeys);
		ArrayList<String> matching = new ArrayList<String>();
		if (searchTerm != null && searchTerm.trim().length()>0) {
			for (String s : myKeys) {
				if (s.matches("*" + searchTerm + "*")) {
					matching.add(s);
				}
			}
		} else {
			matching.addAll(Arrays.asList(myKeys));
		}
		int total = tasks.size();
		if (start > matching.size()) {
			start = 0;
		}
		if (amount > matching.size()) {
			amount = matching.size();
		}
		for (int i = 0; i < amount; i++) {
			JSONObject obj = new JSONObject();
			obj.put("DT_RowId", "" + i);
			obj.put("RESOURCE", matching.get(i));
			obj.put("PROGRESS", "" + (tasks.get(matching.get(i)).doubleValue() * 100.0));
			activity.add(obj);
		}

		result.put("recordsFiltered", matching.size());
		result.put("recordsTotal", total);
		result.put("data", activity);
		// Based on https://datatables.net/manual/server-side //access date
		// Jan 2015

		if (draw != null)
			result.put("draw", draw);
		return result;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getSystemLog(String actor, HashMap<String, String> queryParameters) {
		provanence.logUsage(actor, "CAPSYSTEMACTIVITYQUERY", null, 1);
		JSONObject result = new JSONObject();
		JSONArray activity = new JSONArray();
		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String draw = queryParameters.get("draw");
		String searchTerm = queryParameters.get("sSearch");
		String sdir = queryParameters.get("orderdirection");
		String dir = "desc";
		if (sdir != null) {
			if (sdir.equals("asc"))
				dir = "asc";
		}
		String orderby = queryParameters.get("orderby");

		if (orderby == null)
			orderby = "time";
		else {
			if (orderby.equalsIgnoreCase("0")) {
				orderby = "event";
			} else if (orderby.equalsIgnoreCase("1")) {
				orderby = "message";
			} else {
				orderby = "time";
			}
		}
		result.put("start", sStart);
		result.put("count", sAmount);
		int start = 0;
		int amount = 0;
		try {
			start = Integer.parseInt(sStart);
		} catch (Exception exx) {

		}
		try {
			amount = Integer.parseInt(sAmount);
			if (amount < 0 || amount > 100)
				amount = 10;
		} catch (Exception exx) {

		}

		try {
			// Get the total number of studyrecords
			String q = "SELECT count(*) from  Notifications";
			List<java.math.BigInteger> pctr = entityManager.createNativeQuery(q).getResultList();

			// Determine the list of patients, let this be total
			int total = pctr.get(0).intValue();
			String query = null;
			if (searchTerm != null)
				query = "SELECT * FROM Notifications p where (p.event like '%" + searchTerm + "%' or p.message like '%" + searchTerm + "%' or p.time like '%"
						+ searchTerm + "%') order by " + orderby + " " + dir;
			else
				query = "SELECT * FROM Notifications order by " + orderby + " " + dir;
			Query myq = entityManager.createNativeQuery(query, SystemNotification.class);
			myq.setFirstResult(start);
			myq.setMaxResults(amount);
			List<SystemNotification> results = myq.getResultList();
			for (SystemNotification cl : results) {
				JSONObject obj = new JSONObject();
				obj.put("DT_RowId", cl.getId());
				obj.put("EVENT", cl.getEvent());
				obj.put("MESSAGE", "" + cl.getMessage());
				Date myDate = cl.getTime();
				if (myDate != null)
					obj.put("TIME", "" + myDate);
				else
					obj.put("TIME", "0");
				activity.add(obj);
			}
			result.put("recordsFiltered", results.size());
			result.put("recordsTotal", total);
			result.put("data", activity);
			// Based on https://datatables.net/manual/server-side //access date
			// Jan 2015

			if (draw != null)
				result.put("draw", draw);
			// System.out.println("User activity Log "+result.toJSONString());
		} catch (Exception exx) {
			exx.printStackTrace();
			result.put("error", exx.toString());
		}
		return result;
	}

	public void purgeSystemLog(String actor, HashMap<String, String> queryParameters) throws Exception {
		provanence.logUsage(actor, "PURGECAPSYSTEMLOG", "USERID:" + actor + "; RECORDS:" + queryParameters.get("select"), 1);
		JSONParser parser = new JSONParser();

		try {
			JSONArray arr = (JSONArray) parser.parse(queryParameters.get("select"));
			int asize = arr.size();
			if (asize > 0) {
				StringBuffer buf = new StringBuffer();
				buf.append("(");
				for (int i = 0; i < asize - 1; i++) {
					buf.append(Long.parseLong((String) arr.get(i)) + ",");
				}
				buf.append(Long.parseLong((String) arr.get(asize - 1)) + ")");
				entityManager.createNativeQuery("DELETE FROM Notifications WHERE id in " + buf.toString()).executeUpdate();
				entityManager.flush();
			}
		} catch (Exception exx) {
			exx.printStackTrace();
			throw exx;
		} finally {
			syslog.log("PURGE", "System log was purged by " + actor);
		}

	}

	public void purgeTask(String user, String rid) {
		provanence.logUsage(user, "PURGETASK", "ResouceID:" + rid, 1.0);
		fileManager.setTerminate(rid);
	}

	public JSONObject getActivitySummary(String user, HashMap<String, String> queryParameters) {
		provanence.logUsage(user, "USERACTIVITYSUMMARY", "", 1.0);
		return provanence.getActivitySummary(queryParameters);
	}

}
