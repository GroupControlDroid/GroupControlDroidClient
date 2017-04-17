package com.groupcontroldroid.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网络工具类
 */
public class NetUtil {
	final static Logger logger = LoggerFactory.getLogger(NetUtil.class);

	private static void bindPort(String host, int port) throws IOException {
		Socket s = new Socket();
		s.bind(new InetSocketAddress(host, port));
		s.close();
	}

	/**
	 * 检测本地端口是否可用
	 * 
	 * @param port
	 *            端口号
	 * @return 如果可用返回true,不可用返回false
	 */
	public static boolean isPortAvailable(int port) {
		try {
			bindPort("127.0.0.1", port);
			bindPort("0.0.0.0", port);
			return true;
		} catch (IOException e) {
			logger.error("检测端口:" + port + " 被占用");
		}
		return false;
	}

	/**
	 * 获取本机内网ip
	 * 
	 * @return
	 */
	public static String getLocalIp() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error("获取本机内网ip出错", e);
		}
		return ia.getHostAddress();
	}

	public static String[] getAllLocalHostIP() {
		String[] ret = null;
		try {
			String hostName = getLocalIp();
			if (hostName.length() > 0) {
				InetAddress[] addrs = InetAddress.getAllByName("localhost");
				if (addrs.length > 0) {
					ret = new String[addrs.length];
					for (int i = 0; i < addrs.length; i++) {
						ret[i] = addrs[i].getHostAddress();
					}
				}
			}

		} catch (Exception ex) {
			ret = null;
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		for(String ip : getAllLocalHostIP()){
			System.out.println(ip);
		}
	}
}
