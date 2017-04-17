package com.groupcontroldroid.units.device.apk;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.IDevice.DeviceUnixSocketNamespace;
import com.groupcontroldroid.config.ApkConfig;
import com.groupcontroldroid.entity.ApkServiceEntity;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.entity.MinitouchEntity;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.base.port.ApkPortProvider;
import com.groupcontroldroid.units.device.base.port.TouchPortProvider;
import com.groupcontroldroid.units.device.minitouch.MinitouchInstaller;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;

public class ApkServiceInstaller {
	final static Logger logger = LoggerFactory.getLogger(ApkServiceInstaller.class);
	private CollectingOutputReceiver receiver = new CollectingOutputReceiver();
	private DeviceEntity deviceEntity;
	// private static final String startActivity = "am start -n
	// com.qkmoc.moc/com.qkmoc.moc.view.MainActivity";
	// private static final String installApk = "adb install
	// vendor/MOCService/mocapp.apk";
	private static final String mocPath = "vendor/MOCService/mocapp.apk";
	// private static final String wxzgPath = "vendor/MOCService/wxzg.apk";
	// private static final String startListen = "nc localhost ";
	private static final String startService = "am startservice --user 0 -a com.qkmoc.moc.ACTION_START -n com.qkmoc.moc/.core.Service";
	private static final String startKeyEvent = "ime set com.qkmoc.moc/.core.AdbIME";
	private IDevice idevice;
	// private static final String installF = "adb install -r mocapp.apk";

	public ApkServiceInstaller(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
		idevice = deviceEntity.getIdevice();
	}

	public boolean startInstall() {
		

		try {
			CollectingOutputReceiver outReceiver = new CollectingOutputReceiver();
			idevice.executeShellCommand(ApkConfig.CHECK_APK_SHELL, outReceiver);
			String appInfo = outReceiver.getOutput();
			int start = appInfo.indexOf("versionName=");
			String appVersion = null;
			if (start != -1) {
				appVersion = appInfo.substring(start + 12, start + 15).trim();
			}
			SystemWSSender.msg("设备["+idevice.getSerialNumber()+"]群控大师版本："+appVersion);
			logger.info("Version:" + appVersion );
			
			if (appVersion == null || !appVersion.equals(ApkConfig.VERSION)) {
				// 安装service 并且启动
				logger.info("Version:" + appVersion + "设备没有app或版本不同，开始安装");
				idevice.installPackage(mocPath, true);
//				SystemWSSender.msg(client,"设备["+deviceEntity.getSerialNumber()+"] 应用安装成功");
				SystemWSSender.msg("设备["+idevice.getSerialNumber()+"] 正在安装群控大师...");
			} else {
				
				System.out.println("版本相同，不安装 version:" + appVersion);
				SystemWSSender.msg("设备["+idevice.getSerialNumber()+"]已安装最新版群控大师");
			}
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException
				| InstallException e2) {
			// TODO Auto-generated catch block
			SystemWSSender.msg("设备["+idevice.getSerialNumber()+"] 安装群控大师失败。。。。卸载重新装");
			try {
				idevice.uninstallPackage("com.qkmoc.moc");
				Thread.sleep(1000);
				idevice.installPackage(mocPath, true);
			} catch (InstallException e) {
				// TODO Auto-generated catch block
				SystemWSSender.msg("设备["+idevice.getSerialNumber()+"] 安装群控大师失败。。。。");
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			e2.printStackTrace();
		}
		
		
		

		try {
			idevice.executeShellCommand(startService, receiver);
			SystemWSSender.msg("设备["+idevice.getSerialNumber()+"] 启动手机服务。。。。");
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e2) {
			logger.error("开启服务失败！！");
			e2.printStackTrace();
			SystemWSSender.msg("设备["+idevice.getSerialNumber()+"] 开启群控大师失败。。。。");
		}
		
			try {
				idevice.executeShellCommand(startKeyEvent, receiver);
			} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
					| IOException e1) {
				// TODO Auto-generated catch block
				logger.error("开启群控大师输入法失败！！");
				e1.printStackTrace();
				SystemWSSender.msg("设备["+idevice.getSerialNumber()+"] 开启群控大师输入法失败。。。。");
			}
		
		


		// 分发端口
		final int port = ApkPortProvider.pullPort();
		try {
			// idevice.createForward(port, "jp.co.cyberagent.stf",
			// DeviceUnixSocketNamespace.ABSTRACT);
			idevice.createForward(port, 1100);
			logger.info("apk端口绑定到:" + port);
		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			logger.error("端口映射出错", e);
		}

		// try {
		// idevice.executeShellCommand(startListen + port, receiver, 0);
		// } catch (TimeoutException | AdbCommandRejectedException |
		// ShellCommandUnresponsiveException | IOException e) {
		// logger.error("分配端口出错");
		// }

		deviceEntity.setApkServiceEntity(new ApkServiceEntity("127.0.0.1", port));

		return true;
	}

}
