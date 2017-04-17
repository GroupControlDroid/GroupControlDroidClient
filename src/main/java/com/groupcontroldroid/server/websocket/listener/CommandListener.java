package com.groupcontroldroid.server.websocket.listener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.CommandBean;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.units.device.apk.ApkService;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.JsonUtil;

/**
 * 执行安卓设备命令
 */
public class CommandListener implements DataListener<String> {

	final static Logger logger = LoggerFactory.getLogger(CommandListener.class);
	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("TextSenderThread-%d").setDaemon(true).build();
	private final static ExecutorService executorService = Executors
			.newFixedThreadPool(4, threadFactory);

	@Override
	public void onData(SocketIOClient client, String data, AckRequest ackSender)
			throws Exception {
		if (data != null) {
			CommandBean commandBean = JsonUtil.jsonTobean(data,
					CommandBean.class);
			if (commandBean != null && commandBean.getSerList() != null
					&& commandBean.getCommand() != null) {
				for (String sernum : commandBean.getSerList()) {
					executorService.execute(new Runnable() {

						@Override
						public void run() {
							DeviceEntity deviceEntity = DeviceContainerHandler
									.getDevice(sernum);
							if (deviceEntity != null) {
								IDevice idevice = deviceEntity.getIdevice();
								if (idevice.isOnline()) {
									CollectingOutputReceiver receiver = new CollectingOutputReceiver();
									try {
										idevice.executeShellCommand(
												commandBean.getCommand(),
												receiver);
									} catch (TimeoutException
											| AdbCommandRejectedException
											| ShellCommandUnresponsiveException
											| IOException e) {
										logger.error("执行命令发送异常", e);
									}
									receiver.flush();
									logger.info(receiver.getOutput());
								}
							}
						}
					});
				}
			}
		}
	}
}
