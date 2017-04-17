package com.groupcontroldroid.server.bean;

import java.util.List;

public class OpenWebsiteBean {
	private String url;
	private List<String> serialNumList;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getSerialNumList() {
		return serialNumList;
	}
	public void setSerialNumList(List<String> serialNumList) {
		this.serialNumList = serialNumList;
	}
}
