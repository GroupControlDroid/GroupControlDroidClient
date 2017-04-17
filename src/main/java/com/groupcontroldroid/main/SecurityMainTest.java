package com.groupcontroldroid.main;

import java.util.HashMap;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;
import com.groupcontroldroid.security.lib.Base64;
import com.groupcontroldroid.security.lib.GlobalRSAKey;
import com.groupcontroldroid.security.lib.RSAEncrypt;

public class SecurityMainTest {

	public static void main(String args[]) throws Exception {
		Map<String, String> data = new HashMap<String, String>();
		
		// 公钥加密过程
//		String plainText = "{\"username\":\"test\",\"password\":\"123456\"}";
//		byte[] cipherData = RSAEncrypt.encrypt(GlobalRSAKey.RSAPublicKey,
//				plainText.getBytes());
//		String cipher = Base64.encode(cipherData);
//		data.put("ciphertext", cipher);
//		String resultStr = HttpRequest.post("http://127.0.0.1:8080/auth.cgi")
//				.form(data).body();
//		
//		//解密前数据
//		System.out.println(resultStr);
		
		data.put("token","12d3s");
		data.put("user_id", "1");
		String resultStr =  HttpRequest.post("http://127.0.0.1:8080/token.cgi").form(data).body();
		
		//解密后的数据
		byte[] res = RSAEncrypt.decrypt(GlobalRSAKey.RSAPublicKey, Base64.decode(resultStr));
		String restr = new String(res);
		System.out.println(restr);
	}
}
