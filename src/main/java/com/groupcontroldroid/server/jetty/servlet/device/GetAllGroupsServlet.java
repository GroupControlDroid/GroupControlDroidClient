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

public class GetAllGroupsServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8827810473178721335L;
	private String authUrl = "http://" + AuthConfig.SITE_URL+"/group/get_all_groups.cgi";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long userid=AuthConfig.getUser_id();
		String token=AuthConfig.getToken();
		
		OutputStream os=resp.getOutputStream();
		
		if (userid!=null && token!=null) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("user_id", userid.toString());
			data.put("token", token);
			String returnAnswer = HttpRequest.get(authUrl+"?user_id="+userid+"&token="+token).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		} else {
			OperationBean respBean = new OperationBean();
			respBean.setCode(100);
			respBean.setMsg("token出错或userid出错");
			respBean.setRedirect(null);
			os.write(JsonUtil.beanToJson(respBean).getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	
	
	}
	
	
}
