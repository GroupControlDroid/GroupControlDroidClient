package com.groupcontroldroid.units.device.minicap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.IDevice;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.websocket.collection.ClientCollection;
import com.groupcontroldroid.server.websocket.collection.SocketCollection;
import com.groupcontroldroid.server.websocket.sender.ScreenWSSender;
import com.groupcontroldroid.server.websocket.sender.SystemWSSender;
import com.groupcontroldroid.units.device.base.Banner;
import com.groupcontroldroid.units.device.minitouch.Minitouch;

public class Minicap {
	final static Logger logger = LoggerFactory.getLogger(Minicap.class);
	
//	下面为生产者的变量
	private DeviceEntity deviceEntity;
	private Socket socket;
	private InputStream inputStream = null;
	private DataInputStream dataInputStream = null;
	
//	下面为消费者的变量
	private int readBannerBytes = 0;
	private int bannerLength = 2;
	private int readFrameBytes = 0;
	private int frameBodyLength = 0;
	private byte[] frameBody = new byte[0];
	private Banner banner = new Banner();
	
	
	public Minicap(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
		//this.idevice=deviceEntity.getIdevice();
		try {
			socket = new Socket(deviceEntity.getMinicapEntity().getHost(),
					deviceEntity.getMinicapEntity().getPort());
		} catch (IOException e) {
			logger.error("minicap socket 建立失败",e);
		}
		
		try {
			if(socket != null){
				inputStream=socket.getInputStream();
				dataInputStream=new DataInputStream(inputStream);
			}
		} catch (IOException e) {
			logger.error("inputStream 打开失败",e);
		}
		//imageDataQueue=deviceEntity.getMinicapEntity().getImageDataQueue();
		
	}
	
	public final void getPicture(){
		if (deviceEntity.getIsRunning() && !socket.isClosed()) {
			byte[] buffer = null;
			int len = 0;
			try {
				len = dataInputStream.available();
			} catch (IOException e) {
				logger.error("["+deviceEntity.getSerialNumber()+"]屏幕数据接收IO 异常",e);
				SystemWSSender.error("["+deviceEntity.getSerialNumber()+"]屏幕数据接收IO 异常");
			}
			if (len == 0 ) {
				
			}else {
				buffer = new byte[len];
				try {
					dataInputStream.read(buffer);
				} catch (IOException e) {
					logger.error("dataInputStream read failed",e);
				}
				deviceEntity.getMinicapEntity().getImageDataQueue().add(buffer);
			}
		}else {
			//closeAll();
		}
	}
	
	public final void sendPicture(){
		if (deviceEntity.getIsRunning()) {
			byte[] binaryData = deviceEntity.getMinicapEntity().getImageDataQueue().poll();
			if (binaryData == null) {
			}else {
				int len = binaryData.length;
				for (int cursor = 0; cursor < len;) {
					int byte10 = binaryData[cursor] & 0xff;
					if (readBannerBytes < bannerLength) {
						cursor = parserBanner(cursor, byte10);
					} else if (readFrameBytes < 4) {
						// 第二次的缓冲区中前4位数字和为frame的缓冲区大小
						frameBodyLength += (byte10 << (readFrameBytes * 8)) >>> 0;
						cursor += 1;
						readFrameBytes += 1;
					} else {
						//增加frame缓冲区的大小判断，防止出现负数的情况
						if (len - cursor >= frameBodyLength && frameBodyLength>=0) {
							byte[] subByte = subByteArray(binaryData, cursor,
									cursor + frameBodyLength);
							frameBody = byteMerger(frameBody, subByte);
							if ((frameBody.length>=1 && frameBody[0] != -1) || (frameBody.length>=2 && frameBody[1] != -40)) {
								logger.error(String
										.format("Frame body does not start with JPG header"));
								return;
							}
							byte[] finalBytes = subByteArray(frameBody, 0,
									frameBody.length);
							
							ScreenImageDistribute.add(deviceEntity.getSerialNumber(), finalBytes);

							cursor += frameBodyLength;
							frameBodyLength = 0;
							readFrameBytes = 0;
							frameBody = new byte[0];
						} else if(len >= cursor) {
							byte[] subByte = subByteArray(binaryData, cursor,
									len);
							frameBody = byteMerger(frameBody, subByte);
							frameBodyLength -= (len - cursor);
							readFrameBytes += (len - cursor);
							cursor = len;
						}
					}
				}
				
			}
		}else {
			//closeAll();
		}
	}
	
	private int parserBanner(int cursor, int byte10) {
		switch (readBannerBytes) {
		case 0:
			// version
			banner.setVersion(byte10);
			break;
		case 1:
			// length
			bannerLength = byte10;
			banner.setLength(byte10);
			break;
		case 2:
		case 3:
		case 4:
		case 5:
			// pid
			int pid = banner.getPid();
			pid += (byte10 << ((readBannerBytes - 2) * 8)) >>> 0;
			banner.setPid(pid);
			break;
		case 6:
		case 7:
		case 8:
		case 9:
			// real width
			int realWidth = banner.getReadWidth();
			realWidth += (byte10 << ((readBannerBytes - 6) * 8)) >>> 0;
			banner.setReadWidth(realWidth);
			break;
		case 10:
		case 11:
		case 12:
		case 13:
			// real height
			int realHeight = banner.getReadHeight();
			realHeight += (byte10 << ((readBannerBytes - 10) * 8)) >>> 0;
			banner.setReadHeight(realHeight);
			break;
		case 14:
		case 15:
		case 16:
		case 17:
			// virtual width
			int virtualWidth = banner.getVirtualWidth();
			virtualWidth += (byte10 << ((readBannerBytes - 14) * 8)) >>> 0;
			banner.setVirtualWidth(virtualWidth);

			break;
		case 18:
		case 19:
		case 20:
		case 21:
			// virtual height
			int virtualHeight = banner.getVirtualHeight();
			virtualHeight += (byte10 << ((readBannerBytes - 18) * 8)) >>> 0;
			banner.setVirtualHeight(virtualHeight);
			break;
		case 22:
			// orientation
			banner.setOrientation(byte10 * 90);
			break;
		case 23:
			// quirks
			banner.setQuirks(byte10);
			break;
		}

		cursor += 1;
		readBannerBytes += 1;

		if (readBannerBytes == bannerLength) {
			logger.debug(banner.toString());
		}
		return cursor;
	}

	// java合并两个byte数组
	private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	private byte[] subByteArray(byte[] byte1, int start, int end) {
		byte[] byte2 = new byte[end - start];
		System.arraycopy(byte1, start, byte2, 0, end - start);
		return byte2;
	}
	
	public void closeProducer(){
		if (socket!=null&&socket.isConnected()) {
			try {
				socket.close();
				dataInputStream.close();
			} catch (IOException e) {
				logger.error("socket close is failed",e);
			}
		}
	}
	
	public void closeConsumer(){
		String serialNumber = deviceEntity.getSerialNumber();
		MinicapManager.killMinicap(serialNumber, banner.getPid());
		deviceEntity.getMinicapEntity().getImageDataQueue().clear();
	}
	
	public void closeAll(){
		//String serialNumber = deviceEntity.getSerialNumber();
		closeConsumer();
		closeProducer();

	}
}



















