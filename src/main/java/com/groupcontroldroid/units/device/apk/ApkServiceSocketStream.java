package com.groupcontroldroid.units.device.apk;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.server.websocket.collection.SocketCollection;

public class ApkServiceSocketStream extends Thread {
	final static Logger logger = LoggerFactory.getLogger(ApkServiceSocketStream.class);
	private static ApkServiceSocketStream instance = null;

	public ApkServiceSocketStream(String name) {
		super(name);
	}

	public static synchronized ApkServiceSocketStream getApkStream(String name) {
		if (instance == null) {
			instance = new ApkServiceSocketStream(name);
		}
		return instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Set<String> sets = SocketCollection.getApkServiceKetSet();
				if (sets != null) {
					for (String ser : sets) {
						ApkService service = SocketCollection.getApkService(ser);
						if (service != null) {
//							 logger.info("设备号为"+ser+"的手机进行读数据！！");
							service.getDeviceState();
						} else {
							logger.info(ser + "这个手机没有apkservice!!!");
						}
					}
				}

				Thread.sleep(2000);
			}
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
