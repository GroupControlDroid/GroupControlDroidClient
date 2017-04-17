package com.groupcontroldroid.server.bean;

public class AuthResponseBean {
	private long user_id;
	private String username;
	private int device_count;
	private String out_time;
	private String token;
	
	public AuthResponseBean(){
		
	}
	
	public AuthResponseBean(long user_id, String username, int device_count,
			String out_time,String token) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.device_count = device_count;
		this.out_time = out_time;
		this.token = token;
	}
	
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getDevice_count() {
		return device_count;
	}
	public void setDevice_count(int device_count) {
		this.device_count = device_count;
	}
	public String getOut_time() {
		return out_time;
	}
	public void setOut_time(String out_time) {
		this.out_time = out_time;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}