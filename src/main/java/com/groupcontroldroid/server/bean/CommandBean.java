package com.groupcontroldroid.server.bean;

import java.util.List;

public class CommandBean {
	private List<String> serList;
	private String command;
	
	public CommandBean() {
		super();
	}
	public CommandBean(List<String> serList, String command) {
		super();
		this.serList = serList;
		this.command = command;
	}
	public List<String> getSerList() {
		return serList;
	}
	public void setSerList(List<String> serList) {
		this.serList = serList;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	@Override
	public String toString() {
		return "CommandBean [serList=" + serList + ", command=" + command + "]";
	}

}
