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
import com.groupcontroldroid.server.bean.OperationBean;

public class AddTextServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2530136343786933823L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/add_textgroup.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String group_id_str = req.getParameter("group_id");
		String text=req.getParameter("text");
		Long user_id=AuthConfig.getUser_id();
		String token=AuthConfig.getToken();
		
		OutputStream os=resp.getOutputStream();
		OperationBean respBean=new OperationBean();
		if (group_id_str!=null&&text!=null&&user_id>0&&token!=null) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("token", token);
			data.put("user_id", user_id.toString());
			data.put("group_id", group_id_str);
			data.put("text", text);
			
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
		
	}
	
	

}
