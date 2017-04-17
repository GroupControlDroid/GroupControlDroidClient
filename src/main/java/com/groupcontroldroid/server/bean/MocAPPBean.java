package com.groupcontroldroid.server.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hugeterry(http://hugeterry.cn) Date: 16/7/28 16:05
 */
public class MocAPPBean {
	@JsonIgnoreProperties(ignoreUnknown = true)

	private static MocAPPBean mocBean;

	private int id = 0;// id
	private String Airplane = ""; // 飞行模式
	private String WIFI = ""; // wifi
	private String BatterySourceLabel = ""; // 连接类型（USB）
	private int BatteryLevel = 100;// 电量
	private String ConnTypeName = "";// 网络通信类型
	private String ConnIsConnected = "";// 是否有网络通信
	private String imei = "";// imei
	private String Serial="";
    private boolean minicapState = true;//minicap状态
    private boolean minitouchState = true;//minitouch状态
    
	public MocAPPBean() {
	}

	public MocAPPBean(int id, String airplane, String wIFI, String batterySourceLabel, int batteryLevel,
			String connTypeName, String connIsConnected, String imei, String serial, boolean minicapState,
			boolean minitouchState) {
		super();
		this.id = id;
		Airplane = airplane;
		WIFI = wIFI;
		BatterySourceLabel = batterySourceLabel;
		BatteryLevel = batteryLevel;
		ConnTypeName = connTypeName;
		ConnIsConnected = connIsConnected;
		this.imei = imei;
		Serial = serial;
		this.minicapState = minicapState;
		this.minitouchState = minitouchState;
	}

	public static MocAPPBean getMocBean() {
		return mocBean;
	}

	public static void setMocBean(MocAPPBean mocBean) {
		MocAPPBean.mocBean = mocBean;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAirplane() {
		return Airplane;
	}

	public void setAirplane(String airplane) {
		Airplane = airplane;
	}

	public String getWIFI() {
		return WIFI;
	}

	public void setWIFI(String wIFI) {
		WIFI = wIFI;
	}

	public String getBatterySourceLabel() {
		return BatterySourceLabel;
	}

	public void setBatterySourceLabel(String batterySourceLabel) {
		BatterySourceLabel = batterySourceLabel;
	}

	public int getBatteryLevel() {
		return BatteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		BatteryLevel = batteryLevel;
	}

	public String getConnTypeName() {
		return ConnTypeName;
	}

	public void setConnTypeName(String connTypeName) {
		ConnTypeName = connTypeName;
	}

	public String getConnIsConnected() {
		return ConnIsConnected;
	}

	public void setConnIsConnected(String connIsConnected) {
		ConnIsConnected = connIsConnected;
	}

	public String getImei() {
		return imei;
	}         

	public void setImei(String imei) {
		this.imei = imei;
	}

    public String getSerial() {
		return Serial;
	}

	public void setSerial(String serial) {
		Serial = serial;
	}
	
	public boolean isMinicapState() {
		return minicapState;
	}

	public void setMinicapState(boolean minicapState) {
		this.minicapState = minicapState;
	}

	public boolean isMinitouchState() {
		return minitouchState;
	}

	public void setMinitouchState(boolean minitouchState) {
		this.minitouchState = minitouchState;
	}

	@Override
	public String toString() {
		return "MocAPPBean [id=" + id + ", Airplane=" + Airplane + ", WIFI=" + WIFI + ", BatterySourceLabel="
				+ BatterySourceLabel + ", BatteryLevel=" + BatteryLevel + ", ConnTypeName=" + ConnTypeName
				+ ", ConnIsConnected=" + ConnIsConnected + ", imei=" + imei + ", minicapState=" + minicapState
				+ ", minitouchState=" + minitouchState + "]";
	}
	
	public String getMiniState(){
		return "MocAPPBean [id=" + id + "minicapState=" + minicapState
				+ ", minitouchState=" + minitouchState + "]";
		
	}
	
}
