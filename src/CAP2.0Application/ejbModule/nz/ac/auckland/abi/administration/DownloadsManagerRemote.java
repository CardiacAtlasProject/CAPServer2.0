package nz.ac.auckland.abi.administration;

import java.io.File;
import java.util.ArrayList;

import javax.ejb.Remote;

@Remote
public interface DownloadsManagerRemote {
	public File getResourceLocation(String id);
	
	public String getFailedResourceMessage(String id);
	
	public Object[] createDownloadResource(String filename);

	public String getImageData(String user,ArrayList<String> studyinstanuids) throws Exception;

	public String getModelData(String user,ArrayList<String> modelids) throws Exception;

	public String getData(String user,ArrayList<String> studyinstanuids, ArrayList<String> modelids) throws Exception;
}
