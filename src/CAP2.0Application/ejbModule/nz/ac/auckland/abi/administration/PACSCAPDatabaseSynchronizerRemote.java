package nz.ac.auckland.abi.administration;

import java.util.HashMap;
import java.util.Properties;

import javax.ejb.Remote;

import org.json.simple.JSONObject;

@Remote
public interface PACSCAPDatabaseSynchronizerRemote {
	
	public void consolidateSubject(String pid);
	
	public String getTempDir();

	public void setTempDir(String tempDir);

	public String getAetitle();

	public void setAetitle(String aetitle);

	public String getHostname();

	public void setHostname(String hostname);

	public String getPort();

	public void setPort(String port);

	public String getCaetitle();

	public void setCaetitle(String caetitle);

	public String getCaetname();

	public void setCaetname(String caetname);

	public String getCaeport();

	public void setCaeport(String caeport);

	public String getPacsProtocol();

	public void setPacsProtocol(String pacsProtocol);

	public String getWadoPort();

	public void setWadoPort(String wadoPort);

	public long getMaximumStoredSubjectTableLifeTime();

	public void setMaximumStoredSubjectTableLifeTime(long maximumStoredSubjectTableLifeTime);

	public int getMaxIdleTime();

	public void setMaxIdleTime(int maxIdleTime);

	public long getTemporaryFileLifeTime();

	public void setTemporaryFileLifeTime(long temporaryFileLifeTime);

	public String getModalities();

	public void setModalities(String mod);

	public long getDownloadTokenSize();

	public void setDownloadTokenSize(long downloadTokenSize);
	
	public boolean constrainModelsToPACSStudies();

	public void setConstrainModelsToPACSStudies(boolean constrainModelsToPACSStudies);
	
	public boolean cachePACSImageData();

	public void setCachePACSImageData(boolean cachePACSImageData) ;

	public Properties getPacsAccessProperties();

	public void setPacsAccessProperties(Properties pacsAccessProperties);

	public JSONObject getSystemLog(String actor, HashMap<String, String> queryParameters);

	public JSONObject getActiveTasks(String actor, HashMap<String, String> queryParameters);

	public void purgeSystemLog(String actor, HashMap<String, String> queryParameters) throws Exception;

	public JSONObject getActivitySummary(String user, HashMap<String, String> queryParameters);
	
	public void purgeTask(String user, String rid);
	
	public void recordLogin(String user);
}
