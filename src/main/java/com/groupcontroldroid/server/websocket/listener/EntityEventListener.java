package com.groupcontroldroid.server.websocket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.TouchEntityEvent;
import com.groupcontroldroid.server.bean.TouchEvent;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minitouch.EventQueue;
import com.groupcontroldroid.units.device.minitouch.Minitouch;
import com.groupcontroldroid.util.JsonUtil;

public class EntityEventListener implements DataListener<String>{
	final static Logger logger = LoggerFactory
			.getLogger(EntityEventListener.class);
	@Override
	public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
		data=data.trim();
		if (data!=null) {
			TouchEntityEvent event=JsonUtil.jsonTobean(data, TouchEntityEvent.class);
			if (event!=null) {
				EventQueue queue=EventQueue.getinstance();
				queue.addObject(event);
			}else {
				logger.info("json to bean failed!!");
			}
		}
	}

}
