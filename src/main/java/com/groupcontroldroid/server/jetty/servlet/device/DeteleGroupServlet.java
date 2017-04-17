package com.groupcontroldroid.server.jetty.servlet.device;

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
import com.groupcontroldroid.util.JsonUtil;

public class DeteleGroupServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1353879898793727789L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/group/detele_group_servlet.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String group_id=req.getParameter("group_id");
		String token = AuthConfig.getToken();
		Long userid=AuthConfig.getUser_id();
		
		OutputStream os = resp.getOutputStream();
		//OperationBean respBean = new OperationBean();
		
		
		if ( group_id!=null && userid!=null) {

				Map<String, String> data = new HashMap<String, String>();
				data.put("user_id", userid.toString());
				data.put("token", token);
				data.put("group_id", group_id);
				String returnAnswer = HttpRequest.post(authUrl).form(data).body();
				os.write(returnAnswer.getBytes("UTF-8"));
				// 返回给前端
		}
		os.flush();
		os.close();
	
	}
	
	

}
