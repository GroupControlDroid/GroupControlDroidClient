package com.groupcontroldroid.server.websocket.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.NullOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.OpenActivityBean;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.JsonUtil;

public class OpenPackageActivityListener implements DataListener<String> {
	final static Logger logger = LoggerFactory.getLogger(OpenPackageActivityListener.class);
	private static final String startService = "am startservice --user 0 -a com.qkmoc.moc.ACTION_START -n com.qkmoc.moc/.core.Service";
	private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("OpenPackageActivityThread-%d")
			.setDaemon(true).build();
	// 线程池，这里主要用于设备应用activity打开
	private ExecutorService executorService = Executors.newScheduledThreadPool(15, threadFactory);

	private final static String COMMAND = "am start -n %s/%s";

	@Override
	public void onData(SocketIOClient client, String str, AckRequest ackRequest) throws Exception {
		logger.info(str);
		OpenActivityBean bean = JsonUtil.jsonTobean(str, OpenActivityBean.class);
//		OpenActivityBean noAPPDevicebean = new OpenActivityBean();
		if (bean != null) {
			String packageName = bean.getPackageName();
			String activityName = bean.getActivityName();
			List<String> serialNumberList = bean.getSerialNumList();
//			noAPPDevicebean.setPackageName(packageName);
//			noAPPDevicebean.setActivityName(activityName);
			//List<String> noAppDeviceList = new ArrayList<>();
			if (packageName != null && activityName != null && serialNumberList != null) {
				for (String serialNumber : serialNumberList) {
					DeviceEntity deviceEntity = DeviceContainerHandler.getDevice(serialNumber);
					if (deviceEntity != null) {
						IDevice idevice = deviceEntity.getIdevice();
						if (idevice != null) {
							executorService.execute(new Runnable() {
								@Override
								public void run() {
									CollectingOutputReceiver receiver = new CollectingOutputReceiver();
									try {
										idevice.executeShellCommand(
												String.format(COMMAND, packageName.trim(), activityName.trim()),
												receiver);
										logger.info(String.format(COMMAND, packageName.trim(), activityName.trim()));
										String appInfo = receiver.getOutput().trim();
										if (appInfo.endsWith("does not exist.")) {
											SystemWSSender.warn(client,"该应用在设备["+serialNumber+"]上尚未安装，请先安装");
										}
									} catch (TimeoutException | AdbCommandRejectedException
											| ShellCommandUnresponsiveException | IOException e) {
										logger.error("打开应用activity出错", e);
									}
								}
							});
						}
					}
				}
//				noAPPDevicebean.setSerialNumList(noAppDeviceList);
//				String noAppDeviceStr=JsonUtil.beanToJson(noAPPDevicebean);
//				System.out.println(noAppDeviceStr);
			}
		}
	}

}
