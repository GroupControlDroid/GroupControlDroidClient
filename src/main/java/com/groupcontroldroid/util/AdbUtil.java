package com.groupcontroldroid.util;

import com.android.ddmlib.AndroidDebugBridge;
import com.groupcontroldroid.config.DirConfig;

/**
 * @author pkdog AdbUtil
 */
public class AdbUtil {
	private static AndroidDebugBridge bridge = null;

	/**
	 * 获取ADB连接实例
	 * 
	 * @return AndroidDebugBridge
	 */
	public static AndroidDebugBridge getADBInstance() {
		if (bridge == null) {
			AndroidDebugBridge.init(false);
			String os = System.getProperty("os.name").toLowerCase();
			if (os.startsWith("win")) {
				// 如果是win系统
				bridge = AndroidDebugBridge.createBridge(DirConfig.ADB_DIR + "adb.exe", true);
			} else if(os.startsWith("mac")) {
				// 如果是osx系统
				bridge = AndroidDebugBridge.createBridge(DirConfig.MAC_ADB_DIR + "adb", true);
			} else {
				bridge = AndroidDebugBridge.createBridge("adb",true);
			}
		}
		return bridge;
	}
}
