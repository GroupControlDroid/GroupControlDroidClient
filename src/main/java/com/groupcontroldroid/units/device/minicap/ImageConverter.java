package com.groupcontroldroid.units.device.minicap;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.config.ApkConfig;
import com.groupcontroldroid.config.GlobalConfig;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;

public class ImageConverter extends Thread {
	final static Logger logger = LoggerFactory.getLogger(ImageConverter.class);
	private static ImageConverter instance = null;

	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("ImageConverterSendPicThread-%d").setDaemon(true)
			.build();
	// 线程池，这里主要用于图像转换发送
	private final static ExecutorService executorService = Executors
			.newFixedThreadPool(GlobalConfig.cpuNums, threadFactory);

	public ImageConverter(String str) {
		super(str);
	}

	public static synchronized ImageConverter getImageConverter(String str) {
		if (instance == null) {
			instance = new ImageConverter(str);
		}
		return instance;
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		try {
			while (!currentThread.isInterrupted()) {
				Set<String> sets = SocketCollection.getMiniCapKeySet();
				if (sets != null) {
					for (String ser : sets) {
						executorService.execute(new Runnable() {
							@Override
							public void run() {
								Minicap cap = SocketCollection.getMiniCap(ser);
								if (cap != null) {
									cap.sendPicture();
								}
							}
						});
					}
				}
				Thread.sleep(3);
			}
		} catch (InterruptedException e) {
			logger.error("屏幕数据转换线程中断", e);
			SystemWSSender.error("屏幕监听出错！");
		}
	}

}