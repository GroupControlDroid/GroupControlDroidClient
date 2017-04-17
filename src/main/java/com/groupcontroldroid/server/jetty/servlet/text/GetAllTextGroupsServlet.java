package com.groupcontroldroid.server.jetty.servlet.text;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.kevinsawicki.http.HttpRequest;
import com.groupcontroldroid.config.AuthConfig;

public class GetAllTextGroupsServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8317945063342203249L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/get_all_textgroup.cgi";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String user_id =req.getParameter("user_id");
		String token=AuthConfig.getToken();
		OutputStream os=resp.getOutputStream();
		
		if (user_id!=null&&token!=null) {
			Map<String, String> data=new HashMap<String,String>();
			data.put("user_id", user_id);
			data.put("token", token);
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	}
}
