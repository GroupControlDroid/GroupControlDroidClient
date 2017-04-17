package com.groupcontroldroid.server.websocket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.units.device.apk.ApkServiceSocketStream;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;

public class StartServiceListener implements DataListener<String> {
	final static Logger logger = LoggerFactory
			.getLogger(StartServiceListener.class);

	@Override
	public void onData(SocketIOClient arg0, String serialNumber, AckRequest arg2)
			throws Exception {
		DeviceEntity deviceEntity = DeviceContainerHandler
				.getDevice(serialNumber);
		if (deviceEntity != null) {
			ApkServiceSocketStream stream = ApkServiceSocketStream
					.getApkStream("ApkServiceSocketStream");
			if (stream.getState().equals(Thread.State.NEW) && !stream.isAlive()) {
				stream.start();
			} else if (!stream.isAlive()
					&& !stream.getState().equals(Thread.State.NEW)) {
				stream.run();
			}

		}
	}

}
