package com.groupcontroldroid.server.websocket.sender;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.groupcontroldroid.server.websocket.WebsocketServer;

/**
 * 系统消息广播
 */
public class SystemWSSender {
	private static SocketIOServer server = WebsocketServer.getServer();
	
	/**
	 * 常规消息发送
	 * @param text 消息文本内容
	 */
	public static void msg(String text){
		server.getBroadcastOperations().sendEvent("system.msg", text);
	}
	
	public static void msg(SocketIOClient client,String text){
		if(client != null){
			client.sendEvent("system.msg", text);
		}
	}
	
	/**
	 * 警告消息发送
	 * @param text 消息文本内容
	 */
	public static void warn(String text){
		server.getBroadcastOperations().sendEvent("system.warn", text);
	}
	
	public static void warn(SocketIOClient client,String text){
		server.getBroadcastOperations().sendEvent("system.warn", text);
	}
	
	/**
	 * 错误消息发送
	 * @param text 消息文本内容
	 */
	public static void error(String text){
		server.getBroadcastOperations().sendEvent("system.error", text);
	}
	
	public static void error(SocketIOClient client,String text){
		client.sendEvent("system.error", text);
	}
}
