package com.groupcontroldroid.units.device.apk;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.groupcontroldroid.config.WebsocketConfig;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.entity.MinicapEntity.Status;
import com.groupcontroldroid.server.bean.InfoToAPPBean;
import com.groupcontroldroid.server.bean.MocAPPBean;
import com.groupcontroldroid.server.websocket.sender.DeviceWSSender;
import com.groupcontroldroid.units.device.minitouch.Minitouch;
import com.groupcontroldroid.util.JsonUtil;

public class ApkService {
	final static Logger logger = LoggerFactory.getLogger(ApkService.class);
	private DeviceEntity deviceEntity;
	private Socket socket;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;

	public ApkService(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
		// this.idevice = deviceEntity.getIdevice();
		try {
			socket = new Socket(deviceEntity.getApkServiceEntity().getHost(),
					deviceEntity.getApkServiceEntity().getPort());
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			// pw = new PrintWriter(outputStream);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public void getDeviceState() throws IOException {
		// 用于随时中断自己
		Thread currentThread = Thread.currentThread();
		if (deviceEntity.getIsRunning() && !currentThread.isInterrupted()) {
			byte[] buffer = null;
			int len = 0;

			if (len == 0 && !currentThread.isInterrupted()) {

				try {
					if (inputStream!=null) {
						len = inputStream.available();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 一次能读到的长度
			}

			if (len == 0) {
				
			} else {
				buffer = new byte[len];
				try {
					inputStream.read(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 读取
					// 发送设备id
			
				String answer = new String(buffer);
				//logger.info("answer is "+answer);
				if (answer.length() > 10) {
					MocAPPBean bean = JsonUtil.jsonTobean(answer.trim(), MocAPPBean.class);
					if (bean != null) {
						if (bean.getId() == 0) {
							synchronized (this) {
								int id=WebsocketConfig.getid();
								deviceEntity.setId(id);
								bean.setId(id);
							}
						}
						if (!bean.isMinicapState() && deviceEntity.getMinicapEntity().getStatus() == Status.RUNNING) {
							deviceEntity.getMinicapEntity().getMinicapManager().restartMinicap();
						}
						// else System.out.println("minicap is ok");

						String jsonStr = JsonUtil.beanToJson(bean);
						deviceEntity.getApkServiceEntity().getPort();
						
						try {
							InfoToAPPBean infoToAPPBean = new InfoToAPPBean();
							infoToAPPBean.setId(bean.getId());
							String str = JsonUtil.beanToJson(infoToAPPBean);
							byte[] srtbyte = str.getBytes();
							outputStream.write(srtbyte);
						} catch (IOException e) { // The socket went away

						}

						DeviceWSSender.broadcastDeviceStatus(deviceEntity.getSerialNumber(),jsonStr);
					}
				}

			}
		}
	}

	public void close() {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
