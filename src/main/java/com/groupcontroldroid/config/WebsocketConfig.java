package com.groupcontroldroid.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import com.groupcontroldroid.util.NetUtil;

/**
 * Websocket配置
 */
public class WebsocketConfig {
	final public static String hostname = NetUtil.getLocalIp();
	final public static int port = 9092;

	final public static String DEVICE_GET_DEVICE_LIST = "device.get_device_list";

	final public static String EVENT_START_SCREEN = "screen.start_monitor";
	final public static String EVENT_STOP_SCREEN = "screen.stop_monitor";
	
	final public static String CLIENT_JOIN_TO_SCREEN="screen.join_to_screen";

	final public static String touchPressed = "touch.Pressed";
	final public static String touchReleased = "touch.Released";
	final public static String touchDragged = "touch.Dragged";
	final public static String touchEntity = "touch.Entity";

	final public static String DEVICE_OPEN_WEBSITE = "device.open_website";
	final public static String DEVICE_COPYTEXT = "device.input_clipboard";
	final public static String DEVICE_INPUT_TEXT = "device.input_text";
	final public static String DEVICE_KEYEVENT= "device.input_keyevent";
	
	final public static String DEVICE_OPEN_PACKAGE_ACTIVITY = "device.open_package_activity";
	final public static String DEVICE_INSTALL = "device.install";
	final public static String DEVICE_PUSH_FILE = "device.push_file";
	final public static String DEVICE_COMMAND = "device.command";

	private static TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();     
	
	
	static{
		for (int i = 1; i < 201; i++) {
			map.put(i, i);
		}
	}
	
	public static int getid(){
	    int a= map.firstEntry().getValue();
	    map.remove(a);
	    
	    return a;
	}
	
	public static void addId(int id){
		map.put(id, id);
	}

	

}






















