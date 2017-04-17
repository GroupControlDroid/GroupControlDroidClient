package com.groupcontroldroid.server.jetty;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.config.JettyConfig;
import com.groupcontroldroid.server.jetty.filter.CommonFilter;
import com.groupcontroldroid.server.jetty.servlet.ChangePassword;
import com.groupcontroldroid.server.jetty.servlet.EditGroupServlet;
import com.groupcontroldroid.server.jetty.servlet.FileBrowseServlet;
import com.groupcontroldroid.server.jetty.servlet.LoginServlet;
import com.groupcontroldroid.server.jetty.servlet.UploadServlet;
import com.groupcontroldroid.server.jetty.servlet.UserinfoServlet;
import com.groupcontroldroid.server.jetty.servlet.device.AddDeviceToGroupServlet;
import com.groupcontroldroid.server.jetty.servlet.device.AddGroupServlet;
import com.groupcontroldroid.server.jetty.servlet.device.DeleteDeviceFromGroupServlet;
import com.groupcontroldroid.server.jetty.servlet.device.DeteleGroupServlet;
import com.groupcontroldroid.server.jetty.servlet.device.GetAllGroupsServlet;
import com.groupcontroldroid.server.jetty.servlet.device.GetGroupsServlet;
/**
 * Created by hugeterry
 * Date: 16/7/18 08:57
 */
public class JettyServer extends Thread {
	final static Logger logger = LoggerFactory.getLogger(JettyServer.class);
	private static Server server = null;
	
	static{
		if (server==null) {
			server=new Server(JettyConfig.port);
		}
	}
	
	public JettyServer(String name){
		super(name);
	}
	
	public static Server getServer() {
		return server;
	}

	public void run() {
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setContextPath("/");
		servletContextHandler.setResourceBase("./res");
		servletContextHandler.addFilter(CommonFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));//增加过滤器
		servletContextHandler.addServlet(LoginServlet.class, "/login.cgi");
		servletContextHandler.addServlet(UserinfoServlet.class, "/userinfo.cgi");
		servletContextHandler.addServlet(FileBrowseServlet.class, "/file_browse.cgi");
		servletContextHandler.addServlet(DefaultServlet.class, "/");
		servletContextHandler.addServlet(ChangePassword.class, "/changepassword.cgi");
		servletContextHandler.addServlet(AddDeviceToGroupServlet.class, "/device/add_device_to_group.cgi");
		servletContextHandler.addServlet(AddGroupServlet.class, "/group/add_group.cgi");
		servletContextHandler.addServlet(GetGroupsServlet.class	, "/group/get_groups.cgi");
		servletContextHandler.addServlet(EditGroupServlet.class, "/group/edit_group.cgi");
		servletContextHandler.addServlet(GetAllGroupsServlet.class, "/group/get_all_groups.cgi");
		servletContextHandler.addServlet(DeteleGroupServlet.class, "/group/delete_group.cgi");
		servletContextHandler.addServlet(AddDeviceToGroupServlet.class, "/group/add_device_to_group.cgi");
		servletContextHandler.addServlet(DeleteDeviceFromGroupServlet.class,"/group/delete_device_from_group.cgi");
		
		ServletHolder fileUploadServletHolder = new ServletHolder(new UploadServlet());
		fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(System.getProperty("user.dir") +JettyConfig.UPLOAD_TMP_PATH));
		servletContextHandler.addServlet(fileUploadServletHolder, "/upload.cgi");
		
		servletContextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
        
        server.setHandler(servletContextHandler);
        try {
			server.start();
			server.join();
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
}
