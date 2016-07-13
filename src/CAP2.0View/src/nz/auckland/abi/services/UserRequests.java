package nz.auckland.abi.services;

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

import nz.ac.auckland.abi.administration.CAPAccessManagerRemote;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class UserRequests Requests for changing passwords etc
 * are routed through this servlet
 */
@WebServlet("/UserRequests")
public class UserRequests extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	CAPAccessManagerRemote accessManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserRequests() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
		Enumeration<String> params = request.getParameterNames();
		boolean changePassword = false;
		boolean sessionCheck = false;
		boolean logout = false;
		HashMap<String, String> query = new HashMap<String, String>();
		JSONObject result = new JSONObject();
		while (params.hasMoreElements()) {

			String paramName = (String) params.nextElement();

			if (paramName.equalsIgnoreCase("session")) {
				sessionCheck = true;
			} else if (paramName.equalsIgnoreCase("changePassword")) {
				changePassword = true;
			} else if (paramName.equalsIgnoreCase("logout")) {
				logout = true;
			} else if (paramName.equalsIgnoreCase("oldpass")) {
				query.put("oldpass", request.getParameter(paramName));
			} else if (paramName.equalsIgnoreCase("newpass")) {
				query.put("newpass", request.getParameter(paramName));
			}
		}
		if(sessionCheck){
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		if(logout){
			try{
				request.getSession().invalidate();
			}catch(Exception exx){
				
			}
			response.sendRedirect("./");
			return;
		}
		if (changePassword) {
			String userID = request.getRemoteUser();
			String newPass = query.get("newpass");
			String oldPass = query.get("oldpass");
			if (oldPass == null && !query.containsKey("ADMIN")) {
				result.put("event", "changePassword");
				result.put("error", "old password missing");
				String output = result.toJSONString();
				response.setContentType(MediaType.APPLICATION_JSON);
				response.getOutputStream().write(output.getBytes());
				response.getOutputStream().flush();
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Old password not provided");
			}

			try{
				accessManager.changeUserPassword(user, userID, oldPass, newPass);
			}catch(Exception exx){
				exx.printStackTrace();
				result.put("event", "changePassword");
				result.put("error", exx.getMessage());
				String output = result.toJSONString();
				response.setContentType(MediaType.APPLICATION_JSON);
				response.getOutputStream().write(output.getBytes());
				response.getOutputStream().flush();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			result.put("event", "changePassword");
			result.put("msg", userID + "'s password changed");
			String output = result.toJSONString();
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getOutputStream().write(output.getBytes());
			response.getOutputStream().flush();
			response.setStatus(HttpServletResponse.SC_OK);
		} else
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return;
	}
}


