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

public class DeteleTextGroupServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 199571731749297031L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/detele_textgroup.cgi";
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String group_id=req.getParameter("group_id");
		String user_id=req.getParameter("user_id");
		String token=AuthConfig.getToken();
		
		OutputStream os=resp.getOutputStream();
		if (group_id!=null&&user_id!=null&&token!=null) {
			Map<String, String> data=new HashMap<String,String>();
			
			data.put("group_id", group_id);
			data.put("user_id", user_id);
			data.put("token", token);
			
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	}
}
