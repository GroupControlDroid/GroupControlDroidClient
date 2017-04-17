package com.groupcontroldroid.server.websocket.listener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.IDevice;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.config.ApkConfig;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.InfoToAPPBean;
import com.groupcontroldroid.server.bean.TextBean;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.units.device.apk.ApkService;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.JsonUtil;

public class TextListener implements DataListener<String>{
	final static Logger logger = LoggerFactory.getLogger(TextListener.class);
	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
	.setNameFormat("TextSenderThread-%d")
	.setDaemon(true).build();
	private final static ExecutorService executorService = Executors.newCachedThreadPool(threadFactory);
	
	@Override
	public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
		if (data != null) {
			logger.info("textlistenr:" + data);
			TextBean textBean = JsonUtil.jsonTobean(data.trim(),
					TextBean.class);
			if (textBean.getSerList() != null) {
				 List<String> lists= textBean.getSerList();
				 String answer =textBean.getText();
				if (answer != null) {
					executorService.execute(new Runnable() {
						@Override
						public void run() {
							for (String sernum : lists) {
								
								DeviceEntity deviceEntity = DeviceContainerHandler
										.getDevice(sernum);
								ApkService apkservice=SocketCollection.getApkService(sernum);
								OutputStream outputStream=null;
								
								if (deviceEntity != null) {
									Socket socket=null;
									IDevice idevice = deviceEntity.getIdevice();
									if (idevice != null) {
										try {
											 socket = new Socket(deviceEntity
													.getApkServiceEntity()
													.getHost(), deviceEntity
													.getApkServiceEntity()
													.getPort());
											outputStream = socket
													.getOutputStream();
												InfoToAPPBean infoToAPPBean = new InfoToAPPBean();
												infoToAPPBean.setStyle(ApkConfig.INTPUT_TEXT);
												infoToAPPBean.setKey(answer);
												String str = JsonUtil
														.beanToJson(infoToAPPBean);
												byte[] srtbyte = str
														.getBytes("UTF-8");
												
												outputStream.write(srtbyte);
										} catch (UnknownHostException e) {
											logger.error("位置apk服务端",e);
										} catch (IOException e) {
											logger.error("IO 异常",e);
										} finally {
											try {
												outputStream.flush();
												outputStream.close();
												socket.close();
											} catch (IOException e) {
												logger.error("socket 关闭出错",e);
											}
										}

									}
								}
							}
						}
					});
				}

			}
		}
		
		
	}

}
