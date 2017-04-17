package com.groupcontroldroid.entity;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.android.sdklib.AndroidVersion;

/**
 * 设备实体类
 */
public class DeviceEntity {
	final static Logger logger = LoggerFactory
			.getLogger(DeviceEntity.class);
	private int id;
	private String serialNumber;//adb serial number
	private String name;//设备名称
	private String productModel;//设备型号
	private String deviceID;//imei信息
	private String phoneType;//设备类型
	private List<String> abis;
	
	private MinicapEntity minicapEntity;
	private MinitouchEntity minitouchEntity;
	private ApkServiceEntity apkServiceEntity;

	private AndroidVersion androidVersion;
	
	//屏幕实际尺寸、虚拟尺寸
	private int screenWidth,screenHeight,virtualScreenWidth,virtualScreenHeight;
	
	private IDevice idevice;
	
	private Boolean hasCheck=true;//是否已经检查过在线
	private Boolean isRunning=true;//设备是否运行中。后续与此设备有关的子线程将依赖于此值
	
	public DeviceEntity(){
		
	}
	
	public DeviceEntity(IDevice idevice) {
		super();
		serialNumber = idevice.getSerialNumber();
		abis = idevice.getAbis();
		androidVersion = idevice.getVersion();
		this.idevice = idevice;
		
		//获取设备分辨率
		getScreenSizeInfo();
	}

	public ApkServiceEntity getApkServiceEntity() {
		return apkServiceEntity;
	}

	public void setApkServiceEntity(ApkServiceEntity apkServiceEntity) {
		this.apkServiceEntity = apkServiceEntity;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAbis() {
		return abis;
	}

	public void setAbis(List<String> abis) {
		this.abis = abis;
	}

	public IDevice getIdevice() {
		return idevice;
	}

	public void setIdevice(IDevice idevice) {
		this.idevice = idevice;
	}

	public Boolean getHasCheck() {
		return hasCheck;
	}

	public void setHasCheck(Boolean hasCheck) {
		this.hasCheck = hasCheck;
	}

	public AndroidVersion getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(AndroidVersion androidVersion) {
		this.androidVersion = androidVersion;
	}
	
	
	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public MinicapEntity getMinicapEntity() {
		return minicapEntity;
	}

	public void setMinicapEntity(MinicapEntity minicapEntity) {
		this.minicapEntity = minicapEntity;
	}

	public MinitouchEntity getMinitouchEntity() {
		return minitouchEntity;
	}

	public void setMinitouchEntity(MinitouchEntity minitouchEntity) {
		this.minitouchEntity = minitouchEntity;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public String getProductModel() {
		return productModel;
	}

	public void setProductModel(String productModel) {
		this.productModel = productModel;
	}

	public synchronized String getDeviceID() {
		return deviceID;
	}

	public synchronized void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public synchronized String getPhoneType() {
		return phoneType;
	}

	public synchronized void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public int getVirtualScreenWidth() {
		return virtualScreenWidth;
	}

	public void setVirtualScreenWidth(int virtualScreenWidth) {
		this.virtualScreenWidth = virtualScreenWidth;
	}

	public int getVirtualScreenHeight() {
		return virtualScreenHeight;
	}

	public void setVirtualScreenHeight(int virtualScreenHeight) {
		this.virtualScreenHeight = virtualScreenHeight;
	}

	/**
	 * 获取设备分辨率
	 */
	private void getScreenSizeInfo(){
		RawImage rawScreen;
		try {
			rawScreen = idevice.getScreenshot();
			if(rawScreen != null){
				screenWidth = rawScreen.width;
				screenHeight = rawScreen.height;
			}else{
				
			}
		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			// TODO Auto-generated catch block
			logger.error(serialNumber+":无法获取屏幕尺寸",e);
		}
	}
}
