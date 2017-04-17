package com.groupcontroldroid.entity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ApkServiceEntity {
	private String host;//service所在地址
	private int port;//service端口
	private Queue<byte[]> serviceDataQueue = new ConcurrentLinkedQueue<byte[]>();//手机端二进制数据队列

	public ApkServiceEntity(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	public Queue<byte[]> getServiceDataQueue() {
		return serviceDataQueue;
	}
	public void setServiceDataQueue(Queue<byte[]> serviceDataQueue) {
		this.serviceDataQueue = serviceDataQueue;
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
	@Override
	public String toString() {
		return "ApkServiceEntity [host=" + host + ", port=" + port + ", thread="  + "]";
	}
	
	
}
