package com.groupcontroldroid.server.bean;

import java.util.List;

/**
 * 打开应用activity bean
 */
public class OpenActivityBean {
	private String packageName;
	private String activityName;
	private List<String> serialNumList;
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public List<String> getSerialNumList() {
		return serialNumList;
	}
	public void setSerialNumList(List<String> serialNumList) {
		this.serialNumList = serialNumList;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
