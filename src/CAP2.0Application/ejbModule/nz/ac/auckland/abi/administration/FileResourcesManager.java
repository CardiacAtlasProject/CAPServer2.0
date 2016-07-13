package nz.ac.auckland.abi.administration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.entities.CAPAdministration;
import nz.auckland.abi.archive.CompressionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Session Bean implementation class DownloadPackageManager
 */
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
@LocalBean
public class FileResourcesManager implements FileResourcesManagerRemote {
	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;
	
	@EJB
	SystemNotificationBean syslog;

	private String scratchSpace;
	private long cutoffTime;
	private long downloadTokenSize;

	private HashMap<String, File> downloadResourceMap;
	private HashMap<String, String> downloadNameMap;
	private ConcurrentHashMap<String, Long> resourceBirthTime;
	private HashMap<String, File> myResource;
	private HashMap<String, String> failedResource; // In case of resource
													// creation failures
													// information is stored
													// here
	private HashMap<String,Double> progress;		//Progress towards preparing the file
	private HashMap<String,Boolean> terminate;		//When the admin requests terminate
	private boolean gcrunning;
	private File tempDir;
	private CAPAdministration pc;
	private CAPAdministration SCRATCHDISKSPACEDB;
	private CAPAdministration MAXDOWNLOADRESOURCELIFEDB;
	private CAPAdministration MAXDOWNLOADTOKENSIZE;

	class Storage {
		public HashMap<String, File> downloadResourceMap;
		public HashMap<String, String> downloadNameMap;
		public ConcurrentHashMap<String, Long> resourceBirthTime; //Using concurrent as accessed by GC, Hashmap will cause ConcurrentAccessException 
		public HashMap<String, File> myResource;

		Storage(HashMap<String, File> rm, HashMap<String, String> nm, ConcurrentHashMap<String, Long> bt, HashMap<String, File> mr) {
			downloadResourceMap = rm;
			downloadNameMap = nm;
			resourceBirthTime = bt;
			myResource = mr;
		}

		Storage(byte[] json) throws Exception {
			JSONParser parser = new JSONParser();
			JSONArray master = (JSONArray) parser.parse(new String(json));
			JSONObject rm = (JSONObject) master.get(0);
			JSONObject nm = (JSONObject) master.get(1);
			JSONObject rbt = (JSONObject) master.get(2);
			JSONObject my = (JSONObject) master.get(3);
			downloadResourceMap = new HashMap<String, File>();
			downloadNameMap = new HashMap<String, String>();
			resourceBirthTime = new ConcurrentHashMap<String, Long>();
			myResource = new HashMap<String, File>();
			for (Object key : rm.keySet()) {
				String k = (String) key;
				String fn = (String) rm.get(key);
				File file = new File(fn);
				if (file.exists())
					downloadResourceMap.put(k, file);
			}
			for (Object key : nm.keySet()) {
				String k = (String) key;
				String fn = (String) nm.get(key);
				downloadNameMap.put(k, fn);
			}
			for (Object key : rbt.keySet()) {
				String k = (String) key;
				String fn = (String) rbt.get(key);
				resourceBirthTime.put(k, new Long(fn));
			}
			for (Object key : my.keySet()) {
				String k = (String) key;
				String fn = (String) my.get(key);
				File file = new File(fn);
				if (file.exists())
					myResource.put(k, file);
			}
		}

		@SuppressWarnings("unchecked")
		public byte[] serialize() {
			JSONArray master = new JSONArray();
			JSONObject rm = new JSONObject();
			for (String key : downloadResourceMap.keySet()) {
				rm.put(key, downloadResourceMap.get(key).getAbsolutePath());
			}
			JSONObject nm = new JSONObject();
			for (String key : downloadNameMap.keySet()) {
				nm.put(key, downloadNameMap.get(key));
			}
			JSONObject rbt = new JSONObject();
			for (String key : resourceBirthTime.keySet()) {
				rbt.put(key, "" + resourceBirthTime.get(key));
			}
			JSONObject my = new JSONObject();
			for (String key : myResource.keySet()) {
				my.put(key, myResource.get(key).getAbsolutePath());
			}
			master.add(rm);
			master.add(nm);
			master.add(rbt);
			master.add(my);
			return master.toJSONString().getBytes();
		}

	};

