package com.groupcontroldroid.units.device.base;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.websocket.sender.DeviceWSSender;
import com.groupcontroldroid.units.device.minitouch.MinitouchInstaller;

/**
 * 设备信息处理器
 */
public class DeviceInfoProcessor{
	final static Logger logger=LoggerFactory.getLogger(DeviceInfoProcessor.class);
	
	private DeviceEntity deviceEntity;

	public DeviceInfoProcessor(DeviceEntity deviceEntity){
		this.deviceEntity = deviceEntity;
	}
	
	public void run() {
		IDevice idevice = deviceEntity.getIdevice();
		String receiverStr = null;
		String[] strs = null;
		CollectingOutputReceiver receiver = new CollectingOutputReceiver();
		
		/*获取设备基本信息:手机类型、手机imei*/
		try {
			idevice.executeShellCommand("dumpsys iphonesubinfo", receiver);
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException | IOException e) {
			logger.error("获取设备信息命令执行异常",e);
		}
		receiver.flush();
		receiverStr = receiver.getOutput();
		strs = receiverStr.split("\n");
		if(strs!=null && strs.length==3){
			String[] strs2 = strs[1].split("=");
			if(strs2 != null && strs2.length == 2){
				deviceEntity.setPhoneType(strs2[1].trim());
				logger.info(deviceEntity.getPhoneType());
			}
			
			strs2 = strs[2].split("=");
			if(strs2 != null && strs2.length == 2){
				deviceEntity.setDeviceID(strs2[1].trim());
				logger.info(deviceEntity.getDeviceID());
			}
		}
		
		/*获取手机设备型号*/
		receiver = new CollectingOutputReceiver();
		try {
			idevice.executeShellCommand("getprop ro.product.model", receiver,3,TimeUnit.SECONDS);
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException | IOException e) {
			logger.error("获取设备信息命令执行异常",e);
		}
		receiver.flush();
		receiverStr = receiver.getOutput();
		if(receiverStr != null){
			deviceEntity.setProductModel(receiverStr.trim());
			logger.info(deviceEntity.getProductModel());
		}
	}
	
}
