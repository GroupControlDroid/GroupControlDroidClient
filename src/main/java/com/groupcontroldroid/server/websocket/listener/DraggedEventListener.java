package com.groupcontroldroid.server.websocket.listener;

import java.awt.Point;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.TouchDraggedEvent;
import com.groupcontroldroid.server.bean.TouchEvent;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minitouch.EventQueue;
import com.groupcontroldroid.units.device.minitouch.EventThread;
import com.groupcontroldroid.units.device.minitouch.Minitouch;
import com.groupcontroldroid.util.JsonUtil;

/**
 * @author zengyan
 * 拖拽监听器，监听到拖拽就将其放入队列
 */
public class DraggedEventListener implements DataListener<String>{
	final static Logger logger = LoggerFactory.getLogger(DraggedEventListener.class);
	@Override
	public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
		if (data!=null) {
			TouchDraggedEvent event=JsonUtil.jsonTobean(data.trim(), TouchDraggedEvent.class);
			if (event!=null) {
				EventQueue queue=EventQueue.getinstance();
				queue.addObject(event);
			}else {
				logger.info("json to bean failed!!");
			}
		}
	}

}
