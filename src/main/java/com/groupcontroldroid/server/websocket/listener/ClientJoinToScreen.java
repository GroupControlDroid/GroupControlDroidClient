package com.groupcontroldroid.server.websocket.listener;

import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;

/**
 * 浏览器客户端加入到屏幕传输中
 */
public class ClientJoinToScreen  implements DataListener<String> {
	final static Logger logger = LoggerFactory.getLogger(ClientJoinToScreen.class);
	
	@Override
	public void onData(SocketIOClient client, String serialNumber, AckRequest ackRequest) throws Exception {
		//添加客户端到屏幕中
				if (!ClientCollection.isContainsKey(serialNumber)) {
					ClientCollection.addClients(serialNumber, new TreeSet<UUID>());
				}
				if (!ClientCollection.getUUIDcontainsKey(serialNumber, client.getSessionId())) {
					ClientCollection.addUUID(serialNumber, client.getSessionId());
					logger.info("浏览器客户端加入到屏幕传输中：" + serialNumber + "," + client.getSessionId());
				}
	}
}
