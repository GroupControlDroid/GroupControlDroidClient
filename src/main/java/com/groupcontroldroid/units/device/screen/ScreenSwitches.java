package com.groupcontroldroid.units.device.screen;

import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.entity.MinicapEntity;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.units.device.apk.ApkServiceSocketStream;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minicap.ImageConverter;
import com.groupcontroldroid.units.device.minicap.Minicap;
import com.groupcontroldroid.units.device.minicap.MinicapSocketStream;

/**
 * 屏幕启动、关闭接口
 */
public class ScreenSwitches {
	final static Logger logger = LoggerFactory.getLogger(ScreenSwitches.class);
	
	/**
	 * 开启屏幕，屏幕宽度通过比例计算得出
	 * @param serialNumber
	 * @param screenHeight
	 */
	public static void startScreen(String serialNumber,int screenHeight){
		DeviceEntity deviceEntity = DeviceContainerHandler.getDevice(serialNumber);
		if(deviceEntity != null){
			double scale = (double)screenHeight / (double)deviceEntity.getScreenHeight();
			logger.info("deviceEntity.getScreenHeight():"+deviceEntity.getScreenHeight());
			int screenWidth = (int) Math.round( deviceEntity.getScreenWidth()*scale );
			startScreen(serialNumber,  screenWidth, screenHeight);
		}
	}
	
	/**
	 * 开启屏幕
	 * @param serialNumber 设备串号
	 * @param screenWidth 屏幕宽度
	 * @param screenHeight 屏幕高度
	 */
	public static void startScreen(String serialNumber, int screenWidth, int screenHeight) {
		logger.info("screen:"+screenWidth+" "+screenHeight);
		
		DeviceEntity deviceEntity = DeviceContainerHandler.getDevice(serialNumber);
		if (deviceEntity != null && !deviceEntity.getMinicapEntity().isStarting()) {
			//记录屏幕虚拟尺寸
			deviceEntity.setVirtualScreenWidth(screenWidth);
			deviceEntity.setVirtualScreenHeight(screenHeight);
			
			MinicapEntity minicapEntity = deviceEntity.getMinicapEntity();
			minicapEntity.setStarting(true);// 防止用户操作过快，产生并发开启同一设备的情况

			deviceEntity.getMinicapEntity().getMinicapManager().startMinicap(screenWidth, screenHeight);// 开启minicap
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				logger.error("sleep中断",e);
			}

			ApkServiceSocketStream apkStream = ApkServiceSocketStream.getApkStream("ApkServiceSocketStream");
			if (apkStream.getState().equals(Thread.State.NEW) && !apkStream.isAlive()) {
				apkStream.start();
			} else if (!apkStream.isAlive() && !apkStream.getState().equals(Thread.State.NEW)) {
				apkStream.run();
			}

			// 打开apkservice

			Minicap minicap = SocketCollection.getMiniCap(serialNumber);
			if (minicap == null) {
				minicap = new Minicap(deviceEntity);
				SocketCollection.addMiniCap(serialNumber, minicap);
			}

			MinicapSocketStream stream = MinicapSocketStream.getMinicapSocketStreamInstance("minicapSocketStreamInstance");
			if (stream.getState().equals(Thread.State.NEW) && !stream.isAlive()) {
				stream.start();
			} else if (!stream.isAlive() && !stream.getState().equals(Thread.State.NEW)) {
				stream.run();
			}

			if (!ClientCollection.isContainsKey(serialNumber)) {
				ClientCollection.addClients(serialNumber, new TreeSet<UUID>());
			}

			ImageConverter converter = ImageConverter.getImageConverter("ImageConverter");
			logger.info("imageConverter is :" + converter.isAlive());

			if (converter.getState().equals(Thread.State.NEW) && !converter.isAlive()) {
				converter.start();
			} else if (!converter.getState().equals(Thread.State.NEW) && !converter.isAlive()) {
				converter.run();
			}

			minicapEntity.setStarting(false);
		}
	}
}
