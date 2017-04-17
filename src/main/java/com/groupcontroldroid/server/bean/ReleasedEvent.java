package com.groupcontroldroid.server.bean;

import java.util.ArrayList;

public class ReleasedEvent {
	private int seq;
	private String contact;
	private float x;
	private float y;
	private ArrayList<String> serialNumList;
	
	
	
	public ReleasedEvent() {
		super();
	}



	public ReleasedEvent(int seq, String contact, float x, float y, ArrayList<String> serialNumList) {
		super();
		this.seq = seq;
		this.contact = contact;
		this.x = x;
		this.y = y;
		this.serialNumList = serialNumList;
	}



	public int getSeq() {
		return seq;
	}



	public void setSeq(int seq) {
		this.seq = seq;
	}



	public String getContact() {
		return contact;
	}



	public void setContact(String contact) {
		this.contact = contact;
	}



	public float getX() {
		return x;
	}



	public void setX(float x) {
		this.x = x;
	}



	public float getY() {
		return y;
	}



	public void setY(float y) {
		this.y = y;
	}



	public ArrayList<String> getSerialNumList() {
		return serialNumList;
	}



	public void setSerialNumList(ArrayList<String> serialNumList) {
		this.serialNumList = serialNumList;
	}



	@Override
	public String toString() {
		return "ReleasedEvent [seq=" + seq + ", contact=" + contact + ", x=" + x + ", y=" + y + ", serialNumList="
				+ serialNumList + "]";
	}
	
	
	
	

}
