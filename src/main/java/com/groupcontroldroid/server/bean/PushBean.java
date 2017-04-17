package com.groupcontroldroid.server.bean;

import java.util.List;

public class PushBean {
	List<String> serialNumberList;
	String localPath,remotePath;
	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
}
