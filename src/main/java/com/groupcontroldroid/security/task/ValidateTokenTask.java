package com.groupcontroldroid.security.task;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.security.bean.TokenBean;
import com.groupcontroldroid.security.lib.Base64;
import com.groupcontroldroid.security.lib.GlobalRSAKey;
import com.groupcontroldroid.security.lib.RSAEncrypt;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.util.JsonUtil;

public class ValidateTokenTask extends TimerTask {
	final static Logger logger = LoggerFactory.getLogger(ValidateTokenTask.class);
	Map<String, String> data = new HashMap<String, String>();

	@Override
	public void run() {
		String resultStr = null;
		data.put("token", AuthConfig.getToken());
		data.put("user_id", new Long(AuthConfig.getUser_id()).toString());
		
		try{
		resultStr = HttpRequest.post("http://"+AuthConfig.SITE_URL+"/token.cgi")
				.form(data).body();
		}catch(HttpRequestException e){
			System.exit(1);
		}
		// 解密后的数据
		byte[] res = null;
		try {
			res = RSAEncrypt.decrypt(GlobalRSAKey.RSAPublicKey,
					Base64.decode(resultStr));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		String responseJsonStr = new String(res);
		TokenBean tokenBean = JsonUtil.jsonTobean(responseJsonStr, TokenBean.class);
		if(tokenBean!=null && tokenBean.isIs_success()){
			//token验证通过
			AuthConfig.setToken(tokenBean.getToken()); //更新token
			//logger.info("token 验证通过");
		}else{
			SystemWSSender.error("请不要多个pc登录同一账号");
			logger.info("token 验证失败");
			System.exit(1);
		}
	}

}
