package com.groupcontroldroid.server.jetty.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.groupcontroldroid.config.AuthConfig;

public class CommonFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filterChain) throws IOException, ServletException {
		//如果没有验证通过，那么就把请求转发到login.html
		HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		
		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");
		
		switch(httpServletRequest.getRequestURI()){
			case "/":
				if(AuthConfig.isContinueLockMain()){
					httpServletResponse.sendRedirect("/login.html");
				}else{
					httpServletResponse.sendRedirect("/index.html");
				}
				break;
			case "/index.html":
				if(AuthConfig.isContinueLockMain()){
					httpServletResponse.sendRedirect("/login.html");
				}else{
					filterChain.doFilter(httpServletRequest, httpServletResponse);
				}
				break;
			default:
				filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
