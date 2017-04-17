package com.groupcontroldroid.units.device.minitouch;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.units.device.base.Banner;
import com.groupcontroldroid.units.device.minicap.ImageConverter;

/**
 * @author zengyan 封装minitouch 里面的方面
 */

public class Minitouch {
	final static Logger logger = LoggerFactory.getLogger(Minitouch.class);
	
	private DeviceEntity deviceEntity;
	private IDevice idevice;
	private Socket socket;
	private OutputStream outputStream = null;
	private  PrintWriter pw=null;
	
	private int width = 350;
	private int height = 600;
	private Banner banner = new Banner();
	private final static String EntityEvent = "input keyevent ";
	
	CollectingOutputReceiver receiver = new CollectingOutputReceiver();

	public Minitouch(DeviceEntity deviceEntity) {

		this.deviceEntity = deviceEntity;
		this.idevice = deviceEntity.getIdevice();
		try {
			socket = new Socket(deviceEntity.getMinitouchEntity().getHost(), deviceEntity.getMinitouchEntity().getPort());
			outputStream = socket.getOutputStream();
			pw=new PrintWriter(outputStream);
		} catch (IOException e) {
			logger.error("",e);
		}
	}
	
	
	/**
	 * 点击实体按键
	 * @param key
	 */
	public void clinkEntity(int key){
		String str=EntityEvent+key;
		
		try {
			idevice.executeShellCommand(str, receiver);
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException | IOException e) {
			logger.error("",e);
		}
		
	}


	
	/**

* @param mpoint
	 *            传入mouseEvent获取到的e 封装点下的方法
	 */

	public void pressed(int intX,int intY) {
		if (outputStream != null) {
			String command = String.format("d 0 %s %s 50\n",
					 intX,intY);
			executeTouch(command);
		}
	}

	/**
	 * 松开鼠标
	 */
	public void released() {
		if (outputStream != null) {
			String command = "u 0\n";
			executeTouch(command);
		}
	}

	/**
	 * @param mpoint
	 *            拖拽
	 */
	public void dragged(int intX,int intY) {
		if (outputStream != null) {
			String command = String.format("m 0 %d %d 50\n",
					intX, intY);
			executeTouch(command);
		}
	}

	/**
	 * @param command
	 *            执行nc语句
	 */
	private synchronized void executeTouch(String command) {

		if (outputStream != null) {
			String endCommand = "c\n";
			String str=command+endCommand;
			pw.write(str);
			pw.flush();
		}
	}
	
	public void close(){
		
		try {
			if (pw!=null) {
				pw.close();
			}
			if (outputStream!=null) {
				outputStream.close();
			}
		} catch (IOException e) {
			logger.error("",e);
		}
		
	}

}
