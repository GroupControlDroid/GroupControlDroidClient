package com.groupcontroldroid.server.jetty.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.config.JettyConfig;
import com.groupcontroldroid.server.bean.OperationBean;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.util.JsonUtil;

/**
 * 文件上传servlet
 */
public class UploadServlet extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(UploadServlet.class);
	private static final long serialVersionUID = 1177480487302044371L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// InputStream in = req.getInputStream();
		PrintWriter out = resp.getWriter();
		String operationStr = null;

		req.setCharacterEncoding("UTF-8");
		Part part = req.getPart("file");
		OperationBean operationBean = new OperationBean();
		if (part != null) {
			String fileName = part.getSubmittedFileName();
			if (fileName.length() > 0) {
				part.write(fileName);

				File file = new File(System.getProperty("user.dir") + JettyConfig.UPLOAD_TMP_PATH);
				if (file.exists()) {
					logger.info("上传成功");
					operationBean.setCode(200);
					operationBean.setMsg(fileName);
				} else {
					logger.info("上传失败");
					operationBean.setCode(400);
					operationBean.setMsg("");
				}
			}else {
				SystemWSSender.error("请先浏览到您要安装的app安装包。");
			}
		}
		operationStr = JsonUtil.beanToJson(operationBean);
		logger.info(operationStr);
		out.println(operationStr);
		out.flush();
		out.close();
	}
}
