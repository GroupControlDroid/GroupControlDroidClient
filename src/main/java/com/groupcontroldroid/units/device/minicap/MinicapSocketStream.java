package com.groupcontroldroid.units.device.minicap;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.config.GlobalConfig;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;

/**
 * minicap socket流 生产者
 */
public class MinicapSocketStream extends Thread {
	final static Logger logger = LoggerFactory.getLogger(MinicapSocketStream.class);
	private static MinicapSocketStream instance = null;
	
	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("minicapSocketStream-%d").setDaemon(true).build();
	private static ExecutorService executor=null;
	
	static{

		executor=Executors.newFixedThreadPool(GlobalConfig.cpuNums,threadFactory);
	}
	
	public MinicapSocketStream(String str) {
		super(str);
	}
	

	public static synchronized MinicapSocketStream getMinicapSocketStreamInstance(String str) {
		if (instance == null) {
			instance = new MinicapSocketStream(str);
		}
		return instance;
	}

	@Override
	public void run() {
		try {
		
			while (true) {
				Set<String> sets=SocketCollection.getMiniCapKeySet();
				if (sets!=null) {
					for (String ser : sets) {
						executor.execute(new Runnable() {
							@Override
							public void run() {
								Minicap cap=SocketCollection.getMiniCap(ser);
								if (cap!=null) {
									cap.getPicture();
								}
							}
						});
					}
				}
				Thread.sleep(3);
			}
		} catch (InterruptedException e) {
			logger.error("屏幕监听socket线程中断",e);
			SystemWSSender.error("屏幕接收出错！");
		}
	}
}
