package nz.ac.auckland.abi.businesslogic;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.resource.spi.SecurityException;

import nz.ac.auckland.abi.entities.UserActivityLog;
import nz.ac.auckland.abi.entities.Users;

import org.jboss.security.Base64Encoder;
import org.jboss.security.Base64Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Session Bean implementation class UserProvenenceBean
 */
@Stateless
@LocalBean
public class UserProvenanceBean {
	@PersistenceContext(unitName = "CAPAccess")
	private EntityManager entityManager;

	/**
	 * Default constructor.
	 */
	public UserProvenanceBean() {

	}

	public void logUsage(String userName, String pass, String event, String description, double quantity) throws Exception{
		List<Users> user = entityManager.createQuery("SELECT t FROM Users t WHERE t.userID='" + userName + "'", Users.class).getResultList();
		if (!user.isEmpty()) {
			byte[] dpass = Base64Utils.fromb64(pass);
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(new String(dpass).getBytes("UTF-8"));
			byte[] mypass = Base64Encoder.encode(digest.digest()).getBytes("UTF-8");
			byte[] upass = user.get(0).getPassword().getBytes(Charset.forName("UTF-8"));
			if (Arrays.equals(mypass, upass)) {
				UserActivityLog log = new UserActivityLog(user.get(0), event, description, quantity);
				entityManager.persist(log);
				return;
			}
		}
		throw new SecurityException("Username/Password incorrect");
	}

	public void logUsage(String userName, String event, String description, double quantity) {
		List<Users> user = entityManager.createQuery("SELECT t FROM Users t WHERE t.userID='" + userName + "'", Users.class).getResultList();
		UserActivityLog log = new UserActivityLog(user.get(0), event, description, quantity);
		entityManager.persist(log);
	}

	@SuppressWarnings("unchecked")
	public JSONObject getActivitySummary(HashMap<String, String> queryParameters) {
		String userid = queryParameters.get("userid");
		JSONObject result = new JSONObject();
		String query = "SELECT activity, sum(quantity), DATE(time) FROM UserActivityLog group by activity, DATE(time)";
		if (userid != null) {
			query = "SELECT activity, sum(quantity), DATE(time) FROM UserActivityLog where username = '" + userid + "' group by activity, DATE(time)";
			if (queryParameters.containsKey("sSearch"))
				query = "SELECT activity, sum(quantity), DATE(time) FROM UserActivityLog where username = '" + userid + "' and activity like '%"
						+ queryParameters.get("sSearch") + "%'group by activity, DATE(time)";
		} else {
			if (queryParameters.containsKey("sSearch"))
				query = "SELECT activity, sum(quantity), DATE(time) FROM UserActivityLog where activity like '%" + queryParameters.get("sSearch")
						+ "%'group by activity, DATE(time)";
		}

		if (queryParameters.containsKey("start") && queryParameters.containsKey("length"))
			query += " limit " + queryParameters.get("start") + ", " + queryParameters.get("length");

		List<Object[]> res = entityManager.createNativeQuery(query).getResultList();
		JSONArray activity = new JSONArray();
		for (Object[] ir : res) {
			JSONObject obj = new JSONObject();
			String act = (String) ir[0];
			obj.put("ACTIVITY", ir[0]);
			if (!act.equalsIgnoreCase("GETDATA"))
				obj.put("QUANTITY", "" + ir[1]);
			else {
				BigDecimal val = (BigDecimal) ir[1];
				int valu = (int) (100.0 * val.doubleValue() / (1048576));
				obj.put("QUANTITY", "" + valu / 100.0);
			}
			obj.put("DATE", "" + ir[2]);
			activity.add(obj);
		}
		result.put("recordsFiltered", res.size());
		result.put("recordsTotal", res.size());
		result.put("data", activity);

		return result;
	}
}
