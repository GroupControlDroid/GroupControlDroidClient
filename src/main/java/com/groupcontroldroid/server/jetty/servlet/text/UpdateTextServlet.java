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

public class UpdateTextServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1794719572258272034L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/update_text.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String group_id_str = req.getParameter("group_id");
		String text_id_str=req.getParameter("text_id");
		String text=req.getParameter("text");
		String token=AuthConfig.getToken();
		
		OutputStream os = resp.getOutputStream();
		
		if (group_id_str!=null&&text_id_str!=null&&text!=null&&token!=null) {
			Map<String, String> data=new HashMap<String ,String>();
			data.put("group_id", group_id_str);
			data.put("text_id", text_id_str);
			data.put("text", text);
			data.put("token", token);
			
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	}
}
