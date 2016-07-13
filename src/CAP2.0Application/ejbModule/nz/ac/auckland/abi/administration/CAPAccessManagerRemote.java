package nz.ac.auckland.abi.administration;

import java.util.HashMap;

import javax.ejb.Remote;

import org.json.simple.JSONObject;

@Remote
public interface CAPAccessManagerRemote {

	public void changeUserPassword(String actor, String username, String oldPass, String newPass) throws Exception;

	public void addNewUser(String actor, String userID, String username, String description, String password) throws Exception;

	public void assignRole(String actor, String username, int role) throws Exception;

	public void removeRole(String actor, String username, int role) throws Exception;

	public void removeUser(String actor, String username) throws Exception;

	public JSONObject getUsers(String actor, HashMap<String, String> queryParameters);

	public JSONObject getUserActivityLog(String actor, HashMap<String, String> queryParameters);

	public void purgeUserActivityLog(String actor, HashMap<String, String> queryParameters);
}
