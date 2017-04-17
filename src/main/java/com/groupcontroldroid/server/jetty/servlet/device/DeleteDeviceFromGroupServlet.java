package com.groupcontroldroid.server.jetty.servlet.device;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.server.bean.OperationBean;
import com.groupcontroldroid.server.jetty.JettyServer;
import com.groupcontroldroid.util.JsonUtil;

public class DeleteDeviceFromGroupServlet extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(DeleteDeviceFromGroupServlet.class);
	
	private static final long serialVersionUID = 7192514635422476688L;

	private String authUrl = "http://" + AuthConfig.SITE_URL + "/group/delete_device_from_group.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String sernumListJson=req.getParameter("serial_number_list");
		String group_id=req.getParameter("group_id");
		String token = AuthConfig.getToken();
		Long userid=AuthConfig.getUser_id();
		
		OutputStream os = resp.getOutputStream();
		OperationBean respBean = new OperationBean();
		
		if (sernumListJson!=null && group_id!=null && userid!=null) {
			sernumListJson = sernumListJson.trim();
			if (sernumListJson.length()>0) {				
				Map<String, String> data = new HashMap<String, String>();
				data.put("user_id", userid.toString());
				data.put("token", token);
				data.put("group_id", group_id);
				data.put("serial_number_list", sernumListJson);
				String returnAnswer = HttpRequest.post(authUrl).form(data).body();
				os.write(returnAnswer.getBytes("UTF-8"));
				logger.info(returnAnswer);
				// 返回给前端
			}else {
				respBean.setMsg("没有选中");
				respBean.setCode(100);
				respBean.setRedirect(null);
				os.write(JsonUtil.beanToJson(respBean).getBytes("UTF-8"));
				// 把错误的消息返回给前端
			}
		}
		os.flush();
		os.close();
	}
}
