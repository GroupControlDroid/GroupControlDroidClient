package com.groupcontroldroid.server.bean;

import java.util.List;

public class InstallBean {
	List<String> serialNumberList;
	String apkPath;
	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	public String getApkPath() {
		return apkPath;
	}
	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}
}
