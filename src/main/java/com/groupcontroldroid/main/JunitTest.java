package com.groupcontroldroid.main;

import java.awt.Point;
import java.util.Map;

import org.junit.Test;

import com.groupcontroldroid.config.WebsocketConfig;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.units.device.minitouch.EventQueue;
import com.groupcontroldroid.units.device.minitouch.Minitouch;

public class JunitTest {


	@Test
	public void id(){
		int a1=WebsocketConfig.getid();
		int a2=WebsocketConfig.getid();
		
		System.out.println(a1);
		System.out.println(a2);
		

		WebsocketConfig.addId(a1);
		
		int a3=WebsocketConfig.getid();
		int a4=WebsocketConfig.getid();
		int a5=WebsocketConfig.getid();
		
		System.out.println(a3);
		System.out.println(a4);
		System.out.println(a5);
	
		
		
		
		
	}
	
}
