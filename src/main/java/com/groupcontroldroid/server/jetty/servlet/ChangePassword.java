package com.groupcontroldroid.server.jetty.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.security.lib.GlobalRSAKey;
import com.groupcontroldroid.security.lib.RSAEncrypt;
import com.groupcontroldroid.security.lib.Base64;
import com.groupcontroldroid.server.bean.AuthResponseBean;
import com.groupcontroldroid.server.bean.OperationBean;
import com.groupcontroldroid.util.JsonUtil;

public class ChangePassword extends HttpServlet{
	private static final long serialVersionUID = 4008241732774654485L;
	final static Logger logger = LoggerFactory.getLogger(UploadServlet.class);
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/changepassword.cgi";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Content-type", "text/json;charset=UTF-8");
		OperationBean operationBean = new OperationBean();
		OutputStream os = resp.getOutputStream();
		
		String username=req.getParameter("username");
		String oldPassword=req.getParameter("oldPassword");
		String newPassword1=req.getParameter("newPassword1");
		String token=AuthConfig.getToken();
		
		
		if (username!=null&&oldPassword!=null) {
			String plainText = String.format("{\"username\":\"%s\",\"user_id\":\"%d\",\"oldPassword\":\"%s\","
					+ "\"newPassword1\":\"%s\",\"token\":\"%s\"}", username,AuthConfig.getUser_id()
					,oldPassword,newPassword1,token);
			logger.info(plainText);
			byte[] cipherData=null;
			try {
				cipherData=RSAEncrypt.encrypt(GlobalRSAKey.RSAPublicKey, plainText.getBytes());
//				进行加密
			} catch (Exception e) {
				logger.error("加密出错",e);
			}
			String cipher=Base64.encode(cipherData);
			Map<String, String> data = new HashMap<String, String>();
			data.put("ciphertext", cipher);
			String encryptedStr = HttpRequest.post(authUrl).form(data)
					.body();
//			int answer=encryptedStr.
			//logger.info("现在post过去！！！"+encryptedStr);
//			去到服务器验证，现在返回
			if (encryptedStr.equals("1")) {
				operationBean.setCode(100);
			}else {
				operationBean.setCode(200);
			}
		}
		os.write(JsonUtil.beanToJson(operationBean).getBytes("UTF-8"));
		os.flush();
		os.close();
		
		
		
		
		
		
		
	}
	
	
	

}
