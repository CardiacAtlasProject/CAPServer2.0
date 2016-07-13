package nz.ac.auckland.abi.job;

import java.io.File;
import java.util.ArrayList;

import javax.ejb.Remote;

@Remote
public interface JobManagerRemote {
	public void addModels(String user, File directory) throws Exception;

	public void deleteModels(String user, ArrayList<String> modelids) throws Exception;

	public void updateModelComments(String user, String modelid, String comment);

	public void addToModelMetaData(String user, String modelID, File resource) throws Exception;

	public void replaceModelMetaData(String user, String modelID, File resource) throws Exception;

	public void removeModelMetaData(String user, String modelID) throws Exception;

	public String getModelMetaData(String user, String modelID) throws Exception;

	public void replaceStudyMetaData(String user, String studyUID, String descriptor, File resource) throws Exception;

	public void removeStudyMetaData(String user, String studyUID, String descriptor) throws Exception;

	public String getStudyMetaData(String user, String studyUID, String descriptor) throws Exception;
}
