package com.groupcontroldroid.server.websocket.listener;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.server.websocket.sender.DeviceWSSender;

public class GetDeviceListListener implements DataListener<String>{

	@Override
	public void onData(SocketIOClient client, String str, AckRequest ackRequest)
			throws Exception {
		DeviceWSSender.sendDeviceList(client);
	}

}
