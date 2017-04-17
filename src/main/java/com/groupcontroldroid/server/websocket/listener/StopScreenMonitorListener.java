package com.groupcontroldroid.server.websocket.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.collect.Iterators;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minicap.Minicap;
import com.groupcontroldroid.units.device.minitouch.Minitouch;

/**
 * 坚挺客户端离开设备屏幕图像监听
 */
public class StopScreenMonitorListener implements DataListener<String> {
	final static Logger logger = LoggerFactory
			.getLogger(StopScreenMonitorListener.class);
	final static Semaphore semaphore = new Semaphore(4); // 信号量，只能4个线程同时访问

	@Override
	public void onData(SocketIOClient client, String serialNumber,
			AckRequest ackRequest) throws Exception {
		
		DeviceEntity deviceEntity = DeviceContainerHandler
				.getDevice(serialNumber);
		if (deviceEntity != null && deviceEntity.getMinicapEntity()!=null&&!deviceEntity.getMinicapEntity().isStoping()) {
			semaphore.acquire();
			deviceEntity.getMinicapEntity().setStoping(true);
			Minicap cap = SocketCollection.getMiniCap(serialNumber);
			if (cap != null) {
				cap.closeAll();
			}
			SocketCollection.removeMiniCap(serialNumber);

			// 从屏幕监听客户端列表中移除下线客户端

			if (ClientCollection.isContainsKey(serialNumber)) {
				List<UUID> removeList = new ArrayList<UUID>();
				Set<UUID> clients = ClientCollection.getClients(serialNumber);
				if (clients != null) {
					for (UUID client_uuid : clients) {
						if (client_uuid.equals(client.getSessionId())) {
							removeList.add(client_uuid);
						}
					}
					if (removeList != null) {
						for (UUID uuid : removeList) {
							ClientCollection.removeUUID(serialNumber, uuid);
							logger.info("stop从屏幕监听客户端列表中移除:"
									+ client.getSessionId() + " IP:"
									+ client.getRemoteAddress());
						}
					}
				}
			}
			deviceEntity.getMinicapEntity().setStoping(false);
			semaphore.release();
		}
	}
}
