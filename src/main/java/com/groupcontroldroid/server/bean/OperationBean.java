package com.groupcontroldroid.server.bean;

public class OperationBean {
	private String msg = "";
	private int code;
	private String redirect = "";
	public OperationBean(){
		
	}
	
	public OperationBean(String msg, int code, String redirect) {
		super();
		this.msg = msg;
		this.code = code;
		this.redirect = redirect;
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getRedirect() {
		return redirect;
	}
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
}
