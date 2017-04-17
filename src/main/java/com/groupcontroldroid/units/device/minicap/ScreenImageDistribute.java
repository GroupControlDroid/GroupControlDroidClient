package com.groupcontroldroid.units.device.minicap;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.sender.ScreenWSSender;

/**
 * 屏幕数据分发器
 */
public class ScreenImageDistribute extends Thread {
	final static Logger logger = LoggerFactory.getLogger(ScreenImageDistribute.class);
	final static Queue<ScreenImageItem> screenImageQueue = new ConcurrentLinkedQueue<ScreenImageItem>();

	public ScreenImageDistribute(String string) {
		super(string);
	}

	public static void add(String serialNumber, byte[] bytes) {
		if (serialNumber != null && bytes != null) {
			ScreenImageItem screenImageItem = new ScreenImageItem(serialNumber,
					bytes);
			screenImageQueue.add(screenImageItem);
		}
	}

	public static ScreenImageItem poll() {
		return screenImageQueue.poll();
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		try {
			while (!currentThread.isInterrupted()) {
				ScreenImageItem screenImageItem = ScreenImageDistribute.poll();
				if (screenImageItem != null) {
					String serialNumber = screenImageItem.getSerialNumber();
					Set<UUID> uuids = ClientCollection.getClients(serialNumber);
					if (uuids != null) {
						for (UUID uuid : uuids) {
							ScreenWSSender.sendScreenImageBinary(serialNumber,
									uuid, screenImageItem.getBytes());
						}
					}
				}
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			logger.error("屏幕分发线程退出",e);
		}
	}
}
