package com.groupcontroldroid.entity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.groupcontroldroid.units.device.minicap.MinicapManager;

/**
 * Minicap实体类
 */
public class MinicapEntity {
	private String host;//minicap所在地址
	private int port;//minicap端口
	
	private Queue<byte[]> imageDataQueue = new ConcurrentLinkedQueue<byte[]>();//图像二进制数据队列
	private MinicapManager minicapManager;
	private Thread minicapCMDThread;//minicap命令进程
	
	/*枚举minicap状态*/
	public enum Status{
		CLOSED,RUNNING
	}
	
	private Status status = Status.CLOSED;//默认为已关闭状态
	private boolean isStarting = false;//minicap是否启动中
	private boolean isStoping = false;//minicap是否关闭中
	
	public MinicapEntity(){
		
	}
	
	public MinicapEntity(String host, int port, Thread minicapCMDThread) {
		super();
		this.host = host;
		this.port = port;
		this.minicapCMDThread = minicapCMDThread;
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
	public Thread getMinicapCMDThread() {
		return minicapCMDThread;
	}
	public void setMinicapCMDThread(Thread minicapCMDThread) {
		this.minicapCMDThread = minicapCMDThread;
	}

	public Queue<byte[]> getImageDataQueue() {
		return imageDataQueue;
	}

	public void setImageDataQueue(Queue<byte[]> imageDataQueue) {
		this.imageDataQueue = imageDataQueue;
	}

	public MinicapManager getMinicapManager() {
		return minicapManager;
	}

	public void setMinicapManager(MinicapManager minicapManager) {
		this.minicapManager = minicapManager;
	}

	public synchronized boolean isStarting() {
		return isStarting;
	}

	public synchronized void setStarting(boolean isStarting) {
		this.isStarting = isStarting;
	}

	public synchronized boolean isStoping() {
		return isStoping;
	}

	public synchronized void setStoping(boolean isStoping) {
		this.isStoping = isStoping;
	}

	public synchronized Status getStatus() {
		return status;
	}

	public synchronized void setStatus(Status status) {
		this.status = status;
	}
}
