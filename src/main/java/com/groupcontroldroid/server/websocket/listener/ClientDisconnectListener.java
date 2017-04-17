package com.groupcontroldroid.server.websocket.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;

public class ClientDisconnectListener implements DisconnectListener{
	private static SocketIOServer server = WebsocketServer.getServer();
	final static Logger logger = LoggerFactory.getLogger(ClientDisconnectListener.class);
	@Override
	public void onDisconnect(SocketIOClient client) {
		// TODO Auto-generated method stub
		logger.info("websocket客户端下线:"+client.getSessionId()+" IP:"+client.getRemoteAddress());
		
		//从屏幕监听客户端列表中移除下线客户端
		for(String str : ClientCollection.getClientAll()){
			String serialNumber = str;
			List<UUID> removeList=new ArrayList<UUID>();
			for(UUID client_uuid :  ClientCollection.getClients(serialNumber)){
				if(client_uuid.equals(client.getSessionId())&&client_uuid!=null){
					logger.info("从屏幕监听客户端列表中移除:"+client.getSessionId()+" IP:"+client.getRemoteAddress());
					removeList.add(client_uuid);
				}
			}
			if (removeList!=null) {
				for (UUID uuid : removeList) {
					ClientCollection.removeUUID(serialNumber,uuid);
				}
			}
			
		}
	}

}
