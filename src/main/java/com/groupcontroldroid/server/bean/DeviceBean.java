package com.groupcontroldroid.server.bean;

public class DeviceBean {
	private String serialNumber;
	private String cpuAbi;
	private int apiLevel;
	
	private int screenWidth;
	private int screenHeight;
	private int virtualScreenWidth;
	private int virtualScreenHeight;
	
	private String productModel;//设备型号
	private String deviceID;//imei信息
	private String phoneType;//设备类型
	
	public DeviceBean(){
		
	}
	
	public DeviceBean(String serialNumber, String cpuAbi, int apiLevel,
			int screenWidth, int screenHeight, int virtualScreenWidth, int virtualScreenHeight, String productModel,
			String deviceID, String phoneType) {
		super();
		this.serialNumber = serialNumber;
		this.cpuAbi = cpuAbi;
		this.apiLevel = apiLevel;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.virtualScreenWidth = virtualScreenWidth;
		this.virtualScreenHeight = virtualScreenHeight;
		this.productModel = productModel;
		this.deviceID = deviceID;
		this.phoneType = phoneType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getCpuAbi() {
		return cpuAbi;
	}

	public void setCpuAbi(String cpuAbi) {
		this.cpuAbi = cpuAbi;
	}

	public int getApiLevel() {
		return apiLevel;
	}

	public void setApiLevel(int apiLevel) {
		this.apiLevel = apiLevel;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public String getProductModel() {
		return productModel;
	}

	public void setProductModel(String productModel) {
		this.productModel = productModel;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public int getVirtualScreenWidth() {
		return virtualScreenWidth;
	}

	public void setVirtualScreenWidth(int virtualScreenWidth) {
		this.virtualScreenWidth = virtualScreenWidth;
	}

	public int getVirtualScreenHeight() {
		return virtualScreenHeight;
	}

	public void setVirtualScreenHeight(int virtualScreenHeight) {
		this.virtualScreenHeight = virtualScreenHeight;
	}

}
