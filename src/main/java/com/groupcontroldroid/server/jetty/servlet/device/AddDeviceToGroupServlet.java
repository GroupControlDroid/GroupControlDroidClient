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

public class AddDeviceToGroupServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5445845587917808649L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/device/add_device_to_group.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 参数：user_id,token,device_id,group_id
		String sernumJson = req.getParameter("serial_number_list");
		// 这里拿到的是sernum的list的json
		String group_id = req.getParameter("group_id").trim();
		Long userid = AuthConfig.getUser_id();
		String token = AuthConfig.getToken();

		OutputStream os = resp.getOutputStream();
		//OperationBean respBean = new OperationBean();

		if (sernumJson != null && group_id != null && userid != null && token != null) {
			sernumJson = sernumJson.trim();
			if (sernumJson.length() > 0) {
				//List<String> sernumList = JsonUtil.jsonTobean(sernumJson, List.class);

				Map<String, String> data = new HashMap<String, String>();
				data.put("user_id", userid.toString());
				data.put("token", token);
				data.put("group_id", group_id);
				data.put("serial", sernumJson);
				String returnAnswer = HttpRequest.post(authUrl).form(data).body();
				os.write(returnAnswer.getBytes("UTF-8"));
				// 返回给前端

//				respBean.setMsg("选中的设备断线");
//				respBean.setCode(100);
//				respBean.setRedirect(null);
//				os.write(JsonUtil.beanToJson(respBean).getBytes("UTF-8"));
				// 把错误的消息返回给前端

			}
		}
		os.flush();
		os.close();

	}

}
