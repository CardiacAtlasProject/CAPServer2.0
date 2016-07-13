package nz.ac.auckland.abi.businesslogic;

import java.util.HashMap;

import javax.ejb.Remote;

import org.json.simple.JSONObject;

@Remote
public interface SubjectViewBeanRemote {
	public JSONObject queryImageData(String user,HashMap<String, String> queryParameters);
	public JSONObject queryModelData(String user,HashMap<String, String> queryParameters);
}
