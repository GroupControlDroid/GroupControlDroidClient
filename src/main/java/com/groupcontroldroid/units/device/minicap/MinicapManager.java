package com.groupcontroldroid.units.device.minicap;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceUnixSocketNamespace;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.NullOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.entity.MinicapEntity;
import com.groupcontroldroid.entity.MinicapEntity.Status;
import com.groupcontroldroid.server.websocket.WebsocketServer;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.base.port.ScreenPortProvider;
import com.groupcontroldroid.util.FileUtil;

/**
 * 安卓手机组件安装
 */
public class MinicapManager {
	final static Logger logger = LoggerFactory.getLogger(MinicapManager.class);

	private DeviceEntity deviceEntity;
	private IDevice idevice;
	private String cpuAbi;
	private int apiLevel;

	private final static String ANDROID_TMP_FOLDER = "/data/local/tmp/";
	private final static String COMMAND_CHMOD = "chmod 777 %s %s";
	private final static String MINICAP_START_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/%s -P %dx%d@%dx%d/0 -S";

	private String bin;

	private int restartRequestCount = 0;

	private final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("MinicapManagerKillThread-%d").setDaemon(true)
			.build();
	// 此线程池主要用于防止并发发送minicap关闭命令导致adb崩溃
	private final static ExecutorService executorService = Executors
			.newFixedThreadPool(4, threadFactory);

	public MinicapManager(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
		this.idevice = deviceEntity.getIdevice();
		this.cpuAbi = deviceEntity.getAbis().get(0);
		this.apiLevel = deviceEntity.getAndroidVersion().getApiLevel();

	}

	/**
	 * 安装minicap
	 * 
	 * @return Boolean 安装成功返回true，失败返回false
	 */
	public void startInstall() {
		String binFolder = "vendor/minicap/bin/" + cpuAbi + "/";
		String sharedPath = "vendor/minicap/shared/android-" + apiLevel + "/"
				+ cpuAbi + "/minicap.so";
		String binPath;
		if (apiLevel >= 16) {
			bin = "minicap";
		} else {
			bin = "minicap-nopie";
		}
		binPath = binFolder + bin;
		// 检测文件是否存在
		if (FileUtil.isFileExist(binPath) && FileUtil.isFileExist(sharedPath)) {
			CollectingOutputReceiver receiver = new CollectingOutputReceiver();
			try {
				idevice.pushFile(binPath, ANDROID_TMP_FOLDER + bin);
				idevice.pushFile(sharedPath, ANDROID_TMP_FOLDER + "minicap.so");
			} catch (SyncException | IOException | AdbCommandRejectedException
					| TimeoutException e) {
				logger.error("无法复制minicap", e);
				return;
			}
			// logger.info(deviceEntity.getSerialNumber() + ":minicap复制成功");

			try {
				// 更改777权限
				String command = String.format(COMMAND_CHMOD,
						ANDROID_TMP_FOLDER + bin, ANDROID_TMP_FOLDER
								+ "minicap.so");
				idevice.executeShellCommand(command, receiver);

			} catch (TimeoutException | AdbCommandRejectedException
					| ShellCommandUnresponsiveException | IOException e) {
				logger.error("安装命令执行出错", e);
				return;
			}
		} else {
			logger.error("找不到minicap二进制文件");
			return;
		}

		int port = ScreenPortProvider.pullPort();// 获取可用端口
		try {
			idevice.createForward(port, "minicap",
					DeviceUnixSocketNamespace.ABSTRACT);
			logger.info("端口绑定到:" + port);
		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			logger.error("端口映射出错", e);
			e.printStackTrace();
		}
		MinicapEntity minicapEntity = new MinicapEntity();
		minicapEntity.setHost("127.0.0.1");
		minicapEntity.setPort(port);
		minicapEntity.setMinicapManager(this);
		deviceEntity.setMinicapEntity(minicapEntity);
	}

	/**
	 * 打开minicap
	 */
	public void startMinicap(final int virtualWidth, final int virtualHeight) {
		MinicapEntity minicapEntity = deviceEntity.getMinicapEntity();
		if (minicapEntity != null) {
			// 新建进程，开始监听屏幕数据
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					if (idevice.isOnline()) {
						// 启动程序
						CollectingOutputReceiver receiver = new CollectingOutputReceiver();
						logger.info(deviceEntity.getSerialNumber()
								+ ":minicap启动");
						try {
							String command = String.format(
									MINICAP_START_COMMAND, bin,
									deviceEntity.getScreenWidth(),
									deviceEntity.getScreenHeight(),
									virtualWidth, virtualHeight);

							// logger.info(command);
							minicapEntity.setStatus(Status.RUNNING);// 设置minicap状态为运行中
							idevice.executeShellCommand(command, receiver, 0);
						} catch (TimeoutException | AdbCommandRejectedException
								| ShellCommandUnresponsiveException
								| IOException e) {
							logger.error(deviceEntity.getSerialNumber()
									+ ":监听屏幕出错", e);
						}
						logger.info(deviceEntity.getSerialNumber()
								+ ":minicap下线");
						receiver.flush();
						logger.info(receiver.getOutput());
						receiver.cancel();
					}
				}
			}, "MinicapAndroidThread-" + deviceEntity.getSerialNumber());
			deviceEntity.getMinicapEntity().setMinicapCMDThread(thread);
			thread.start();
		}
	}

	/**
	 * 重启minicap,通过通知某一个客户端minicap下线通知。使得其重新发起屏幕启动请求
	 */
	public void restartMinicap() {
		// 连续收到两次的重启请求才去执行重启启动。
		if (deviceEntity.getMinicapEntity().getStatus() == Status.RUNNING
				&& restartRequestCount >= 1) {
			logger.warn("[" + deviceEntity.getSerialNumber()
					+ "]minicap退出，正在重启minicap");
			Set<UUID> uuids = ClientCollection.getClients(deviceEntity
					.getSerialNumber());
			if (uuids != null && uuids.size() > 0) {
				SocketIOServer server = WebsocketServer.getServer();
				Iterator<UUID> it = uuids.iterator();
				while (it.hasNext()) {
					SocketIOClient client = server.getClient(uuids.iterator()
							.next());
					if (client != null && client.isChannelOpen()) {
						client.sendEvent("screen.restart_screen_"
								+ deviceEntity.getSerialNumber(),
								deviceEntity.getSerialNumber());
						deviceEntity.getMinicapEntity()
								.setStatus(Status.CLOSED);
						return;
					}
				}
			}
			restartRequestCount = 0;
		}
		restartRequestCount++;
	}

	/**
	 * 关闭minicap
	 * 
	 * @param pid
	 *            进程号
	 */
	public static void killMinicap(final String serialNumber, final int pid) {
		logger.info(serialNumber + ":关闭minicap线程  " + pid);
		DeviceEntity deviceEntity = DeviceContainerHandler
				.getDevice(serialNumber);
		if (deviceEntity != null) {
			MinicapEntity minicapEntity = deviceEntity.getMinicapEntity();
			IDevice idevice = deviceEntity.getIdevice();

			executorService.execute(new Runnable() {
				IShellOutputReceiver receiver = new NullOutputReceiver();

				@Override
				public void run() {
					if (idevice.isOnline()) {
						try {
							idevice.executeShellCommand("exec kill " + pid,
									receiver, 3, TimeUnit.SECONDS);
						} catch (TimeoutException | AdbCommandRejectedException
								| ShellCommandUnresponsiveException
								| IOException e) {
							logger.error("关闭minicap出错", e);
						}
						minicapEntity.setStatus(Status.CLOSED);
					}
				}
			});
		}
	}
}
