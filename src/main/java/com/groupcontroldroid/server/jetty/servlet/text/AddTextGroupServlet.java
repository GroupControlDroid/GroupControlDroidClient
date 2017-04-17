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

public class AddTextGroupServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3897884798491448493L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/add_textgroup.cgi";
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String user_id_str = req.getParameter("user_id");
		String name = req.getParameter("name");
		String token=AuthConfig.getToken();
		
		OutputStream os = resp.getOutputStream();
	
		if (user_id_str!=null&&name!=null&&token!=null) {
			Map<String, String> data=new HashMap<String,String>();
			data.put("user_id", user_id_str);
			data.put("name", name);
			data.put("token", token);
			
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	}
}
