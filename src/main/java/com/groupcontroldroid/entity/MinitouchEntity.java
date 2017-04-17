package com.groupcontroldroid.entity;

import java.util.concurrent.ExecutorService;

/**
 * MinitouchEntity实体类
 */
public class MinitouchEntity {
	private String host;//minitouch所在地址
	private int port;//minitouch端口
	private Thread touchCmdThread;//进程
//	private ExecutorService executor;
	public MinitouchEntity(){
		
	}
public MinitouchEntity(String host, int port, Thread touchCmdThread) {
	super();
	this.host = host;
	this.port = port;
	this.touchCmdThread = touchCmdThread;
}
public String getHost() {
	return host;
}
public void setHost(String host) {
	this.host = host;
}
public int getPort() {
	return port;
}
public void setPort(int port) {
	this.port = port;
}
public Thread getTouchCmdThread() {
	return touchCmdThread;
}
public void setTouchCmdThread(Thread touchCmdThread) {
	this.touchCmdThread = touchCmdThread;
}

	
	
	
	
	
	
	
}
