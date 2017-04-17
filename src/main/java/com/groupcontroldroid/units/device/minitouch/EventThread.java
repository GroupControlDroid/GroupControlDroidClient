package com.groupcontroldroid.units.device.minitouch;



import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.ReleasedEvent;
import com.groupcontroldroid.server.bean.TouchDraggedEvent;
import com.groupcontroldroid.server.bean.TouchEntityEvent;
import com.groupcontroldroid.server.bean.TouchEvent;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;

/**
 * @author zengyan
 *	 点击事件线程  用来监听消息队列和触发点击动作
 */
/**
 *
 */
public class EventThread extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(EventThread.class);
	private static LinkedBlockingDeque<Runnable> queue=new LinkedBlockingDeque<>();
	
	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("minitouchThread-%d").setDaemon(true).build();
	private static ExecutorService executor=null;
	
//	public static ThreadPoolExecutor getExecutor() {
//		return executor;
//	}

//	public static void setExecutor(ThreadPoolExecutor executor) {
//		EventThread.executor = executor;
//	}


	static{

		executor=Executors.newFixedThreadPool(10,threadFactory);
	}


	public EventThread(String arg0) {
		super(arg0);
	}

	@Override
	public void run() {
		EventQueue queue = EventQueue.getinstance();
		Thread currentThread = Thread.currentThread();
		try {
			while (!currentThread.isInterrupted()) {
				if (!queue.isEmpty()) {
					Object event = queue.pollObject();
					if (event != null) {
						Class<? extends Object> c = event.getClass();
						if (c.equals(TouchEntityEvent.class)) {
							touchEntityEvent((TouchEntityEvent) event);
						} else if (c.equals(TouchEvent.class)) {
							touchEvent((TouchEvent) event);
						} else if (c.equals(ReleasedEvent.class)) {
							touchReleased((ReleasedEvent) event);
						} else if (c.equals(TouchDraggedEvent.class)) {
							touchDragged((TouchDraggedEvent) event);
						}
					}
				}
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			logger.error("线程中断", e);
		}
	}

	private void touchDragged(TouchDraggedEvent event) {
		for (String serialNumber : event.getSerialNumList()) {
			DeviceEntity device = DeviceContainerHandler.getDevice(serialNumber);
			if (device!=null) {
				Minitouch toucher = SocketCollection.getMiniTouch(serialNumber);
				if (toucher == null) {
					toucher = new Minitouch(device);
					SocketCollection.addMiniTouch(serialNumber, toucher);
				}
				toucher.dragged((int) event.getX(), (int) event.getY());
			}
			
		}

	}

	public void touchReleased(ReleasedEvent event) {
		List<String> serialNumberList = event.getSerialNumList();
		if(serialNumberList!=null){
			for (String serialNumber : event.getSerialNumList()) {
				DeviceEntity device = DeviceContainerHandler.getDevice(serialNumber);
				if (device!=null) {
					Minitouch toucher = SocketCollection.getMiniTouch(serialNumber);
					if (toucher == null) {
						toucher = new Minitouch(device);
						SocketCollection.addMiniTouch(serialNumber, toucher);
					}
					toucher.released();
				}
			}
		}
	}

	/**
	 * 普通点击事件
	 * 
	 * @param event
	 */

	public void touchEvent(TouchEvent event) {
		for (String serialNumber : event.getSerialNumList()) {
			DeviceEntity device = DeviceContainerHandler.getDevice(serialNumber);
			if (device!=null) {
				Minitouch toucher = SocketCollection.getMiniTouch(serialNumber);
				if (toucher == null) {
					toucher = new Minitouch(device);
					SocketCollection.addMiniTouch(serialNumber, toucher);
				}
				toucher.pressed((int) event.getX(), (int) event.getY());
			}
		}

	}
	
	
	/**
	 * 点击实体按键
	 * 
	 * @param touchEntityEvent
	 */
	public void touchEntityEvent(TouchEntityEvent touchEntityEvent) {
		final TouchEntityEvent event=touchEntityEvent;
		for (final String serialNumber : touchEntityEvent.getSerialNumList()) {
			
			
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					DeviceEntity device = DeviceContainerHandler.getDevice(serialNumber);
					if (device!=null) {
						Minitouch toucher = SocketCollection.getMiniTouch(serialNumber);
						if (toucher == null) {
							toucher = new Minitouch(device);
							SocketCollection.addMiniTouch(serialNumber, toucher);
						}
						toucher.clinkEntity(event.getEntityKey());
					}
				
					
				}
			});
			
		}
	}

}
