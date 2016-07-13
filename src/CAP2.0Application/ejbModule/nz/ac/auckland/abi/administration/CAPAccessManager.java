package nz.ac.auckland.abi.administration;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.resource.spi.SecurityException;

import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;
import nz.ac.auckland.abi.businesslogic.UserProvenanceBean;
import nz.ac.auckland.abi.entities.UserActivityLog;
import nz.ac.auckland.abi.entities.UserRoles;
import nz.ac.auckland.abi.entities.Users;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Session Bean implementation class CAPAccessManager
 */
@Singleton
@LocalBean
public class CAPAccessManager implements CAPAccessManagerRemote {

	@PersistenceContext(unitName = "CAPAccess")
	private EntityManager entityManager;

	@EJB
	SystemNotificationBean syslog;

	@EJB
	UserProvenanceBean provanence;

	/**
	 * Default constructor.
	 */
	public CAPAccessManager() {
		super();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void recordLogin(String user) {
		String q = "SELECT * FROM Users U WHERE U.username='" + user + "'";
		Query nQuery = entityManager.createNativeQuery(q, Users.class);
		@SuppressWarnings("unchecked")
		List<Users> pks = nQuery.getResultList();
		if (pks.size() != 0) {
			Users userObj = pks.get(0);
			userObj.setLastLogin(new Date(System.currentTimeMillis()));
			entityManager.merge(userObj);
			entityManager.flush();
		} else {
			syslog.log("SECURITYVIOLATION", "User:" + user + " login record not found, but passed through at " + (new Date(System.currentTimeMillis())));
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	/**
	 * 
	 * @param actor the caller
	 * @param username
	 * @param oldPass in message digested form
	 * @param newPass in message digested form
	 * @throws Exception
	 */
	public void changeUserPassword(String actor, String username, String oldPass, String newPass) throws Exception {
		syslog.log("CHANGEPASSWORD", "User:" + username + ";Actor:" + actor);
		provanence.logUsage(actor, "CHANGEPASSWORD", "USERID:" + username, 1);
		String q = "SELECT * FROM Users U WHERE U.username='" + username + "'";
		Query nQuery = entityManager.createNativeQuery(q, Users.class);
		@SuppressWarnings("unchecked")
		List<Users> pks = nQuery.getResultList();
		if (pks.size() != 0) {
			Users user = pks.get(0);
			if (oldPass != null) {
				if (user.getPassword().equals(oldPass)) {
					user.setPassword(newPass);
					user.setLastPasswordChange(new Date(System.currentTimeMillis()));
					entityManager.merge(user);
					entityManager.flush();
				} else {
					syslog.log("CHANGEPASSWORDFAILED", "User:" + username + ";Actor:" + actor + "; EXCEPTION: incorrect password");
					throw new SecurityException("Incorrect Username/Password");
				}
			} else {
				user.setPassword(newPass);
				user.setLastPasswordChange(new Date(System.currentTimeMillis()));
				entityManager.merge(user);
				entityManager.flush();
			}
		} else {
			syslog.log("CHANGEPASSWORDFAILED", "User:" + username + ";Actor:" + actor + "; EXCEPTION: USER not found");
			throw new Exception("User " + username + " not found");
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addNewUser(String actor, String userID, String username, String description, String password) throws Exception {
		syslog.log("ADDNEWUSER", "User:" + username + ";Actor:" + actor);
		provanence.logUsage(actor, "ADDNEWUSER", "USERID:" + userID, 1);
		String q = "SELECT u.username FROM Users u WHERE u.username='" + username + "'";
		Query nQuery = entityManager.createNativeQuery(q);
		@SuppressWarnings("unchecked")
		List<String> pks = nQuery.getResultList();
		if (pks.size() == 0) {
			Users user = new Users();
			user.setUserID(userID);
			user.setUserName(username);
			user.setPassword(password);
			user.setDescription(description);
			user.setLastPasswordChange(new Date(System.currentTimeMillis()));
			user.setLastLogin(new Date(295675200000L));// Dummy date
			entityManager.persist(user);
			UserRoles role = new UserRoles();
			role.setMyRole(user);
			role.setRole("CAPREADER");
			entityManager.persist(role);
			entityManager.flush();
		} else {
			syslog.log("ADDNEWUSERFAILED", "User:" + username + ";Actor:" + actor + "; EXCEPTION: USER ALREADY EXISTS");
			throw new KeyAlreadyExistsException("Username already exists");
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void assignRole(String actor, String username, int role) throws Exception {
		syslog.log("ASSIGNROLE", "User:" + username + ";Actor:" + actor + ";ROLE:" + role);
		provanence.logUsage(actor, "ASSIGNROLE", "USERID:" + username + "; ROLE:" + role, 1);
		String q = "SELECT * FROM Users u WHERE u.username='" + username + "'";
		@SuppressWarnings("unchecked")
		List<Users> nQuery = entityManager.createNativeQuery(q, Users.class).getResultList();
		if (nQuery.size() > 0) {
			Users user = nQuery.get(0);
			Set<UserRoles> myRoles = user.getRoles();
			String roleDesc = "CAPREADER";
			switch (role) {
			case 0:
				break;
			case 1: {
				roleDesc = "CAPAUTHOR";
				break;
			}
			case 2: {
				roleDesc = "CAPADMIN";
				break;
			}
			default: {
				syslog.log("SECURITY", "ASSIGNROLE:User:" + username + ";Actor:" + actor + "; EXCEPTION: Incorrect role value(" + role
						+ ") passed, possible phishing");
				roleDesc = "CAPREADER";
				break;
			}
			}
			for (UserRoles ur : myRoles) {
				if (ur.getRole().equalsIgnoreCase(roleDesc)) {
					return;
				}
			}
			UserRoles newRole = new UserRoles();
			newRole.setRole(roleDesc);
			newRole.setMyRole(user);
			entityManager.persist(newRole);
			entityManager.flush();
		} else {
			syslog.log("SECURITY", "ASSIGNROLE: User:" + username + ";Actor:" + actor + "; EXCEPTION: USER NOT FOUND, possible phishing");
			throw new EntityNotFoundException(username + "\t not found");
		}
	}

	public void removeRole(String actor, String username, int role) throws Exception {
		syslog.log("REMOVEROLE", "User:" + username + ";Actor:" + actor + ";ROLE:" + role);
		provanence.logUsage(actor, "REMOVEROLE", "USERID:" + username + "; ROLE:" + role, 1);
		String q = "SELECT * FROM Users u WHERE u.username='" + username + "'";
		@SuppressWarnings("unchecked")
		List<Users> nQuery = entityManager.createNativeQuery(q, Users.class).getResultList();
		if (nQuery.size() > 0) {
			Users user = nQuery.get(0);
			Set<UserRoles> myRoles = user.getRoles();
			String roleDesc = "CAPREADER";
			switch (role) {
			case 0:
				break;
			case 1: {
				roleDesc = "CAPAUTHOR";
				break;
			}
			case 2: {
				roleDesc = "CAPADMIN";
				break;
			}
			default: {
				syslog.log("SECURITY", "REMOVEROLE: User:" + username + ";Actor:" + actor + "; EXCEPTION: Incorrect role value passed, possible phishing");
				roleDesc = "CAPREADER";
				break;
			}
			}
			for (UserRoles ur : myRoles) {
				if (ur.getRole().equalsIgnoreCase(roleDesc)) {
					entityManager.remove(ur);
					entityManager.flush();
					return;
				}
			}
		} else {
			syslog.log("SECURITY", "REMOVEROLE: User:" + username + ";Actor:" + actor + "; EXCEPTION: USER NOT FOUND, possible phishing");
			throw new EntityNotFoundException(username + "\t not found");
		}
	}

	public void removeUser(String actor, String username) throws Exception {
		syslog.log("REMOVEUSER", "User:" + username + ";Actor:" + actor);
		provanence.logUsage(actor, "REMOVEUSER", "USERID:" + username, 1);
		String q = "SELECT * FROM Users u WHERE u.username='" + username + "'";
		@SuppressWarnings("unchecked")
		List<Users> nQuery = entityManager.createNativeQuery(q, Users.class).getResultList();
		if (nQuery.size() > 0) {
			Users user = nQuery.get(0);
			entityManager.remove(user);
			entityManager.flush();
		} else {
			syslog.log("SECURITY", "REMOVEUSER: User:" + username + ";Actor:" + actor + "; EXCEPTION: USER NOT FOUND, possible phishing");
			throw new EntityNotFoundException(username + "\t not found");
		}
	}

	private String createQuery(HashMap<String, String> queryParameters) {
		String dir = "asc";
		String sdir = queryParameters.get("orderdirection");
		String orderby = queryParameters.get("orderby");
		if (orderby == null)
			orderby = "userID";
		else {
			if (orderby.equalsIgnoreCase("1")) {
				orderby = "userName";
			} else {
				orderby = "userID";
			}
		}

		if (sdir != null) {
			if (!sdir.equals("asc"))
				dir = "desc";
		}

		String searchTerm = queryParameters.get("sSearch");
		String globeSearch = "";
		if (searchTerm != null) { // Note the field names should match the
									// Hibernate names i.e in the entity
									// definition
			globeSearch = " where (p.userID like '%" + searchTerm + "%'" + " or p.userName like '%" + searchTerm + "%')";
		}
		if (sdir != null)
			globeSearch += " order by p." + orderby + " " + dir;
		// globeSearch += " limit " + start + ", " + amount;

		return globeSearch;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getUsers(String actor, HashMap<String, String> queryParameters) {
		provanence.logUsage(actor, "CAPUSERQUERY", createQuery(queryParameters), 1);
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();

		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String draw = queryParameters.get("draw");

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

		result.put("start", sStart);
		result.put("count", sAmount);

		try {
			// Get the total number of studyrecords
			String q = "SELECT count(*) from  Users";
			Query countq = entityManager.createNativeQuery(q);
			List<java.math.BigInteger> pctr = countq.getResultList();

			// Determine the list of patients, let this be total
			int total = pctr.get(0).intValue();

			Query myq = entityManager.createQuery("SELECT p FROM Users p" + createQuery(queryParameters), Users.class);
			myq.setFirstResult(start);
			myq.setMaxResults(amount);
			List<Users> results = myq.getResultList();
			for (Users user : results) {

				if (user.getUserID().equalsIgnoreCase("ADMIN"))
					continue;

				JSONObject userRecord = new JSONObject();
				userRecord.put("DT_RowId", "" + user.getPkID());
				userRecord.put("ID", "" + user.getUserID());
				userRecord.put("NAME", "" + user.getUserName());
				userRecord.put("DESC", "" + user.getDescription());
				userRecord.put("LASTLOGIN", "" + user.getLastLogin());
				userRecord.put("LASTPWDCHANGE", "" + user.getLastPasswordChange());
				JSONArray roles = new JSONArray();
				Set<UserRoles> croles = user.getRoles();
				for (UserRoles rl : croles) {
					roles.add("" + rl.getRole());
				}
				userRecord.put("ROLES", roles.toJSONString());
				array.add(userRecord);
			}
			// Based on https://datatables.net/manual/server-side //access date
			// Jan 2015
			result.put("recordsTotal", total);
			result.put("recordsFiltered", results.size());
			result.put("data", array);
			if (draw != null)
				result.put("draw", draw);
			// System.out.println(result.toJSONString());
		} catch (Exception exx) {
			exx.printStackTrace();
			result.put("error", exx.toString());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getUserActivityLog(String actor, HashMap<String, String> queryParameters) {
		provanence.logUsage(actor, "CAPUSERACTIVITYQUERY", null, 1);
		JSONObject result = new JSONObject();
		JSONArray activity = new JSONArray();
		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String draw = queryParameters.get("draw");
		String userid = queryParameters.get("activitylog");
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
				orderby = "activity";
			} else if (orderby.equalsIgnoreCase("1")) {
				orderby = "quantity";
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

			if (!userid.equalsIgnoreCase("TRUE")) {
				// Get the total number of studyrecords
				String q = "SELECT count(*) from  UserActivityLog u where u.username='" + userid + "'";
				List<java.math.BigInteger> pctr = entityManager.createNativeQuery(q).getResultList();

				// Determine the list of patients, let this be total
				int total = pctr.get(0).intValue();
				String query = null;
				if (searchTerm != null)
					query = "SELECT * FROM UserActivityLog p where p.username='" + userid + "' and (p.activity like '%" + searchTerm
							+ "%' or p.quantity like '%" + searchTerm + "%' or p.time like '%" + searchTerm + "%') order by " + orderby + " " + dir;
				else
					query = "SELECT * FROM UserActivityLog p where p.username='" + userid + "' order by " + orderby + " " + dir;
				Query myq = entityManager.createNativeQuery(query, UserActivityLog.class);
				myq.setFirstResult(start);
				myq.setMaxResults(amount);
				List<UserActivityLog> results = myq.getResultList();
				for (UserActivityLog cl : results) {
					JSONObject obj = new JSONObject();
					obj.put("ACTIVITY", cl.getActivity() + ":" + cl.getDescription());
					obj.put("QUANTITY", "" + cl.getQuantity());
					if (cl.getActivity().equalsIgnoreCase("GETDATA")) {
						int valu = (int) (100.0 * cl.getQuantity().doubleValue() / (1048576));
						obj.put("QUANTITY", "" + valu / 100.0);
					}
					Date myDate = cl.getTime();
					if (myDate != null)
						obj.put("TIME", "" + myDate);
					else
						obj.put("TIME", "0");
					activity.add(obj);

				}
				result.put("recordsFiltered", results.size());
				result.put("recordsTotal", total);
			} else {
				result.put("recordsFiltered", "0");
				result.put("recordsTotal", "0");
			}
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

	public void purgeUserActivityLog(String actor, HashMap<String, String> queryParameters) {
		String userid = queryParameters.get("activitylogpurge");
		provanence.logUsage(actor, "PURGECAPUSERACTIVITYQUERY", "USERID:" + userid, 1);
		try {
			entityManager.createNativeQuery("DELETE FROM UserActivityLog where username='" + userid + "'").executeUpdate();
			entityManager.flush();
		} catch (Exception exx) {
			throw exx;
		}

	}

}
