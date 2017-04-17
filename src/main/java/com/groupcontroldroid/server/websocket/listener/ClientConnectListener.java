package com.groupcontroldroid.server.websocket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.groupcontroldroid.server.websocket.sender.DeviceWSSender;

/**
 * 客户端连接监听
 */
public class ClientConnectListener implements ConnectListener {
	final static Logger logger = LoggerFactory.getLogger(ClientConnectListener.class);
	
	@Override
	public void onConnect(SocketIOClient client) {
		logger.info("websocket客户端连接:"+client.getSessionId()+" IP:"+client.getRemoteAddress());
	}

}
