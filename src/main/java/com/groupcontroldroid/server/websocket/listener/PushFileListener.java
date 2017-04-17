package com.groupcontroldroid.server.websocket.listener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.PushBean;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.FileUtil;
import com.groupcontroldroid.util.JsonUtil;

public class PushFileListener implements DataListener<String> {
	final static Logger logger = LoggerFactory.getLogger(PushFileListener.class);
	final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("PushFileThread-%d")
			.setDaemon(true).build();
	final static ExecutorService executorService = Executors.newFixedThreadPool(4,threadFactory);
	
	@Override
	public void onData(SocketIOClient client, String data, AckRequest ackSender)
			throws Exception {
		PushBean bean = JsonUtil.jsonTobean(data, PushBean.class);
		if(bean != null){
			List<String> serialNumberList = bean.getSerialNumberList();
			String localPath = bean.getLocalPath();
			String remotePath = bean.getRemotePath();
			if(serialNumberList != null && localPath != null && remotePath != null && FileUtil.isFileExist(localPath)){
				for(String serialNumber : serialNumberList){
					DeviceEntity deviceEntity = DeviceContainerHandler.getDevice(serialNumber);
					if(deviceEntity != null){
						IDevice idevice = deviceEntity.getIdevice();
						if(idevice != null){
							executorService.execute(new Runnable() {
								
								@Override
								public void run() {
									try {
										SystemWSSender.msg(client, "正在发送文件到["+deviceEntity.getSerialNumber()+"]:"+remotePath);
										logger.info(remotePath);
										idevice.pushFile(localPath, remotePath);
										SystemWSSender.msg(client, "文件成功发送到 "+deviceEntity.getSerialNumber());
									} catch (SyncException | IOException
											| AdbCommandRejectedException
											| TimeoutException e) {
										logger.info("文件发送出错",e);
										SystemWSSender.warn("["+deviceEntity.getSerialNumber()+"] 文件发送出错"+e.getMessage());
									}
								}
							});
						}
					}
				}
			}
		}
	}

}
