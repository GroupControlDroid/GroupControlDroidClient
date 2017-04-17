package com.groupcontroldroid.server.jetty.servlet.text;

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

/**
 * @author zengyan
 * 删除text servlet
 */
public class DeteleTextServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4486200330262609803L;
	private String authUrl = "http://" + AuthConfig.SITE_URL + "/text/detele_text.cgi";


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String token = AuthConfig.getToken();
		String text_id=req.getParameter("text_id");
		OutputStream os = resp.getOutputStream();
		
		if (token!=null&&text_id!=null) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("token", token);
			data.put("text_id", text_id);
			
			String returnAnswer = HttpRequest.post(authUrl).form(data).body();
			os.write(returnAnswer.getBytes("UTF-8"));
		}
		os.flush();
		os.close();
	}
}
