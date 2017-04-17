package com.groupcontroldroid.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.config.DirConfig;
import com.groupcontroldroid.config.PortConfig;
import com.groupcontroldroid.security.task.ValidateTokenTask;
import com.groupcontroldroid.server.jetty.JettyServer;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.system.ShutdownWork;
import com.groupcontroldroid.ui.Entrance;
import com.groupcontroldroid.units.device.base.DeviceConnectListenerTask;
import com.groupcontroldroid.units.device.base.port.ApkPortProvider;
import com.groupcontroldroid.units.device.base.port.ScreenPortProvider;
import com.groupcontroldroid.units.device.base.port.TouchPortProvider;
import com.groupcontroldroid.units.device.minicap.ScreenImageDistribute;
import com.groupcontroldroid.units.device.minitouch.EventThread;
import com.groupcontroldroid.util.AdbUtil;
import com.groupcontroldroid.util.SysUtil;

/**
 * 程序主入口
 */
public class Main {
	final static Logger logger = LoggerFactory.getLogger(Main.class);
	final static Timer timer = new Timer("MocMainTimer");
	
	/**
	 * 初始化
	 */
	public static void init() {
		try {
			Process process;
			if (SysUtil.OS_NAME.startsWith("win")) {
				String[] cmd = {new File(DirConfig.ADB_DIR).getAbsolutePath()+"\\adb.exe","kill-server"};
				// 如果是win系统
				process = Runtime.getRuntime().exec(cmd, null);
			} else if (SysUtil.OS_NAME.startsWith("mac")) {
				// 如果是osx系统
				String[] cmd = {new File(DirConfig.ADB_DIR).getAbsolutePath()+"/adb","kill-server"};
				// 如果是win系统
				process = Runtime.getRuntime().exec(cmd, null);
			} else {
				process = Runtime.getRuntime().exec("adb kill-server", null);
			}
			InputStream in = process.getInputStream();
			while (in.read() != -1) {
				System.out.println(in.read());
			}
			in.close();
			process.waitFor();
		} catch (IOException e) {
			logger.error("IO出错", e);
		} catch (InterruptedException e) {
			logger.error("意外中断", e);
		}
		ScreenPortProvider.init(PortConfig.screenPortStart,
				PortConfig.screenPortEnd);
		TouchPortProvider.init(PortConfig.touchPortStart,
				PortConfig.touchPortEnd);
		ApkPortProvider.init(PortConfig.apkPortStart, PortConfig.apkPortEnd);
	}

	public static void main(String[] args) {
		logger.debug("开始");
		
		init();// 初始化
		AdbUtil.getADBInstance();
		new JettyServer("JettyServer").start();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Entrance().setVisible(true);
			}
		});
		
		try {
			while (AuthConfig.isContinueLockMain()) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			logger.error("auth error",e);
			System.exit(1);
		}
		new WebsocketServer("WebsocketServer").start();
		
		timer.schedule(new DeviceConnectListenerTask(), 2, 1900);//设备连接监听
		timer.schedule(new ValidateTokenTask(), 100, 1000*60*5);//在线token验证
		
		new EventThread("Event Thread").start();
		new ScreenImageDistribute("ScreenImageDistributeThread").start();
		//系统关闭事件监听
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownWork(),"ShutdownWork"));
		logger.debug("结束");
	}
}
