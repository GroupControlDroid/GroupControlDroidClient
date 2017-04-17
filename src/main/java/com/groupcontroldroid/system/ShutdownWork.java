package com.groupcontroldroid.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.util.AdbUtil;

/**
 * 系统退出时需要完成的工作
 */
public class ShutdownWork implements Runnable {
	final static Logger logger = LoggerFactory.getLogger(ShutdownWork.class);
	
	@Override
	public void run() {
		if(!AuthConfig.isContinueLockMain()){
			SystemWSSender.msg("主控服务端系统已下线");
		}
	}

}
