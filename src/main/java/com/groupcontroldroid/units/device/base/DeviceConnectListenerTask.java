package com.groupcontroldroid.units.device.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.config.GlobalConfig;
import com.groupcontroldroid.config.WebsocketConfig;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.server.websocket.sender.DeviceWSSender;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.apk.ApkService;
import com.groupcontroldroid.units.device.apk.ApkServiceInstaller;
import com.groupcontroldroid.units.device.minicap.Minicap;
import com.groupcontroldroid.units.device.minicap.MinicapManager;
import com.groupcontroldroid.units.device.minitouch.Minitouch;
import com.groupcontroldroid.units.device.minitouch.MinitouchInstaller;
import com.groupcontroldroid.units.device.screen.ScreenSwitches;
import com.groupcontroldroid.util.AdbUtil;
import com.groupcontroldroid.util.JsonUtil;

/**
 * 设备连接监听
 */
public class DeviceConnectListenerTask extends TimerTask {
	final static Logger logger = LoggerFactory
			.getLogger(DeviceConnectListenerTask.class);
	private static AndroidDebugBridge bridge = AdbUtil.getADBInstance();

	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("DeviceConnectListenerChildThread-%d")
			.setDaemon(true).build();
	// 线程池，这里主要用于设备信息获取
	private final static ExecutorService executorService = Executors
			.newFixedThreadPool(4, threadFactory);

	/**
	 * 开始监听设备连接，如果有新手机连接进入，那么就添加到设备列表
	 */

	@Override
	public void run() {

		if (!bridge.isConnected() || bridge.hasInitialDeviceList() == false) {
			// 初始化还没有完成
			return;
		} else {
			IDevice[] devices = bridge.getDevices();
			List<String> serials = new ArrayList<>();// 记录serial，用于提交到服务器端

			for (String serialNumber : DeviceContainerHandler.getKeySet()) {
				DeviceContainerHandler.getDevice(serialNumber).setHasCheck(
						false);// 重置所有设备的检查标记
			}

			// 检测是否有新设备上线
			for (IDevice device : devices) {
				if (!DeviceContainerHandler.hasDevice(device.getSerialNumber())) {
					String serialNumber = device.getSerialNumber();
					DeviceWSSender.broadcastInstallingDevice(serialNumber);

					if (device.isOnline()
							&& DeviceContainerHandler.getDeviceCount() < AuthConfig
									.getDeviceLimit()) {
						// 广播上线消息
						SystemWSSender.msg("设备[" + serialNumber + "]上线中...");
						DeviceEntity deviceEntity = new DeviceEntity(device);
						DeviceContainerHandler.addDevice(deviceEntity);

						serials.add(serialNumber);

						executorService.execute(new Runnable() {
							@Override
							public void run() {

								MinicapManager capManager = new MinicapManager(
										deviceEntity);
								MinitouchInstaller touchInstaller = new MinitouchInstaller(
										deviceEntity);
								ApkServiceInstaller apkInstaller = new ApkServiceInstaller(
										deviceEntity);

								touchInstaller.startInstall();// 安装 打开cmd线程
								capManager.startInstall();// 安装 不建立cmd 线程
								SystemWSSender.msg("设备[" + serialNumber
										+ "] 正在启动服务...");
								apkInstaller.startInstall();

								// 新建touch socket 放入map
								Minitouch toucher = new Minitouch(deviceEntity);
								SocketCollection.addMiniTouch(serialNumber,
										toucher);

								// 新建apkservice socket 加入map
								ApkService apkservice = new ApkService(
										deviceEntity);
								SocketCollection.addApkService(serialNumber,
										apkservice);

								// 获取设备基本信息
								new DeviceInfoProcessor(deviceEntity).run();

								//开启屏幕
								ScreenSwitches.startScreen(serialNumber, GlobalConfig.getScreenHeight());
								
								// SocketIO广播设备上线
								DeviceWSSender.broadcastNewDevice(deviceEntity);
								
								logger.info("上线:" + device.toString() + " "
										+ device.getAbis().toString() + " "
										+ device.getVersion().getApiLevel());
							}
						});
					}
				} else {
					if (device.isOffline()) {
						DeviceContainerHandler.getDevice(
								device.getSerialNumber()).setHasCheck(false);
					} else if (device.isOnline()) {
						DeviceContainerHandler.getDevice(
								device.getSerialNumber()).setHasCheck(true);
					}
				}
			}
			Set<String> sernums = DeviceContainerHandler.getKeySet();
			List<String> deleteList = null;
			if (sernums != null) {
				for (String serialNumber : sernums) {
					if (DeviceContainerHandler.getDevice(serialNumber)
							.getHasCheck() == false) {
						SystemWSSender.msg("设备[" + serialNumber + "]已下线!");

						// 说明此机掉线了
						deleteList = new ArrayList<String>();
						deleteList.add(serialNumber);

						DeviceWSSender.broadcastOfflineDevice(serialNumber);// 广播设备下线信息
						DeviceEntity deviceEntity = DeviceContainerHandler
								.getDevice(serialNumber);
						WebsocketConfig.addId(deviceEntity.getId());
						// 中断线程
						// deviceEntity.getMinicapEntity().getMinicapCMDThread().interrupt();
						deviceEntity.getMinitouchEntity().getTouchCmdThread()
								.interrupt();

						// 关闭Minicap 全部服务
						Minicap cap = SocketCollection.getMiniCap(serialNumber);
						if (cap != null) {
							cap.closeAll();
						}
						SocketCollection.removeMiniCap(serialNumber);

						// 删除websocket客户端收取设备信息列表
						if (ClientCollection.isContainsKey(serialNumber)) {
							ClientCollection.getClients(serialNumber).clear();
						}

						// minitouch socket连接下线
						if (SocketCollection.getTouchContainsKey(serialNumber)) {
							Minitouch minitouch = SocketCollection
									.getMiniTouch(serialNumber);
							if (minitouch != null) {
								minitouch.close();
							}
							SocketCollection.removeMiniTouch(serialNumber);
						}

						// apkservice 下线
						if (SocketCollection
								.getApkServiceContainsKey(serialNumber)) {
							ApkService apkservice = SocketCollection
									.getApkService(serialNumber);
							if (apkservice != null) {
								apkservice.close();
							}
							SocketCollection.removeApkService(serialNumber);
						}

						deviceEntity.setIsRunning(false);
						logger.info("下线:" + serialNumber);
					}
				}
				if (deleteList != null) {
					for (String sernum : deleteList) {
						DeviceContainerHandler.deleteDevice(sernum);// 从设备列表中删除
					}
				}
			}

			// 发送新上线的设备串号到服务器
			if (!serials.isEmpty()) {
				sendSerialsToServer(serials);
			}
		}
	}

	/**
	 * 发送新上线设备到服务器
	 * 
	 * @param serials
	 */
	private void sendSerialsToServer(List<String> serials) {
		String serialsJsonStr = JsonUtil.beanToJson(serials);
		if (serialsJsonStr != null) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("serials", serialsJsonStr);
			data.put("user_id", new Long(AuthConfig.getUser_id()).toString());
			data.put("token", AuthConfig.getToken());
			String resultStr = HttpRequest
					.post("http://" + AuthConfig.SITE_URL
							+ "/device/add_device.cgi").form(data).body();
			logger.info(resultStr);
		}
	}

}
