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

import nz.ac.auckland.abi.businesslogic.SubjectViewBeanRemote;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet that handles query from jquery datatables plugin
 */
@WebServlet("/query")
public class DatatablesQueryHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private SubjectViewBeanRemote subjectViewBean;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DatatablesQueryHandler() {
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

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getRemoteUser();
		HashMap<String, String> query = new HashMap<String, String>();
		boolean queryImage = true;

		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = (String) params.nextElement();

			if (paramName.indexOf("search") > -1) {
				JSONParser parser = new JSONParser();
				try{
					JSONObject search = (JSONObject)parser.parse(request.getParameter(paramName));
					if(search.containsKey("subject_id"))
						query.put("subject_id", (String)search.get("subject_id"));
					else if(search.containsKey("subject_name"))
						query.put("subject_name", (String)search.get("subject_name"));
					else if(search.containsKey("subject_birthdate"))
						query.put("subject_birthdate", (String)search.get("subject_birthdate"));
					else if(search.containsKey("lsd"))
						query.put("lsd", (String)search.get("lsd"));		
					else if(search.containsKey("usd"))
						query.put("usd", (String)search.get("usd"));	
					else if(search.containsKey("model"))
						queryImage = false;
					
				}catch(Exception exx){
					
				}
			} else if (paramName.indexOf("order[0][column]") > -1) {
				try {
					int id = Integer.parseInt(request.getParameter(paramName));
					if (id < 4 || id == 6)
						query.put("orderby", "" + id);
					else if(queryImage==false && id==4){//Model name is 4
						//This the column name index in subjectview bean createQuery
						query.put("orderby", "10");
					}else if(queryImage==false && id==5){//Study Date is 5
						//This the column name index in subjectview bean createQuery
						query.put("orderby", "6");
					}
					//System.out.println(id);
				} catch (Exception exx) {

				}
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
			} 
			/*
			 * else{ //System.out.println("Parameter - " + paramName +
			 * ", Value - " + request.getParameter(paramName)); }
			 */
		}

/*		for (String key : query.keySet()) {
			System.out.println(key + "\t" + query.get(key));
		}*/

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
			if (queryImage) {
				result = subjectViewBean.queryImageData(user,query);
			} else {
				result = subjectViewBean.queryModelData(user,query);
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
