package com.groupcontroldroid.server.websocket.listener;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.JsonUtil;

public class ShowServiceListener implements DataListener<String>{
	final static Logger logger = LoggerFactory.getLogger(ShowServiceListener.class);
	private static final String startActivity="am start -n com.qkmoc.moc/com.qkmoc.moc.view.MainActivity";
	private CollectingOutputReceiver receiver = new CollectingOutputReceiver();
	
	@Override
	public void onData(SocketIOClient arg0, String data, AckRequest arg2) throws Exception {
		if (data!=null) {
			List<String> list=JsonUtil.jsonTobean(data=data.trim(), List.class);
			if (list!=null) {
				for (String sernum :list) {
					DeviceEntity deviceEntity=DeviceContainerHandler.getDevice(sernum);
					IDevice idevice=deviceEntity.getIdevice();
					idevice.executeShellCommand(startActivity, receiver);
				}
			}else {
				logger.info("json to bean 失败!!");
			}
		}
		
		
	}
	
	

}
