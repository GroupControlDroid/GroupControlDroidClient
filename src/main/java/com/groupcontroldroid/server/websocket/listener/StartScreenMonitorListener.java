package com.groupcontroldroid.server.websocket.listener;

import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.entity.MinicapEntity;
import com.groupcontroldroid.server.bean.StartScreenBean;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.units.device.apk.ApkServiceSocketStream;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minicap.ImageConverter;
import com.groupcontroldroid.units.device.minicap.Minicap;
import com.groupcontroldroid.units.device.minicap.MinicapSocketStream;
import com.groupcontroldroid.units.device.screen.ScreenSwitches;
import com.groupcontroldroid.util.JsonUtil;

/**
 * 监听前端发过来的屏幕启动请求
 */
public class StartScreenMonitorListener implements DataListener<String> {
	final static Logger logger = LoggerFactory.getLogger(StartScreenMonitorListener.class);
	final static Semaphore semaphore = new Semaphore(4); // 信号量，只能4个线程同时访问

	@Override
	public void onData(SocketIOClient client, String json, AckRequest ackRequest) throws Exception {
		semaphore.acquire();

		StartScreenBean startScreenBean = JsonUtil.jsonTobean(json, StartScreenBean.class);
		if (startScreenBean == null) {
			logger.error("屏幕请求json出错");
			return;
		}

		String serialNumber = startScreenBean.getSerialNumber();
		ScreenSwitches.startScreen(serialNumber, startScreenBean.getWidth(), startScreenBean.getHeight());
		
		//添加客户端到屏幕中
		if (!ClientCollection.isContainsKey(serialNumber)) {
			ClientCollection.addClients(serialNumber, new TreeSet<UUID>());
		}
		if (!ClientCollection.getUUIDcontainsKey(serialNumber, client.getSessionId())) {
			ClientCollection.addUUID(serialNumber, client.getSessionId());
			logger.info("添加客户端：" + serialNumber + "," + client.getSessionId());
		}
		semaphore.release();// 释放信号量
	}
}
