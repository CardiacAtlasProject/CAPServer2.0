package nz.auckland.abi.controller;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import nz.ac.auckland.abi.administration.FileResourcesManagerRemote;
import nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote;
import nz.ac.auckland.abi.job.JobManagerRemote;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class StudyInterface
 */
@WebServlet("/CAPStudy/*")
public class StudyInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	PACSCAPDatabaseSynchronizerRemote sync;

	@EJB
	JobManagerRemote jobManager;

	@EJB
	FileResourcesManagerRemote resourceManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StudyInterface() {
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
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getRemoteUser();
		JSONObject jsonObject = new JSONObject();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String key = params.nextElement();
			if (key.equalsIgnoreCase("addStudyMetaData")) {
				jsonObject.put("addStudyMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("replaceStudyMetaData")) {
				jsonObject.put("replaceStudyMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("deleteStudyMetaData")) {
				jsonObject.put("deleteStudyMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("downloadStudyMetaData")) {
				jsonObject.put("downloadStudyMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("resource[resourceid]")) {
				jsonObject.put("resourceid", request.getParameter(key));
			} else if (key.equalsIgnoreCase("resource[resourceloc]")) {
				jsonObject.put("resourceloc", request.getParameter(key));
			} else if (key.equalsIgnoreCase("descriptor")) {
				jsonObject.put("descriptor", request.getParameter(key));
			}
		}
		
		try {
			if (jsonObject.containsKey("addStudyMetaData")) {
				String studyuid = (String) jsonObject.get("addStudyMetaData");
				String descriptor = (String) jsonObject.get("descriptor");
				File resource = getResource(jsonObject, response);
				if (resource != null) {
					jobManager.replaceStudyMetaData(user, studyuid, descriptor, resource);
					JSONObject obj = new JSONObject();
					obj.put("event", "addStudyMetaData");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("replaceStudyMetaData")) {
				String studyuid = (String) jsonObject.get("replaceStudyMetaData");
				String descriptor = (String) jsonObject.get("descriptor");
				File resource = getResource(jsonObject, response);
				if (resource != null) {
					jobManager.replaceStudyMetaData(user, studyuid, descriptor, resource);
					JSONObject obj = new JSONObject();
					obj.put("event", "replaceStudyMetaData");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("downloadStudyMetaData")) {
				String studyuid = (String) jsonObject.get("downloadStudyMetaData");// StudyUID
				String descriptor = (String) jsonObject.get("descriptor");
				if (descriptor != null) {
					String rid = jobManager.getStudyMetaData(user, studyuid, descriptor);
					JSONObject obj = new JSONObject();
					obj.put("event", "downloadStudyMetaData");
					obj.put("SUCCESS", "TRUE");
					obj.put("RESOURCE", rid);
					sendMessage(obj, response);
				} else {
					JSONObject obj = new JSONObject();
					obj.put("ERROR", "No descriptor provided");
					sendMessage(obj, response);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No descriptor provided");
				}
			} else if (jsonObject.containsKey("deleteStudyMetaData")) {
				String studyuid = (String) jsonObject.get("deleteStudyMetaData");// StudyUID
				String descriptor = (String) jsonObject.get("descriptor");
				if (descriptor != null) {
					jobManager.removeStudyMetaData(user, studyuid, descriptor);
					JSONObject obj = new JSONObject();
					obj.put("event", "deleteStudyMetaData");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				} else {
					JSONObject obj = new JSONObject();
					obj.put("ERROR", "No descriptor provided");
					sendMessage(obj, response);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No descriptor provided");
				}
			}
		} catch (Exception exx) {
			exx.printStackTrace();
			try {
				JSONObject obj = new JSONObject();
				obj.put("ERROR", exx.getMessage());
				sendMessage(obj, response);
			} catch (Exception e) {

			}
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
		}
	}

	private File getResource(JSONObject jsonObject, HttpServletResponse response) throws Exception {
		File resource = null;
		if (jsonObject.containsKey("resourceid")) {
			String id = (String) jsonObject.get("resourceid");
			resource = resourceManager.getResourceLocation(id);
			String msg = resourceManager.checkFailure(id);
			if (msg != null) {
				response.sendError(HttpServletResponse.SC_SEE_OTHER, msg);
				return null;
			}
		} else if (jsonObject.containsKey("resourceloc")) {
			String file = (String) jsonObject.get("resourceloc");
			resource = new File(sync.getTempDir(), file);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "resource information not available");
			return null;
		}
		return resource;
	}

	private void sendMessage(JSONObject obj, HttpServletResponse response) throws Exception {
		String output = obj.toJSONString();
		response.setContentType(MediaType.APPLICATION_JSON);
		response.getOutputStream().write(output.getBytes());
		response.getOutputStream().flush();
	}
}
