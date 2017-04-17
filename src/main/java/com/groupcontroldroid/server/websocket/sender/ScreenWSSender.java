package com.groupcontroldroid.server.websocket.sender;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.groupcontroldroid.server.websocket.WebsocketServer;

/**
 * 屏幕图像数据发送者
 */
public class ScreenWSSender {
	final static Logger logger = LoggerFactory.getLogger(ScreenWSSender.class);
	private static SocketIOServer server = WebsocketServer.getServer();
	
	/**
	 * 发送图像数据到客户端
	 * @param client 客户端
	 * @param imageByte 图像数据
	 */
	public static void sendScreenImageBinary(String serialNumber,UUID uuid,byte[] imageByte){
		SocketIOClient client = server.getClient(uuid);
		if (client!=null) {
			client.sendEvent("screen.image_"+serialNumber, imageByte);
		}else {
			logger.info("找不到该客户端"+uuid.toString());
		}
		
	}
}
