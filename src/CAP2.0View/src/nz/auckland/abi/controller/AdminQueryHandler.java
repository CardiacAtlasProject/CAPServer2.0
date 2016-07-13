package nz.auckland.abi.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote;

import org.json.simple.JSONObject;

/**
 * Servlet that handles query from jquery datatables plugin
 */
@WebServlet("/admin")
public class AdminQueryHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private PACSCAPDatabaseSynchronizerRemote admin;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminQueryHandler() {
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

	@SuppressWarnings("unchecked")
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getRemoteUser();
		HashMap<String, String> query = new HashMap<String, String>();
		boolean querySystemLog = false;
		boolean purgeSystemLog = false;
		boolean updateSystem = false;
		boolean getActiveTasks = false;
		boolean purgeTask = false;
		boolean userActivitySummary = false;
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = (String) params.nextElement();

			if (paramName.indexOf("purgesystemlog") > -1) {
				purgeSystemLog = true;
			} else if (paramName.indexOf("systemlog") > -1) {
				querySystemLog = true;
			} else if (paramName.indexOf("activeTasks") > -1) {
				getActiveTasks = true;
			} else if (paramName.indexOf("useractivitysummary") > -1) {
				userActivitySummary = true;
			} else if (paramName.indexOf("purgeTask") > -1) {
				purgeTask = true;
			} else if (paramName.indexOf("order[0][column]") > -1) {
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
			} else if (paramName.equalsIgnoreCase("select")) {
				query.put("select", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("update")) {
				query.put("update", request.getParameter(paramName));
				updateSystem = true;
			} else if (paramName.equalsIgnoreCase("entity")) {
				query.put("entity", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("resourceid")) {
				query.put("resourceid", request.getParameter(paramName));
			}

			// System.out.println(paramName+"\t"+request.getParameter(paramName));
		}
		/*
		 * for (String key : query.keySet()) { System.out.println(key + "\t" +
		 * query.get(key)); }
		 */

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
			if (querySystemLog) {
				result = admin.getSystemLog(user, query);
			} else if (getActiveTasks) {
				result = admin.getActiveTasks(user, query);
			} else if (purgeSystemLog) {
				admin.purgeSystemLog(user, query);
				result = new JSONObject();
				result.put("EVENT", "SYSTEMACTIVITYPURGE");
				result.put("SUCCESS", "TRUE");
			} else if (purgeTask) {
				if (query.containsKey("resourceid")) {
					admin.purgeTask(user, query.get("resourceid"));
					result = new JSONObject();
					result.put("EVENT", "SYSTEMACTIVITYPURGE");
					result.put("SUCCESS", "TRUE");
				}else{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			} else if (userActivitySummary) {
				result = admin.getActivitySummary(user, query);
			} else if (updateSystem) {
				String key = query.get("update");
				String value = query.get("entity");
				if (key.equalsIgnoreCase("AETITLE")) {
					admin.setAetitle(value);
				} else if (key.equalsIgnoreCase("HOSTNAME")) {
					admin.setHostname(value);
				} else if (key.equalsIgnoreCase("HOSTPORT")) {
					admin.setPort(value);
				} else if (key.equalsIgnoreCase("PROTOCOL")) {
					admin.setPacsProtocol(value);
				} else if (key.equalsIgnoreCase("WADOPORT")) {
					admin.setWadoPort(value);
				} else if (key.equalsIgnoreCase("CAET")) {
					admin.setCaetitle(value);
				} else if (key.equalsIgnoreCase("CAEHOSTNAME")) {
					admin.setCaetname(value);
				} else if (key.equalsIgnoreCase("CAEPORT")) {
					admin.setCaeport(value);
				} else if (key.equalsIgnoreCase("MODALITIES")) {
					admin.setModalities(value);
				} else if (key.equalsIgnoreCase("SCRATCH")) {
					admin.setTempDir(value);
				} else if (key.equalsIgnoreCase("TOKENSIZE")) {
					admin.setDownloadTokenSize(Long.parseLong(value));
				} else if (key.equalsIgnoreCase("RESOURCELIFE")) {
					admin.setTemporaryFileLifeTime(Long.parseLong(value));
				} else if (key.equalsIgnoreCase("IDLETIME")) {
					admin.setMaxIdleTime(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("SYNCPERIOD")) {
					admin.setMaximumStoredSubjectTableLifeTime(Long.parseLong(value));
				} else if (key.equalsIgnoreCase("SYNCNOW")) {
					if(value.length()>0){
						String[] values = value.split(";");
						for(String pid : values){
							admin.consolidateSubject(pid);
						}
					}
				} else if (key.equalsIgnoreCase("CACHEPACSINSTANCES")) {
					admin.setCachePACSImageData(Boolean.parseBoolean(value));
				} else if (key.equalsIgnoreCase("CONSTRAINMODELS")) {
					admin.setConstrainModelsToPACSStudies(Boolean.parseBoolean(value));
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				result = new JSONObject();
				result.put("EVENT", key);
				result.put("SUCCESS", "TRUE");
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
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
}
