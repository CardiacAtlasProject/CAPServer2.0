package nz.ac.auckland.abi.administration;

import java.io.File;

import javax.ejb.Remote;

@Remote
public interface FileResourcesManagerRemote {
	public File getResourceLocation(String id);

	public String checkFailure(String id);

	public String createResource(File target, long addLife);

	public String createCompressedResource(File target, long addLife) throws Exception;

	public String createResource(String rid, File target, long addLife);

	public String createCompressedResource(String rid, File target, long addLife) throws Exception;

	public String consumeResource(File target, long addLife);

	public void releaseResource(String rid);

	public void setResouceFailure(String rid, String error);

	public void setTerminate(String rid);

	public boolean getTerminate(String rid);

	public void setProgress(String rid, double prog);

	public double getProgress(String rid);

	public long getResourceLife();

	public long getDownloadTokenSize();

}
