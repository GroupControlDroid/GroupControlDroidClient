package com.groupcontroldroid.units.device.base;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.groupcontroldroid.entity.DeviceEntity;


/**
 * 设备列表容器
 */
public class DeviceContainerHandler {
	//设备map表 key:设备serial number，value: 设备实体类
	private final static Map<String, DeviceEntity> deviceMap = new ConcurrentHashMap<String, DeviceEntity>();
	
	public static void addDevice(DeviceEntity deviceEntity){
		if(deviceEntity != null && deviceEntity.getSerialNumber() != null && !deviceMap.containsKey(deviceEntity.getSerialNumber())){
			deviceMap.put(deviceEntity.getSerialNumber(), deviceEntity);
		}
	}
	
	public static void deleteDevice(String serialNumber){
		
		  synchronized (deviceMap) {  
	            Iterator<Map.Entry<String, DeviceEntity>> t = deviceMap.entrySet().iterator();  
	            while (t.hasNext()) {  
	                Map.Entry<String, DeviceEntity> entry = t.next();  
	                String key = entry.getKey();  
	                if (key.equals(serialNumber)) { // 看下key是否是需要删除的key是的话就删除  
	                	deviceMap.remove(key);  
	                    break;  
	                }  
	            }  
	        }  
	}
	
	public static DeviceEntity getDevice(String serialNumber){
		if(serialNumber != null){
		return deviceMap.get(serialNumber);
		}else{
			return null;
		}
	}
	
	public static Boolean hasDevice(String serialNumber){
		return serialNumber != null && deviceMap.containsKey(serialNumber);
	}
	
	public static Set<String> getKeySet(){
		return deviceMap.keySet();
	}
	
	public static Set<Entry<String, DeviceEntity>> getMapEntry(){
		return deviceMap.entrySet();
	}
	
	/**
	 * 查询设备数
	 * @return
	 */
	public static int getDeviceCount(){
		return deviceMap.size();
	}
}
