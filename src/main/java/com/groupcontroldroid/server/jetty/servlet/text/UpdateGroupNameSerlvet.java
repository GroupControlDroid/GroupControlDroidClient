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

public class UpdateGroupNameSerlvet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8812255658393355721L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/update_textgroud_name.cgi";
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String group_id_str = req.getParameter("group_id");
		String token=AuthConfig.getToken();
		
		OutputStream os = resp.getOutputStream();
		
		if (name!=null&&group_id_str!=null&&token!=null) {
			Map<String, String> data=new HashMap<String,String>();
			data.put("name", name);
			data.put("groud_id", group_id_str);
			data.put("token", token);
			 
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
		
	}
	
	
	

}
