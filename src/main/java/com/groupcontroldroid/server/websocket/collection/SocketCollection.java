package com.groupcontroldroid.server.websocket.collection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.groupcontroldroid.units.device.apk.ApkService;
import com.groupcontroldroid.units.device.minicap.Minicap;
import com.groupcontroldroid.units.device.minitouch.Minitouch;

/**
 * @author zengyan 用于收集和管理socket连接
 */
public class SocketCollection {
	private final static Map<String, Minitouch> minitouchMap = new ConcurrentHashMap<String, Minitouch>();

	private final static Map<String, Minicap> minicapMap = new ConcurrentHashMap<String, Minicap>();

	private final static Map<String, ApkService> apkServiceMap = new ConcurrentHashMap<String, ApkService>();

	// 下面是minitouch 的操作
	public static void addMiniTouch(String serialNumber, Minitouch minitouch) {
		if (serialNumber != null && minitouch != null) {
			minitouchMap.put(serialNumber, minitouch);
		}
	}

	public static void removeMiniTouch(String serialNumber) {
		if (serialNumber != null) {
			minitouchMap.remove(serialNumber);
		}
	}

	public static Minitouch getMiniTouch(String serialNumber) {
		if (serialNumber != null && minitouchMap.containsKey(serialNumber)) {
			return minitouchMap.get(serialNumber);
		} else {
			return null;
		}
	}

	public static Set<String> getMiniTouchKeySet() {
		return minitouchMap.keySet();
	}

	public static boolean getTouchContainsKey(String serialNumber) {
		if (serialNumber != null) {
			return minitouchMap.containsKey(serialNumber);
		} else {
			return false;
		}
	}

	// 下面是minicap的操作
	public static void addMiniCap(String serialNumber, Minicap minicap) {
		if (serialNumber != null && minicap != null) {
			minicapMap.put(serialNumber, minicap);
		}
	}

	public static void removeMiniCap(String serialNumber) {
		if (serialNumber != null) {
			minicapMap.remove(serialNumber);
		}
	}

	public static Minicap getMiniCap(String serialNumber) {
		if (serialNumber != null && minicapMap.containsKey(serialNumber)) {
			return minicapMap.get(serialNumber);
		} else {
			return null;
		}
	}

	public static Set<String> getMiniCapKeySet() {
		return minicapMap.keySet();
	}

	public static boolean getCapContainsKey(String serialNumber) {
		return minicapMap.containsKey(serialNumber);
	}

	// 下面是apkService 的操作

	public static void addApkService(String serialNumber, ApkService apkservice) {
		if (serialNumber != null) {
			apkServiceMap.put(serialNumber, apkservice);
		}
	}

	public static void removeApkService(String serialNumber) {
		if (serialNumber != null) {
			apkServiceMap.remove(serialNumber);
		}
	}

	public static ApkService getApkService(String serialNumber) {
		if (serialNumber != null && apkServiceMap.containsKey(serialNumber)) {
			return apkServiceMap.get(serialNumber);
		} else {
			return null;
		}
	}

	public static Set<String> getApkServiceKetSet() {
		return apkServiceMap.keySet();
	}

	public static boolean getApkServiceContainsKey(String serialNumber) {
		return serialNumber != null && apkServiceMap.containsKey(serialNumber);
	}
}
