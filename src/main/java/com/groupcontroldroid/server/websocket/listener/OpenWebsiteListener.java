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
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.NullOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.OpenWebsiteBean;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.JsonUtil;

public class OpenWebsiteListener implements DataListener<String> {
	final static Logger logger = LoggerFactory
			.getLogger(OpenWebsiteListener.class);
	private ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("OpenWebsiteThread-%d").setDaemon(true).build();
	// 线程池，这里主要用于设备网址打开
	private ExecutorService executorService = Executors.newScheduledThreadPool(
			15, threadFactory);
	private final static String COMMAND = "am start -a android.intent.action.VIEW -d %s";

	@Override
	public void onData(SocketIOClient client, String str, AckRequest ackRequest)
			throws Exception {
		logger.info(str);
		OpenWebsiteBean bean = JsonUtil.jsonTobean(str, OpenWebsiteBean.class);

		// 根据设备列表统一打开网址
		if (bean != null) {
			List<String> serialNumberList = bean.getSerialNumList();
			if (serialNumberList != null && serialNumberList.size() > 0) {
				String url = bean.getUrl().trim();
				for (String serialNumber : serialNumberList) {
					DeviceEntity deviceEntity = DeviceContainerHandler
							.getDevice(serialNumber);
					if (deviceEntity != null) {
						IDevice idevice = deviceEntity.getIdevice();
						executorService.execute(new Runnable() {

							@Override
							public void run() {
								NullOutputReceiver receiver = new NullOutputReceiver();
								try {
									idevice.executeShellCommand(
											String.format(COMMAND, url),
											receiver);
								} catch (TimeoutException
										| AdbCommandRejectedException
										| ShellCommandUnresponsiveException
										| IOException e) {
									logger.error(serialNumber + ":设备打开网址:"
											+ url + "出错");
								}
							}
						});
					}
				}
			}
		}
	}

}