	/**
	 * Default constructor.
	 */
	public FileResourcesManager() {
		downloadResourceMap = new HashMap<String, File>();
		downloadNameMap = new HashMap<String, String>();
		resourceBirthTime = new ConcurrentHashMap<String, Long>();
		myResource = new HashMap<String, File>();
		failedResource = new HashMap<String, String>();
		progress = new HashMap<String, Double>();
		terminate = new HashMap<String, Boolean>();
		gcrunning = false;
	}

	@PostConstruct
	@Lock(LockType.WRITE)
	public void init() {
		SCRATCHDISKSPACEDB = entityManager.find(CAPAdministration.class, "SCRATCHDISKSPACE");
		scratchSpace = SCRATCHDISKSPACEDB.getValue();
		MAXDOWNLOADRESOURCELIFEDB = entityManager.find(CAPAdministration.class, "MAXDOWNLOADRESOURCELIFE");
		cutoffTime = Long.parseLong(MAXDOWNLOADRESOURCELIFEDB.getValue());
		MAXDOWNLOADTOKENSIZE = entityManager.find(CAPAdministration.class, "DOWNLOADTOKENSIZE");
		downloadTokenSize = Long.parseLong(MAXDOWNLOADTOKENSIZE.getValue());
		try {
			tempDir = new File(scratchSpace, "downloadsstage");
			if (!tempDir.exists())
				tempDir.mkdirs();
			// Load existing storage data
			pc = entityManager.find(CAPAdministration.class, this.getClass().getSimpleName());
			if (pc != null) {
				byte[] pcdata = pc.getData();
				if (pcdata == null)// Check if data is available
					return;
				Storage store = new Storage(pcdata);

				downloadNameMap = store.downloadNameMap;
				downloadResourceMap = store.downloadResourceMap;
				resourceBirthTime = store.resourceBirthTime;
				myResource = store.myResource;
				Thread checker = new Thread(new Runnable() {

					@Override
					public void run() {
						// Check if files exist
						for (String rid : downloadResourceMap.keySet()) {
							if (!downloadResourceMap.get(rid).exists()) {
								resourceBirthTime.remove(rid);
								myResource.remove(rid);
								for (String tar : downloadNameMap.keySet()) {
									if (downloadNameMap.get(tar).equalsIgnoreCase(rid)) {
										downloadNameMap.remove(tar);
										break;
									}
								}
							}
						}

					}
				});
				checker.start();
			} else {
				pc = new CAPAdministration(this.getClass().getSimpleName());
				entityManager.persist(pc);
			}
		} catch (Exception exx) {
			exx.printStackTrace();
			tempDir = new File(scratchSpace);
			syslog.log("FILERESOURCESMANAGER:INIT", "EXCEPTION:"+exx.getMessage());
		}
	}

	@Schedule(minute = "*/10", hour = "*", persistent = false)
	public void synchronizeDB() throws Exception {
		// Check if tempDir or cuttoff time has changed
		SCRATCHDISKSPACEDB = entityManager.find(CAPAdministration.class, "SCRATCHDISKSPACE");
		MAXDOWNLOADRESOURCELIFEDB = entityManager.find(CAPAdministration.class, "MAXDOWNLOADRESOURCELIFE");
		cutoffTime = Long.parseLong(MAXDOWNLOADRESOURCELIFEDB.getValue());
		MAXDOWNLOADTOKENSIZE = entityManager.find(CAPAdministration.class, "DOWNLOADTOKENSIZE");
		downloadTokenSize = Long.parseLong(MAXDOWNLOADTOKENSIZE.getValue());
		
		String space = SCRATCHDISKSPACEDB.getValue();
		if (!space.equals(scratchSpace)) {
			// Copy myresource to new directory
			File newDir = new File(space);
			if (!newDir.exists())
				newDir.mkdirs();
			pc.setData(null);
			int clen = tempDir.getAbsolutePath().length() + 1;
			ArrayList<String> keys = new ArrayList<String>(myResource.keySet());
			for (String rid : keys) {
				try {
					File file = myResource.get(rid);
					String stem = file.getAbsolutePath().substring(clen);
					File newFile = new File(newDir, stem);
					Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					downloadNameMap.remove(file.getAbsolutePath());
					downloadNameMap.put(newFile.getAbsolutePath(), rid);
					myResource.put(rid, newFile);
					downloadResourceMap.put(rid, newFile);
					file.delete();
				} catch (Exception exx) {
					exx.printStackTrace();
					syslog.log("FILERESOURCESMANAGER:SYNCHRONIZE", "EXCEPTION:"+exx.getMessage());
				}

			}
		}

	}

