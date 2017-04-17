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

/**
 * 添加设备组
 */
public class AddGroupServlet extends HttpServlet {

	private static final long serialVersionUID = -6416549421253834685L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/group/add_group.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String name = req.getParameter("name");
		String token=AuthConfig.getToken();
		Long userid=AuthConfig.getUser_id();
		OutputStream os = resp.getOutputStream();
		OperationBean respBean = new OperationBean();
		
		if(name != null&&token!=null&&userid!=null){
			name=name.trim();
			if (name.length()>0) {
				
				Map<String, String> data = new HashMap<String, String>();
				data.put("name", name);
				data.put("token", token);
				data.put("user_id", userid.toString());
				String returnAnswer = HttpRequest.post(authUrl).form(data)
						.body();
				os.write(returnAnswer.getBytes("UTF-8"));
				
			}
		}else{
			respBean.setMsg("设备组名称不能为空");
			respBean.setCode(100);
			respBean.setRedirect(null);
			os.write(JsonUtil.beanToJson(respBean).getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	}

	
}
