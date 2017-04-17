package com.groupcontroldroid.server.bean;


/**
 * 开启屏幕请求json bean
 */
public class StartScreenBean {
	private String serialNumber;
	private int width;
	private int height;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
