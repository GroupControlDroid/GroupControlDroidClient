package com.groupcontroldroid.server.jetty.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.groupcontroldroid.config.AuthConfig;

/**
 * 获取用户信息servlet
 * 
 */
public class UserinfoServlet extends HttpServlet {
	private String jsonFormat = "{\"username\":\"%s\",\"device_limit\":\"%d\",\"out_time\":\"%s\"}";
	private static final long serialVersionUID = 2656064576162237269L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OutputStream os = resp.getOutputStream();
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Content-type", "text/json;charset=UTF-8");
		String json = String.format(jsonFormat, AuthConfig.getUsername(),AuthConfig.getDeviceLimit(),AuthConfig.getOutTime());
		os.write(json.getBytes("UTF-8"));
		os.close();
	}
}
