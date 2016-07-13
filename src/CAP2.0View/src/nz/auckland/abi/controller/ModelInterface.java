package nz.auckland.abi.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class ModelInterface
 */
@WebServlet(urlPatterns = "/CAPModel/*", asyncSupported = true)
public class ModelInterface extends HttpServlet {
	private static final long serialVersionUID = 8989821L;

	@EJB
	PACSCAPDatabaseSynchronizerRemote sync;

	@EJB
	JobManagerRemote jobManager;

	@EJB
	FileResourcesManagerRemote resourceManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModelInterface() {
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
			if (key.equalsIgnoreCase("addModel[resourceid]")) {
				JSONObject obj = new JSONObject();
				obj.put("resourceid", request.getParameter(key));
				jsonObject.put("addModel", obj);
			} else if (key.equalsIgnoreCase("addModel[resourceloc]")) {
				JSONObject obj = new JSONObject();
				obj.put("resourceloc", request.getParameter(key));
				jsonObject.put("addModel", obj);
			} else if (key.equalsIgnoreCase("removeModel")) {
				try {
					JSONParser parser = new JSONParser();
					JSONArray arr = (JSONArray) parser.parse(request.getParameter(key));
					jsonObject.put("removeModel", arr);
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			} else if (key.equalsIgnoreCase("addModelMetaData")) {
				jsonObject.put("addModelMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("uploadModelMetaData")) {
				jsonObject.put("uploadModelMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("replaceModelMetaData")) {
				jsonObject.put("replaceModelMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("updateModelComments")) {
				jsonObject.put("updateModelComments", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("deleteModelMetaData")) {
				jsonObject.put("deleteModelMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("downloadModelMetaData")) {
				jsonObject.put("downloadModelMetaData", request.getParameter(key));// ModelID
			} else if (key.equalsIgnoreCase("resource[resourceid]")) {
				jsonObject.put("resourceid", request.getParameter(key));
			} else if (key.equalsIgnoreCase("resource[resourceloc]")) {
				jsonObject.put("resourceloc", request.getParameter(key));
			} else if (key.equalsIgnoreCase("comment")) {
				jsonObject.put("comment", request.getParameter(key));
			}
		}

		try {

			if (jsonObject.containsKey("addModel")) {
				JSONObject model = (JSONObject) jsonObject.get("addModel");
				File resource = getResource(model, response);
				if (resource != null) {
					jobManager.addModels(user, resource);
					JSONObject obj = new JSONObject();
					obj.put("event", "addModel");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("removeModel")) {
				JSONArray modelids = (JSONArray) jsonObject.get("removeModel");
				if (modelids != null) {
					ArrayList<String> ids = new ArrayList<String>();
					for (Object id : modelids) {
						ids.add((String) id);
					}
					jobManager.deleteModels(user, ids);
					JSONObject obj = new JSONObject();
					obj.put("event", "removeModels");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				} else {
					JSONObject obj = new JSONObject();
					obj.put("event", "removeModels");
					obj.put("ERROR", "NO model id provided");
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "resource information not available");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("updateModelComments")) {
				String modelid = (String) jsonObject.get("updateModelComments");
				String urlcomments = (String) jsonObject.get("comment");
				String comments = URLDecoder.decode(urlcomments.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
				jobManager.updateModelComments(user, modelid, comments);
				JSONObject obj = new JSONObject();
				obj.put("event", "updateComments");
				obj.put("SUCCESS", "TRUE");
				sendMessage(obj, response);
			} else if (jsonObject.containsKey("addModelMetaData")) {
				String modelid = (String) jsonObject.get("addModelMetaData");
				File resource = getResource(jsonObject, response);
				if (resource != null) {
					jobManager.addToModelMetaData(user, modelid, resource);
					JSONObject obj = new JSONObject();
					obj.put("event", "addModelMetaData");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("uploadModelMetaData")) {
				String modelid = (String) jsonObject.get("uploadModelMetaData");
				File resource = getResource(jsonObject, response);
				if (resource != null) {
					jobManager.replaceModelMetaData(user, modelid, resource);
					JSONObject obj = new JSONObject();
					obj.put("event", "uploadModelMetaData");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("replaceModelMetaData")) {
				String modelid = (String) jsonObject.get("replaceModelMetaData");
				File resource = getResource(jsonObject, response);
				if (resource != null) {
					jobManager.replaceModelMetaData(user, modelid, resource);
					JSONObject obj = new JSONObject();
					obj.put("event", "replaceModelMetaData");
					obj.put("SUCCESS", "TRUE");
					sendMessage(obj, response);
				}
			} else if (jsonObject.containsKey("deleteModelMetaData")) {
				String modelid = (String) jsonObject.get("deleteModelMetaData");
				jobManager.removeModelMetaData(user, modelid);
				JSONObject obj = new JSONObject();
				obj.put("event", "deleteModelMetaData");
				obj.put("SUCCESS", "TRUE");
				sendMessage(obj, response);
			} else if (jsonObject.containsKey("downloadModelMetaData")) {
				String modelid = (String) jsonObject.get("downloadModelMetaData");
				String rid = jobManager.getModelMetaData(user, modelid);
				JSONObject obj = new JSONObject();
				obj.put("event", "downloadModelMetaData");
				obj.put("SUCCESS", "TRUE");
				obj.put("RESOURCE", rid);
				sendMessage(obj, response);
			}else {
				System.out.println("Quitting with resource information not available: update");
				JSONObject obj = new JSONObject();
				obj.put("ERROR", "UNRECOGNIZED OPERATION");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "resource information not available");
				sendMessage(obj, response);
			}
		} catch (Exception exx) {
			// exx.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exx.getMessage());
			try {
				JSONObject obj = new JSONObject();
				obj.put("ERROR", exx.getMessage());
				sendMessage(obj, response);
			} catch (Exception e) {

			}

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
