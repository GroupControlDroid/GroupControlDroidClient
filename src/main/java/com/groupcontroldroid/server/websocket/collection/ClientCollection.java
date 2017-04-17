package com.groupcontroldroid.server.websocket.collection;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.groupcontroldroid.units.device.minitouch.Minitouch;

public class ClientCollection {
	// 屏幕监听map key:serialNumber value:客户端Set
	private final static Map<String, Set<UUID>> screenClientMap = new ConcurrentHashMap<String, Set<UUID>>();

	public static void addClients(String serialNumber, Set<UUID> client) {
		if (serialNumber != null && client != null) {
			screenClientMap.put(serialNumber, client);
		}
	}

	/**
	 * 删除串号对应下的所有浏览器客户端
	 * 
	 * @param serialNumber
	 */
	public static void removeClients(String serialNumber) {
		if (serialNumber != null) {
			screenClientMap.remove(serialNumber);
		}
	}

	/**
	 * 获取串号说对应的所有浏览器客户端
	 * 
	 * @param serialNumber
	 * @return
	 */
	public static Set<UUID> getClients(String serialNumber) {
		if (serialNumber != null) {
			return screenClientMap.get(serialNumber);
		} else {
			return null;
		}
	}

	public static Set<String> getClientAll() {
		return screenClientMap.keySet();
	}

	public static void removeUUID(String serialNumber, UUID uuid) {
		if (uuid != null && serialNumber != null) {
			Set<UUID> uuids = getClients(serialNumber);
			if (uuids != null) {
				uuids.remove(uuid);
			}
		}
	}

	public static void addUUID(String serialNumber, UUID uuid) {
		if (serialNumber != null && uuid != null) {
			Set<UUID> uuids = getClients(serialNumber);
			if (uuids != null) {
				if (!uuids.contains(uuid)) {
					uuids.add(uuid);
				}
			}
		}
	}

	public static boolean isContainsKey(String serialNumber) {
		return serialNumber != null
				&& screenClientMap.containsKey(serialNumber);
	}

	public static boolean getUUIDcontainsKey(String serialNumber, UUID uuid) {
		if (serialNumber != null && uuid != null) {
			Set<UUID> uuids = getClients(serialNumber);
			if (uuids != null) {
				return uuids.contains(uuid);
			}
		}
		return false;
	}
}
