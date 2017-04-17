package com.groupcontroldroid.units.device.minicap;

public class ScreenImageItem{
	String serialNumber;
	byte[] bytes;
	
	public ScreenImageItem(){
		
	}
	
	public ScreenImageItem(String serialNumber,byte[] bytes){
		this.serialNumber = serialNumber;
		this.bytes = bytes;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}