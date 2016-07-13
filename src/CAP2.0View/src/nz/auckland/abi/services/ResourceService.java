package nz.auckland.abi.services;

import java.io.IOException;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.ac.auckland.abi.administration.DownloadsManagerRemote;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class ResourceService
 */
@WebServlet(urlPatterns = "/getData/*", asyncSupported = true)
public class ResourceService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private DownloadsManagerRemote downloadsManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ResourceService() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request structure");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getRemoteUser();
		try {
			
			JSONParser parse = new JSONParser();
			//JSONObject data = (JSONObject) parse.parse(request.getReader().readLine());
			JSONArray data = (JSONArray) parse.parse(request.getReader().readLine());
			if(data==null)
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request structure");

			if (data.size() > 0) {
				JSONObject[] items = new JSONObject[data.size()];
				items = (JSONObject[]) data.toArray(items);
				ArrayList<String> studyKeys = new ArrayList<String>();
				ArrayList<String> modelKeys = new ArrayList<String>();
				for (JSONObject itm : items) {
					if (itm.containsKey("STUDY")){
						studyKeys.add(itm.toJSONString());
					}
					else if (itm.containsKey("MODEL")){//This is a json String
						modelKeys.add(itm.toJSONString());
					}
				}
				String resourceid = downloadsManager.getData(user,studyKeys, modelKeys);
				JSONObject result = new JSONObject();
				result.put("RESOURCE", resourceid);
				response.getWriter().println(result.toJSONString());
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No items requested");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// crash and burn
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
		}
	}
}
