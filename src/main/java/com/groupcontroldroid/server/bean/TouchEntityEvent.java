package com.groupcontroldroid.server.bean;

import java.util.ArrayList;

public class TouchEntityEvent {
	private int entityKey;
	private ArrayList<String> serialNumList;
	
	
	
	public TouchEntityEvent() {
		super();
	}



	/**
	 * @param entityKey 实体按键 adb中编号 
	 * @param serialNumList 设备列表
	 */
	public TouchEntityEvent(int entityKey, ArrayList<String> serialNumList) {
		super();
		this.entityKey = entityKey;
		this.serialNumList = serialNumList;
	}



	public int getEntityKey() {
		return entityKey;
	}



	public void setEntityKey(int entityKey) {
		this.entityKey = entityKey;
	}



	public ArrayList<String> getSerialNumList() {
		return serialNumList;
	}



	public void setSerialNumList(ArrayList<String> serialNumList) {
		this.serialNumList = serialNumList;
	}



	@Override
	public String toString() {
		return "TouchEntityEvent [entityKey=" + entityKey + ", serialNumList=" + serialNumList + "]";
	}
	
	
	
	
	

}
