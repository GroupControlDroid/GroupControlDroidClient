package com.groupcontroldroid.server.bean;

import java.util.List;

public class CodeBean {
	private List<String> serList;
	private String Text;
	
	public CodeBean() {
		super();
	}

	public CodeBean(List<String> serList, String text) {
		super();
		this.serList = serList;
		Text = text;
	}

	public List<String> getSerList() {
		return serList;
	}

	public void setSerList(List<String> serList) {
		this.serList = serList;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	@Override
	public String toString() {
		return "CodeBean [serList=" + serList + ", Text=" + Text + "]";
	}
	
	

}
