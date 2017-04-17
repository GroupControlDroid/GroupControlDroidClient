package com.groupcontroldroid.server.websocket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.server.bean.TouchEvent;
import com.groupcontroldroid.units.device.minitouch.EventQueue;
import com.groupcontroldroid.util.JsonUtil;

/**
 * @author zengyan
 * 按下监听器
 */
public class PressedEventListener implements DataListener<String>{
	final static Logger logger = LoggerFactory.getLogger(PressedEventListener.class);
	@Override
	public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
		if (data!=null) {
			TouchEvent event=JsonUtil.jsonTobean(data=data.trim(), TouchEvent.class);
			if (event!=null) {
				EventQueue queue=EventQueue.getinstance();
				queue.addObject(event);
			}else {
				logger.info("json to bean failed!!");
			}
		}
	}

}