	@Lock(LockType.READ)
	public File getResourceLocation(String id) {
		return downloadResourceMap.get(id);
	}

	@Lock(LockType.READ)
	public String checkFailure(String id) {
		return failedResource.get(id);
	}

	@Lock(LockType.WRITE)
	public  String createResource(File target, long addLife) {
		// Check if the target already exists
		if (!downloadNameMap.containsKey(target.getAbsolutePath())) {
			String rid = "d" + Math.random();
			while (downloadResourceMap.containsKey(rid) || failedResource.containsKey(rid))
				rid = "d" + Math.random();
			downloadResourceMap.put(rid, target);
			downloadNameMap.put(target.getAbsolutePath(), rid);
			resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
			persist();
			return rid;
		} else {
			String rid = downloadNameMap.get(target.getAbsolutePath());
			resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
			return rid;
		}
	}

	@Lock(LockType.WRITE)
	public  String createCompressedResource(File target, long addLife) throws Exception {
		// Check if the target already exists
		if (!downloadNameMap.containsKey(target.getAbsolutePath())) {
			String rid = "d" + Math.random();
			while (downloadResourceMap.containsKey(rid) || failedResource.containsKey(rid))
				rid = "d" + Math.random();
			CompressionManager cman = new CompressionManager();
			cman.addDirectory("/", target);
			File compressedFile = new File(tempDir, target.getName() + ".tar.gz");
			cman.compressTo(compressedFile);
			downloadResourceMap.put(rid, compressedFile);
			downloadNameMap.put(target.getAbsolutePath(), rid);
			resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
			myResource.put(rid, compressedFile);
			persist();
			//Delete the target
			removeRecursive(target.toPath());
			return rid;
		} else {
			String rid = downloadNameMap.get(target.getAbsolutePath());
			resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
			return rid;
		}
	}

	@Lock(LockType.WRITE)
	public  String createResource(String rid, File target, long addLife) {
		downloadResourceMap.put(rid, target);
		downloadNameMap.put(target.getAbsolutePath(), rid);
		resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
		persist();
		return rid;
	}

	@Lock(LockType.WRITE)
	public  String createCompressedResource(String rid, File target, long addLife) throws Exception {
		CompressionManager cman = new CompressionManager();
		cman.addDirectory("/", target);
		File compressedFile = new File(tempDir, target.getName() + ".tar.gz");
		cman.compressTo(compressedFile);
		downloadResourceMap.put(rid, compressedFile);
		downloadNameMap.put(target.getAbsolutePath(), rid);
		resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
		myResource.put(rid, compressedFile);
		persist();
		//Delete the target
		removeRecursive(target.toPath());
		return rid;
	}

	@Lock(LockType.WRITE)
	public  String consumeResource(File target, long addLife) {
		// Check if the target already exists
		if (!downloadNameMap.containsKey(target.getAbsolutePath())) {
			String rid = "d" + Math.random();
			while (downloadResourceMap.containsKey(rid) || failedResource.containsKey(rid))
				rid = "d" + Math.random();
			downloadResourceMap.put(rid, target);
			downloadNameMap.put(target.getAbsolutePath(), rid);
			resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
			return rid;
		} else {
			String rid = downloadNameMap.get(target.getAbsolutePath());
			resourceBirthTime.put(rid, new Long(System.nanoTime() + addLife));
			return rid;
		}
	}

	@Lock(LockType.WRITE)
	public  void releaseResource(String rid) {
		if (downloadResourceMap.containsKey(rid)) {
			downloadNameMap.remove(downloadResourceMap.get(rid).getAbsolutePath());
			resourceBirthTime.remove(rid);
			downloadResourceMap.remove(rid);
		}
	}
	
	@Lock(LockType.WRITE)
	public  void releaseResource(File resource) {
		String rid = downloadNameMap.get(resource.getAbsolutePath()); 
		if (rid!=null) {
			progress.remove(rid);
			terminate.remove(rid);
			downloadNameMap.remove(resource.getAbsolutePath());
			resourceBirthTime.remove(rid);
			downloadResourceMap.remove(rid);
			if(myResource.containsKey(rid)){
				myResource.remove(rid);
				
			}
		}

	}

