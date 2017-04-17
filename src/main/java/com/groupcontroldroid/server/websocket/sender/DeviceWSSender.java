package com.groupcontroldroid.server.websocket.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.DeviceBean;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.JsonUtil;

/**
 * websocket设备信息发送
 */
public class DeviceWSSender {
	private static SocketIOServer server = WebsocketServer.getServer();

	/**
	 * 广播设备列表到客户端
	 */
	public static void BroadcastDeviceList() {

	}

	/**
	 * 发送设备列表到客户端
	 * 
	 * @param client
	 */
	public static void sendDeviceList(SocketIOClient client) {
		client.sendEvent("device.device_list", deviceListToJson());
	}

	/**
	 * @param jsonStr
	 *            手机状态 json数据
	 */
	public static void broadcastMinicapCrash(String serialNumber) {
		server.getBroadcastOperations().sendEvent("device.minicap_crash", serialNumber);

	}

	/**
	 * @param jsonStr
	 *            手机状态 json数据
	 */
	public static void broadcastDeviceStatus(String serialNumber, String jsonStr) {
		server.getBroadcastOperations().sendEvent("device.status_" + serialNumber, jsonStr);

	}

	/**
	 * 广播新设备(已上线)
	 * 
	 * @param deviceEntity
	 */
	public static void broadcastNewDevice(DeviceEntity deviceEntity) {
		DeviceBean deviceBean = new DeviceBean(deviceEntity.getSerialNumber(), deviceEntity.getAbis().get(0),
				deviceEntity.getAndroidVersion().getApiLevel(), deviceEntity.getScreenWidth(),
				deviceEntity.getScreenHeight(), deviceEntity.getVirtualScreenWidth(),
				deviceEntity.getVirtualScreenHeight(), deviceEntity.getProductModel(), deviceEntity.getDeviceID(),
				deviceEntity.getPhoneType());

		server.getBroadcastOperations().sendEvent("device.new_device", JsonUtil.beanToJson(deviceBean));
		System.out.println();
	}

	/**
	 * 广播掉线设备
	 * 
	 * @param serialNumber
	 */
	public static void broadcastOfflineDevice(String serialNumber) {
		server.getBroadcastOperations().sendEvent("device.offline_device", serialNumber);
	}

	/**
	 * 广播上线中的设备
	 * 
	 * @param serialNumber
	 */
	public static void broadcastInstallingDevice(String serialNumber) {
		server.getBroadcastOperations().sendEvent("device.installing_device", serialNumber);
	}

	/**
	 * 把设备列表转换为json字符串
	 * 
	 * @return
	 */
	private static String deviceListToJson() {
		String json = null;
		List<DeviceBean> list = new ArrayList<DeviceBean>();
		for (String str : DeviceContainerHandler.getKeySet()) {
			DeviceEntity deviceEntity = DeviceContainerHandler.getDevice(str);
			DeviceBean deviceBean = new DeviceBean(deviceEntity.getSerialNumber(), deviceEntity.getAbis().get(0),
					deviceEntity.getAndroidVersion().getApiLevel(), deviceEntity.getScreenWidth(),
					deviceEntity.getScreenHeight(), deviceEntity.getVirtualScreenWidth(),
					deviceEntity.getVirtualScreenHeight(), deviceEntity.getProductModel(), deviceEntity.getDeviceID(),
					deviceEntity.getPhoneType());
			list.add(deviceBean);
		}
		json = JsonUtil.beanToJson(list);
		return json;
	}
}
