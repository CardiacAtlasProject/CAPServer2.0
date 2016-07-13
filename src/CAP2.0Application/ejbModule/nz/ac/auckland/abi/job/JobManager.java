package nz.ac.auckland.abi.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import nz.ac.auckland.abi.administration.CAPModelManager;
import nz.ac.auckland.abi.administration.CAPStudyManager;
import nz.ac.auckland.abi.businesslogic.ModelBean;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.entities.Model;

/**
 * Session Bean implementation class JobManager
 */
@Singleton
@LocalBean
public class JobManager implements JobManagerRemote {

	@EJB
	CAPModelManager modelManger;
	
	@EJB
	CAPStudyManager studyManager;

	@EJB
	SystemNotificationBean sysLog;
	
	@EJB
	ModelBean modelBean;

	Logger log;

	/**
	 * Default constructor.
	 */
	public JobManager() {
		log = Logger.getLogger(this.getClass().getSimpleName());
	}

	public void deleteModels(String user, ArrayList<String> modelids) throws Exception{
		if (modelids.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String s : modelids) {
				sb.append(s);
				sb.append(", ");
			}
			try {
				for (String id : modelids) {
					modelManger.removeModel(user, id);
				}
			} catch (Exception exx) {
				throw exx;
			}
		} else {
			throw new Exception("No model id(s) provided");
		}
	}

	
	
	// Get the list of sub directories with xml files and add them
	/**
	 * Method to add FEM models from a file system, the method enforces atomic transaction
	 * i.e. if there are multiple models in the target directory, it will add al of them
	 * if any one could not be added to the DB, none are added
	 * 
	 * @param userID
	 *            of the user calling the method
	 * @param directory
	 *            locally accessible directory
	 */
	public void addModels(String user, File directory) throws Exception{
		FindCAPDir finder = new FindCAPDir();
		ArrayList<Model> models = new ArrayList<Model>();
		try {
			Files.walkFileTree(directory.toPath(), finder);
			ArrayList<Path> targets = finder.getDirectories();
			int numtasks = targets.size();
			if (numtasks > 0) {
				for (Path path : targets) {
					Model newModel = modelManger.addModel(user, path.toFile());
					if(newModel!=null)
						models.add(newModel);
					else
						throw new NullPointerException("Unbale to create model for directory "+directory.getName());
				}
			} else {
				throw new Exception("No model id(s) provided");
			}
		} catch (Exception exx) {
			exx.printStackTrace();
			//Remove any models thats were created
			for(Model model: models){
				try{
					modelBean.removeModel(model);
				}catch(Exception exx1){
					sysLog.log("JOBMANAGER:ADDMODELS", "Failed to remove model from db, modelid:" +model.getModelID() + "; EXCEPTION:" + exx1.getMessage());
				}
			}
			throw exx;
		}
	}

	public void addToModelMetaData(String user, String modelID, File resource) throws Exception{
		checkResource(resource);
		modelManger.addModelMetaData(user, modelID, resource);
	}
	
	public void replaceModelMetaData(String user, String modelID, File resource) throws Exception{
		checkResource(resource);
		modelManger.replaceModelMetaData(user, modelID, resource);
	}
	
	public void removeModelMetaData(String user, String modelID) throws Exception{
		modelManger.removeModelMetaData(user, modelID);
	}
	
	public void updateModelComments(String user, String modelid, String comment) {
		modelManger.updateComments(user, modelid, comment);
	}

	public String getModelMetaData(String user, String modelID) throws Exception{
		return modelManger.getModelMetaData(user, modelID);
	}
	
	
	public void replaceStudyMetaData(String user, String studyUID, String descriptor, File resource) throws Exception{
		checkResource(resource);
		studyManager.replaceStudyMetaData(user, studyUID, descriptor, resource);
	}
	
	public void removeStudyMetaData(String user, String studyUID, String descriptor) throws Exception{
		studyManager.removeStudyMetaData(user, studyUID, descriptor);
	}
	
	public String getStudyMetaData(String user, String studyUID, String descriptor) throws Exception{
		try{
			return studyManager.getStudyMetaData(user, studyUID, descriptor);
		}catch(Exception exx){
			exx.printStackTrace();
			throw exx;
		}
	}
	
	
	private void checkResource(File resource) throws Exception{
		//Wait for the resource to be available
		int ctr = 30; // 1 min
		while(ctr-->0){
			if(!resource.exists()){
				try{
					Thread.sleep(2000);
				}catch(Exception ex){
					
				}
			}else{
				return;
			}
		}
		throw new FileNotFoundException(resource.getName());
	}
	
	
	private static class FindCAPDir extends SimpleFileVisitor<Path> {
		private final PathMatcher matcher1;
		private final PathMatcher matcher2;

		private HashSet<Path> result;

		public FindCAPDir() {
			matcher1 = FileSystems.getDefault().getPathMatcher("glob:*.exnode");
			matcher2 = FileSystems.getDefault().getPathMatcher("glob:*.exregion");
			result = new HashSet<Path>();
		}

		public ArrayList<Path> getDirectories() {
			return new ArrayList<Path>(result);
		}

		// Compares the glob pattern against
		// the file or directory name.
		void find(Path file) {
			Path name = file.getFileName();
			if (name != null && (matcher1.matches(name) || matcher2.matches(name))) {
				result.add(file.getParent());
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