	@Lock(LockType.READ)
	public HashMap<String,Double> getActiveTasks(){
		return new HashMap<String, Double>(progress);
	}
	
	
	@Lock(LockType.WRITE)
	public void setResouceFailure(String rid, String error) {
		failedResource.put(rid, error);
	}

	private void persist() {
		try {
			if (pc == null)
				return;
			Storage store = new Storage(downloadResourceMap, downloadNameMap, resourceBirthTime, myResource);
			pc.setValue("" + System.currentTimeMillis());
			pc.setData(store.serialize());
			entityManager.merge(pc);
		} catch (Exception exx) {
			syslog.log("FILERESOURCESMANAGER:PERSIST", "EXCEPTION:"+exx.getMessage());
		}
	}

	// Garbage collector checks life of resources and deletes resources, runs
	// every 30 mins
	@Schedule(minute = "*/30", hour = "*", persistent = false)
	public void resourceGC() throws Exception {
		// Run as a separate thread to avoid timer errors
		if (!gcrunning) {
			gcrunning = true;
			Thread checker = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						long curTime = System.nanoTime();

						Iterator<String> it = resourceBirthTime.keySet().iterator();
						while(it.hasNext()){
							String rid = it.next();
							long rtime = curTime - resourceBirthTime.get(rid).longValue();
							if (rtime > cutoffTime) {
								File myfile = downloadResourceMap.get(rid);
								progress.remove(rid);
								terminate.remove(rid);
								if (myResource.containsKey(rid)) {
									try{
										removeRecursive(myfile.toPath());
									}catch(Exception exx){
										syslog.log("FILERESOURCESMANAGER:GC", "UNABLETODELETEFILE: FILE:"+myfile.getAbsolutePath()+"; EXCEPTION:"+exx.getMessage());
									}
									myResource.remove(rid);
								}
								resourceBirthTime.remove(rid);
								String target = null;
								for (String tar : downloadNameMap.keySet()) {
									if (downloadNameMap.get(tar).equalsIgnoreCase(rid)) {
										target = rid;
										break;
									}
								}
								if (target != null)
									downloadNameMap.remove(target);
								downloadResourceMap.remove(rid);
							}
						}
						
					} catch (Exception exx) {
						exx.printStackTrace();
						syslog.log("FILERESOURCESMANAGER:GC", "EXCEPTION:"+exx);
					}
					
					gcrunning = false;
				}
			});
			checker.start();
		}
	}
	
	@Lock(LockType.READ)
	public long getResourceLife(){
		return cutoffTime;
	}
	
	@Lock(LockType.READ)
	public long getDownloadTokenSize(){
		return downloadTokenSize;
	}
	
	
	@Lock(LockType.WRITE)
	public void setTerminate(String rid){
		terminate.put(rid, new Boolean(true));
	}
	
	@Lock(LockType.READ)
	public boolean getTerminate(String rid){
		Boolean val = terminate.get(rid);
		if(val!=null){
			return val.booleanValue();
		}else{
			return false;
		}
	}
	
	@Lock(LockType.WRITE)
	public void setProgress(String rid, double prog){
		//System.out.println(rid+"\t"+prog*100.0);
		if(prog>=1.0){//Remove if completed
			progress.remove(rid);
		}else
			progress.put(rid, new Double(prog));
	}
	
	
	@Lock(LockType.READ)
	public double getProgress(String rid){
		if(downloadResourceMap.containsKey(rid)){
			Double prog = progress.get(rid);
			if(prog!=null)
				return prog.doubleValue();
			else
				return 0.0;
		}
		return -1.0;
	}
	
	
	public static void removeRecursive(Path path) throws IOException
	{
	    Files.walkFileTree(path, new SimpleFileVisitor<Path>()
	    {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	                throws IOException
	        {
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
	        {
	            // try to delete the file anyway, even if its attributes
	            // could not be read, since delete-only access is
	            // theoretically possible
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
	        {
	            if (exc == null)
	            {
	                Files.delete(dir);
	                return FileVisitResult.CONTINUE;
	            }
	            else
	            {
	                // directory iteration failed; propagate exception
	                throw exc;
	            }
	        }
	    });
	}
}
