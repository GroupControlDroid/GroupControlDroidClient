package com.groupcontroldroid.units.device.minitouch;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceUnixSocketNamespace;
import com.android.ddmlib.NullOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.entity.MinitouchEntity;
import com.groupcontroldroid.units.device.base.port.TouchPortProvider;
import com.groupcontroldroid.util.FileUtil;

/**
 * @author zengyan
 * 安卓安装minitouch
 */
public class MinitouchInstaller {
//	final static Logger logger=(Logger) LoggerFactory.getLogger(MinitouchInstaller.class);
	final static Logger logger=LoggerFactory.getLogger(MinitouchInstaller.class);
	private DeviceEntity deviceEntity;
	private IDevice idevice;
	private String cpuAbi;
	private int apiLevel;
	
	private final static String ANDROID_TMP_FOLDER = "/data/local/tmp/";
	private final static String COMMAND_CHMOD = "chmod 777 %s";
//	private final static String MINICAP_START_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/%s -P %dx%d@%dx%d/0";
	private final static String MINITOUCH_START_COMMAND="/data/local/tmp/";
	/**
	 * @param deviceEntity
	 */
	public MinitouchInstaller(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
		this.idevice = deviceEntity.getIdevice();
		this.cpuAbi = deviceEntity.getAbis().get(0);
		this.apiLevel = deviceEntity.getAndroidVersion().getApiLevel();
	}
	
	public boolean startInstall(){
		String binFolder="vendor/minitouch/"+ cpuAbi + "/";
		final String bin;
		String binpath;
		if (apiLevel >= 16) {
			bin = "minitouch";
		} else {
			bin = "minitouch-nopie";
		}
		binpath=binFolder+bin;
		
//		检测文件是否存在
		if (FileUtil.isFileExist(binpath)) {
			NullOutputReceiver nullReceiver=new NullOutputReceiver();
			CollectingOutputReceiver receiver = new CollectingOutputReceiver();
			try {
				idevice.pushFile(binpath, ANDROID_TMP_FOLDER + bin);
			} catch (SyncException | IOException | AdbCommandRejectedException | TimeoutException e) {
				logger.error("minitouch push 失败");
				return false;
			}
//			logger.info(deviceEntity.getSerialNumber() + ":minitouch复制成功");
			
//			更改777权限
			try {
				// 更改777权限
				String command = String.format(COMMAND_CHMOD, ANDROID_TMP_FOLDER+bin);
				idevice.executeShellCommand(command, receiver);
			} catch (TimeoutException | AdbCommandRejectedException
					| ShellCommandUnresponsiveException | IOException e) {
				logger.error("安装命令执行出错", e);
			}
			
//			新建进程 ，开始进行监听电脑鼠标点击
			Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					CollectingOutputReceiver receiver=new CollectingOutputReceiver();
					try {
						logger.info(deviceEntity.getSerialNumber()
								+ ":minitouch启动");
						idevice.executeShellCommand(MINITOUCH_START_COMMAND+bin, receiver,0);
					} catch (TimeoutException | AdbCommandRejectedException
							| ShellCommandUnresponsiveException | IOException e) {
						((org.slf4j.Logger) logger).error(deviceEntity.getSerialNumber() + "监听鼠标点击出错",e);
					}
					logger.info(deviceEntity.getSerialNumber()
							+ ":minitouch断开");
					receiver.flush();
//					logger.info(receiver.getOutput());
				}
			},"MinitouchInstaller Thread");
			
//			deviceEntity.getMinitouchEntity().setTouchCmdThread(thread);
			
			
			thread.start();
			
			int port =TouchPortProvider.pullPort();
			try {
				idevice.createForward(port, "minitouch",
						DeviceUnixSocketNamespace.ABSTRACT);
				logger.info("端口绑定到:"+port);
			} catch (TimeoutException | AdbCommandRejectedException
					| IOException e) {
				((org.slf4j.Logger) logger).error("端口映射出错", e);
				e.printStackTrace();
			}
			deviceEntity.setMinitouchEntity(new MinitouchEntity("127.0.0.1", port,thread));
			
			return true;
			
		}else {
			logger.error("找不到minitouch二进制文件");
			return false;
		}
		
		
		
	}
	

}































