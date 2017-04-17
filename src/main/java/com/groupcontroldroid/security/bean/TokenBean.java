package com.groupcontroldroid.security.bean;

public class TokenBean {
	long user_id;
	String token;
	boolean is_success;

	public TokenBean() {

	}

	public TokenBean(long user_id, String token, boolean is_success) {
		super();
		this.user_id = user_id;
		this.token = token;
		this.is_success = is_success;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isIs_success() {
		return is_success;
	}

	public void setIs_success(boolean is_success) {
		this.is_success = is_success;
	}
}
