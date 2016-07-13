package nz.auckland.abi.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import nz.ac.auckland.abi.administration.CAPAccessManagerRemote;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class CAPUserServices
 */
@WebServlet(urlPatterns = "/CAPUserServices/*")
public class CAPUserServices extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	CAPAccessManagerRemote accessManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CAPUserServices() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap<String, String> query = new HashMap<String, String>();
		String user = request.getRemoteUser();
		if(request.isUserInRole("CAPADMIN"))//To ensure that only admin can overide oldpass
			query.put("ADMIN", "TRUE");

		int operation = 0;

		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {

			String paramName = (String) params.nextElement();

			if (paramName.indexOf("order[0][column]") > -1) {
				query.put("orderby", request.getParameter(paramName));
			} else if (paramName.indexOf("order[0][dir]") > -1) {
				query.put("orderdirection", request.getParameter(paramName));
			} else if (paramName.indexOf("search[value]") > -1) {
				query.put("sSearch", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("start")) {
				query.put("start", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("length")) {
				query.put("length", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("draw")) {
				query.put("draw", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("adduser")) {
				operation = 1;
				query.put("adduser", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("removeuser")) {
				operation = 2;
				query.put("removeuser", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("addRole")) {
				operation = 3;
				query.put("addRole", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("removeRole")) {
				operation = 4;
				query.put("removeRole", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("changePassword")) {
				operation = 5;
				query.put("changePassword", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("activitylog")) {
				operation = 6;
				query.put("activitylog", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("activitylogpurge")) {
				operation = 7;
				query.put("activitylogpurge", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("oldpass")) {
				query.put("oldpass", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("newpass")) {
				query.put("newpass", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("userdesc")) {
				query.put("userdesc", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("username")) {
				query.put("username", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("roles")) {
				query.put("roles", request.getParameter(paramName));
			}
		}
		
		switch (operation) {
		case 0: {
			getData(user, query, response);
			break;
		}
		case 1: {
			addUser(user, query, response);
			break;
		}
		case 2: {
			removeUser(user, query, response);
			break;
		}
		case 3: {
			addRole(user, query, response);
			break;
		}
		case 4: {
			removeRole(user, query, response);
			break;
		}
		case 5: {
			changePassword(user, query, response);
			break;
		}
		case 6: {
			getUserActivityLog(user, query, response);
			break;
		}
		case 7: {
			purgeUserActivityLog(user, query, response);
			break;
		}
		default: {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		}
	}

	private void getData(String user, HashMap<String, String> query, HttpServletResponse response) {
		if (query.containsKey("draw")) {
			try {
				// See https://datatables.net/manual/server-side //access date
				// Jan 2015
				query.put("draw", "" + Integer.parseInt(query.get("draw")));
			} catch (Exception exx) {

			}
		}

		JSONObject result = null;
		try {
			result = accessManager.getUsers(user, query);
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
			// System.out.println(output);
		} catch (Exception exx) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			exx.printStackTrace();
		}
	}

	private void getUserActivityLog(String user, HashMap<String, String> query, HttpServletResponse response) {
		if (query.containsKey("draw")) {
			try {
				// See https://datatables.net/manual/server-side //access date
				// Jan 2015
				query.put("draw", "" + Integer.parseInt(query.get("draw")));
			} catch (Exception exx) {

			}
		}

		JSONObject result = null;
		try {
			result = accessManager.getUserActivityLog(user, query);
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
			// System.out.println("User activity Log" + output);
		} catch (Exception exx) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			exx.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void purgeUserActivityLog(String user, HashMap<String, String> query, HttpServletResponse response) {
		String userID = query.get("activitylogpurge");
		JSONObject result = new JSONObject();
		try {
			accessManager.purgeUserActivityLog(user, query);
			result.put("event", "purgeUserActivityLog");
			result.put("msg", userID+"'s log purged");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
			// System.out.println("User activity Log" + output);
		} catch (Exception exx) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			exx.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void changePassword(String user, HashMap<String, String> query, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			String userID = query.get("changePassword");
			String newPass = query.get("newpass");
			String oldPass = query.get("oldpass");
			if(oldPass==null&&!query.containsKey("ADMIN")){
				result.put("event", "changePassword");
				result.put("error", "old password missing");
				String output = result.toJSONString();
				response.setContentType(MediaType.APPLICATION_JSON);
				response.getOutputStream().write(output.getBytes());
				response.getOutputStream().flush();
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Old password not provided");
			}
				
			accessManager.changeUserPassword(user, userID, oldPass, newPass);
			result.put("event", "changePassword");
			result.put("msg", userID + "'s password changed");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
		} catch (Exception exx) {
			exx.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
			} catch (Exception exx1) {
				exx1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addRole(String user, HashMap<String, String> query, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String userID = query.get("addRole");
		try {
			
			String roles = query.get("roles").replaceAll("\\[", "").replaceAll("\\]", "");
			String ir[] = roles.split(",");
			for (String role : ir) {
				try {
					int re = Integer.parseInt(role);
					accessManager.assignRole(user, userID, re);
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			}
			result.put("event", "addRole");
			result.put("msg", userID + " role changed");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
		} catch (Exception exx) {
			exx.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
			} catch (Exception exx1) {
				exx1.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeRole(String user, HashMap<String, String> query, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String userID = query.get("removeRole");
		try {
			
			String roles = query.get("roles").replaceAll("\\[", "").replaceAll("\\]", "");
			String ir[] = roles.split(",");
			for (String role : ir) {
				try {
					int re = Integer.parseInt(role);
					accessManager.removeRole(user, userID, re);
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			}
			result.put("event", "removeRole");
			result.put("msg", userID + " role changed");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
		} catch (Exception exx) {
			exx.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
			} catch (Exception exx1) {
				exx1.printStackTrace();
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void addUser(String user, HashMap<String, String> query, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String userID = query.get("adduser");
		try {
			accessManager.addNewUser(user, userID, query.get("username"), query.get("userdesc"), query.get("newpass"));
			// Add the roles
			String roles = query.get("roles").replaceAll("\\[", "").replaceAll("\\]", "");
			String ir[] = roles.split(",");
			for (String role : ir) {
				try {
					int re = Integer.parseInt(role);
					accessManager.assignRole(user, userID, re);
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			}
			result.put("event", "adduser");
			result.put("msg", userID + " added");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
		} catch (KeyAlreadyExistsException kx) {
			result.put("event", "adduser");
			result.put("error", userID + " exists");
			String output = result.toJSONString();
			try {
				response.setContentType(MediaType.APPLICATION_JSON);
				response.getOutputStream().write(output.getBytes());
				response.getOutputStream().flush();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, kx.getMessage());
			} catch (Exception exx) {

			}
		} catch (Exception exx) {
			exx.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
			} catch (Exception exx1) {
				exx1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void removeUser(String user, HashMap<String, String> query, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			String userID = query.get("removeuser");
			accessManager.removeUser(user, userID);
			result.put("event", "removeuser");
			result.put("msg", userID + " removed");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
		} catch (Exception exx) {
			exx.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
			} catch (Exception exx1) {
				exx1.printStackTrace();
			}
		}
	}
}
