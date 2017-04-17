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

public class GetAllTextsServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4606422497400835335L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/get_all_text.cgi";


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String token = AuthConfig.getToken();
		Long user_id=AuthConfig.getUser_id();
		OutputStream os = resp.getOutputStream();
		
		if (token!=null&&user_id>0) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("token", token);
			data.put("user_id", user_id.toString());
			
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
			
		}
		os.flush();
		os.close();
	}
}
