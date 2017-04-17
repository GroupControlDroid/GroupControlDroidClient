package com.groupcontroldroid.server.jetty.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.security.lib.Base64;
import com.groupcontroldroid.security.lib.GlobalRSAKey;
import com.groupcontroldroid.security.lib.RSAEncrypt;
import com.groupcontroldroid.server.bean.AuthResponseBean;
import com.groupcontroldroid.server.bean.OperationBean;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.util.JsonUtil;

public class LoginServlet extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(LoginServlet.class);

	private static final long serialVersionUID = -7931712745055322719L;

	private String authUrl = "http://" + AuthConfig.SITE_URL + "/auth.cgi";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		OutputStream os = resp.getOutputStream();
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Content-type", "text/json;charset=UTF-8");
		OperationBean operationBean = new OperationBean();
		if (username != null && password != null) {
			if (username.length() > 0 && password.length() > 0) {
				String plainText = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username,password);
				byte[] cipherData = null;
				try {
					cipherData = RSAEncrypt.encrypt(GlobalRSAKey.RSAPublicKey,
							plainText.getBytes());
				} catch (Exception e) {
					logger.error("加密出错",e);
				}
				String cipher = Base64.encode(cipherData);
				Map<String, String> data = new HashMap<String, String>();
				data.put("ciphertext", cipher);
				String encryptedStr = HttpRequest.post(authUrl).form(data)
						.body();
				//logger.info(authUrl);
				//logger.info(encryptedStr);
				if (encryptedStr != null) {
					byte[] resByte = null;
					try {
						resByte = RSAEncrypt.decrypt(GlobalRSAKey.RSAPublicKey,
								Base64.decode(encryptedStr));
					} catch (Exception e) {
						logger.error("rsa解密出错", e);
					}
					String restr = new String(resByte);
					logger.info(restr);
					AuthResponseBean authResponseBean = JsonUtil.jsonTobean(
							restr, AuthResponseBean.class);
					if (authResponseBean != null
							&& authResponseBean.getUser_id() > 0) {
						operationBean.setCode(200);
						operationBean.setRedirect("/index.html");
						operationBean.setMsg("登陆成功");
						
						AuthConfig.setUser_id(authResponseBean.getUser_id());
						AuthConfig.setUsername(authResponseBean.getUsername());
						AuthConfig.setToken(authResponseBean.getToken());
						AuthConfig.setDeviceLimit(authResponseBean.getDevice_count());
						AuthConfig.setOutTime(authResponseBean.getOut_time());
						
						os.write(JsonUtil.beanToJson(operationBean).getBytes(
								"UTF-8"));
						AuthConfig.setIsContinueLockMain(false); // 验证通过，允许程序往下执行
						os.close();
						return;
					} else {
						operationBean.setCode(300);
						operationBean.setRedirect("/");
						operationBean.setMsg("用户名或密码出错，请重新尝试或更新系统");
					}
				} else {
					operationBean.setCode(300);
					operationBean.setRedirect("/");
					operationBean.setMsg("登陆失败");
				}

			} else {
				operationBean.setCode(300);
				operationBean.setMsg("用户名和密码不能为空");
			}
		} else {
			operationBean.setCode(300);
			operationBean.setMsg("非法提交");
		}
		os.write(JsonUtil.beanToJson(operationBean).getBytes("UTF-8"));
		os.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println("<h1>Hello</h1>");
	}
}
