package com.groupcontroldroid.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件
 */
public class GlobalConfig {
	final static Logger logger = LoggerFactory.getLogger(GlobalConfig.class);
	private static final  String filePath = "config/config.properties";
	
	private static Properties properties = new Properties();
	
	private static int screenHeight;
	public static int cpuNums = 4;
	
	static{
		try {
			properties.load(new FileInputStream(filePath));
		} catch (IOException e) {
			logger.error("读取全局配置文件出错",e);
			System.exit(1);
		}
		
		//手机展示屏幕高度
		String heightStr = properties.getProperty("device.screenHeight");
		if(heightStr != null){
			screenHeight = new Integer(heightStr);
		}
		cpuNums=Runtime.getRuntime().availableProcessors();
	}
	
	public static int getScreenHeight(){
		return screenHeight;
	}
}
