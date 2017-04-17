package com.groupcontroldroid.server.websocket.listener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.InstallBean;
import com.groupcontroldroid.server.jetty.servlet.UploadServlet;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minitouch.EventThread;
import com.groupcontroldroid.util.FileUtil;
import com.groupcontroldroid.util.JsonUtil;

public class InstallListener implements DataListener<String> {
	final static Logger logger = LoggerFactory.getLogger(InstallListener.class);
	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("InstallApksThread-%d")
			.setDaemon(true).build();
	private final static ExecutorService executorService = Executors.newFixedThreadPool(4,threadFactory);
	
	public void onData(SocketIOClient client, String jsonStr, AckRequest ack)
			throws Exception {
		InstallBean bean = JsonUtil.jsonTobean(jsonStr, InstallBean.class);
		logger.info(jsonStr);
		if(bean != null){
			List<String> serialNumberList = bean.getSerialNumberList();
			String apkPath = bean.getApkPath();
			if(serialNumberList != null && apkPath != null && FileUtil.isFileExist(apkPath)){
				for(String serialNumber : serialNumberList){
					DeviceEntity deviceEntity = DeviceContainerHandler.getDevice(serialNumber);
					if(deviceEntity != null){
						IDevice idevice = deviceEntity.getIdevice();
						if(idevice != null && idevice.isOnline()){
							executorService.execute(new Runnable() {
								@Override
								public void run() {
									SystemWSSender.msg(client,"设备["+deviceEntity.getSerialNumber()+"] 开始安装应用");
									try {
										idevice.installPackage(apkPath, true);
										SystemWSSender.msg(client,"设备["+deviceEntity.getSerialNumber()+"] 应用安装成功");
									} catch (InstallException e) {
										logger.error(serialNumber+":安装apk["+apkPath+"] 出错",e);
										SystemWSSender.error(client,"设备["+serialNumber+"]apk安装出错,原因:"+e.getMessage());
									}
								
								}
							});
						}
					}
				}
			}
		}
	}

}
