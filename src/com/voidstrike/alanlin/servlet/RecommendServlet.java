package com.voidstrike.alanlin.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.voidstrike.alanlin.dbmgr.DBMgr;
import com.voidstrike.alanlin.dbmgr.Singleton;
import com.voidstrike.alanlin.logic.Activity;
import com.voidstrike.alanlin.logic.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class PostServlet
 */
@WebServlet("/RecommendServlet")
public class RecommendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		Singleton sin = new Singleton();
		DBMgr auxMgr = sin.getInstance();
		JSONObject json = new JSONObject();
		HttpSession session = request.getSession();
		
		
		String userEmail = (String) session.getAttribute("email");
		String recommendType = (String) request.getParameter("type");
		auxMgr = new DBMgr();
		User currentUser = auxMgr.getUser(userEmail);
		Map<Integer, String> recommendedFriends = new HashMap<Integer, String>();
		
		if (recommendType == null){
			// Error workPattern
			json.put("flag", false);
			json.put("test", true);
			json.put("msg", "unsupported operation");
		}
		else if (recommendType.equals("activity")){
			// Get Activity Recommendation
			json.put("flag", true);
			LinkedList<Activity> acList = auxMgr.getLinkedActivityList();
			JSONArray tmpArray = new JSONArray();
			for(Activity tmpAc : acList){
				tmpArray.add(tmpAc.getJSONObject());
			}
			json.put("activities", tmpArray);
		}
		else if (recommendType.equals("friend")){
			// Get Friend Recommendation
			json.put("flag", true);
			LinkedList<User> userList = auxMgr.getLinkedUserList(currentUser.getUserID());
			JSONArray tmpArray = new JSONArray();
			for(User eachUser : userList){
				tmpArray.add(eachUser.getJSONObject());
				recommendedFriends.put(Integer.parseInt(eachUser.getUserID()), eachUser.getNickName() + " " + eachUser.getUserName());
			}
			json.put("friends", tmpArray);
			session.setAttribute("recommendedFriends", recommendedFriends);
			RequestDispatcher rs = request.getRequestDispatcher("index.jsp");
			rs.forward(request, response);
		}
		else{
			// Error workPattern
			json.put("flag", false);
			json.put("msg", "unsupported operation");
		}
		
		try{
			response.getWriter().write(json.toString());
			response.getWriter().flush();
			response.getWriter().close();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
}