package com.groupcontroldroid.server.bean;

import java.util.List;

public class TextBean {
	private List<String> serList;
	private String text;
	
	
	public TextBean() {
		super();
	}

	public TextBean(List<String> serList, String text) {
		super();
		this.serList = serList;
		this.text = text;
	}

	public List<String> getSerList() {
		return serList;
	}

	public void setSerList(List<String> serList) {
		this.serList = serList;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "textBean [serList=" + serList + ", text=" + text + "]";
	}
	
	
	

}
