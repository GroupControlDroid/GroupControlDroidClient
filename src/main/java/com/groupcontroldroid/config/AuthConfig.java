package com.groupcontroldroid.config;

/**
 * Oauth授权配置
 */
public class AuthConfig {
//	public final static String APP_ID = "";
//	public final static String APP_SECRET = "";
//	public final static char str[] = "a7d1d7a1ec31e70e4e0b539160b8913346369fd7".toCharArray();
//	public final static String COMMUNICATION_KEY = "a7d1d7a1ec31e70e4e0b539160b8913346369fd7";
//	public final static String SITE_URL = "127.0.0.1:8080";
	public final static String SITE_URL = "wechat.020fhd.com:8080";
	
	private static long user_id = 0; //登录成功后的用户id
	private static String username;//登陆后的用户名
	private static String token;//服务器验证token
	private static int deviceLimit = 0;//设备数目限制
	private static String outTime;//用户账户过期日期
	
	private static boolean isContinueLockMain = true;//在验证通过不允许程序进行运行
	
	public synchronized static boolean isContinueLockMain(){
		return isContinueLockMain;
	}
	
	public synchronized static void setIsContinueLockMain(boolean isContinueLockMain){
		AuthConfig.isContinueLockMain = isContinueLockMain;
	}

	public synchronized static long getUser_id() {
		return user_id;
	}

	public synchronized static void setUser_id(long user_id) {
		AuthConfig.user_id = user_id;
	}

	public synchronized static String getToken() {
		return token;
	}

	public synchronized static void setToken(String token) {
		AuthConfig.token = token;
	}
	
	public synchronized static void setDeviceLimit(int deviceLimit){
		AuthConfig.deviceLimit = deviceLimit;
	}
	
	public synchronized static int getDeviceLimit(){
		return AuthConfig.deviceLimit;
	}

	public synchronized static String getOutTime() {
		return outTime;
	}

	public synchronized static void setOutTime(String outTime) {
		AuthConfig.outTime = outTime;
	}

	public synchronized static String getUsername() {
		return username;
	}

	public synchronized static void setUsername(String username) {
		AuthConfig.username = username;
	}
}
