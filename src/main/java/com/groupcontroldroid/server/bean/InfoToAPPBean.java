package com.groupcontroldroid.server.bean;

public class InfoToAPPBean {
	private String style;
	private int id;
	private String key;
	
	public InfoToAPPBean() {
		super();
	}
	
	public InfoToAPPBean(String style, int id, String key) {
		super();
		this.style = style;
		this.id = id;
		this.key = key;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	


}
