package com.groupcontroldroid.server.jetty.servlet;

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
 * 编辑设备组信息
 */
public class EditGroupServlet extends HttpServlet {
	private static final long serialVersionUID = 7202419305278085567L;
	private String authUrl = "http://" + AuthConfig.SITE_URL
			+ "/group/edit_group.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String name = req.getParameter("name");//设备组名
		String group_id = req.getParameter("group_id");//设备组id
		Long user_id = AuthConfig.getUser_id();
		OutputStream os = resp.getOutputStream();
		if (name != null && group_id != null) {
			name = name.trim();
			group_id = group_id.trim();
			if (name.length() > 0 && group_id.length() > 0) {
				Map<String, String> data = new HashMap<>();
				data.put("name", name);
				data.put("group_id", group_id);
				data.put("user_id", user_id.toString());
				data.put("token", AuthConfig.getToken());
				String respStr = HttpRequest.post(authUrl).form(data).body();
				if(respStr != null){
					os.write(respStr.getBytes("UTF-8"));
				}
			}else{
				OperationBean operationBean = new OperationBean("组名、组id不能为空", 315, "");
				String jsonStr = JsonUtil.beanToJson(operationBean);
				os.write(jsonStr.getBytes("UTF-8"));
			}
		}
		os.flush();
		os.close();
	}
}
