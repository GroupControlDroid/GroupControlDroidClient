package com.groupcontroldroid.server.bean;

import com.android.ddmlib.FileListingService.FileEntry;

public class FileBean {
	String name;
	String info;
	String permissions;
	String size;
	String date;
	String time;
	String owner;
	String group;
	int type;
	
	public FileBean(){
		
	}
	
	public FileBean(FileEntry fileEntry){
		name = fileEntry.getName();
		info = fileEntry.getInfo();
		permissions = fileEntry.getPermissions();
		size = fileEntry.getSize();
		date = fileEntry.getDate();
		time = fileEntry.getTime();
		owner = fileEntry.getOwner();
		group = fileEntry.getGroup();
		type = fileEntry.getType();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
