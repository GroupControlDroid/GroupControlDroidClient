package com.groupcontroldroid.server.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.groupcontroldroid.config.AuthConfig;
import com.groupcontroldroid.config.WebsocketConfig;
import com.groupcontroldroid.server.bean.TouchEvent;
import com.groupcontroldroid.server.websocket.listener.ClientConnectListener;
import com.groupcontroldroid.server.websocket.listener.ClientDisconnectListener;
import com.groupcontroldroid.server.websocket.listener.CodeListener;
import com.groupcontroldroid.server.websocket.listener.CommandListener;
import com.groupcontroldroid.server.websocket.listener.DraggedEventListener;
import com.groupcontroldroid.server.websocket.listener.EntityEventListener;
import com.groupcontroldroid.server.websocket.listener.GetDeviceListListener;
import com.groupcontroldroid.server.websocket.listener.InstallListener;
import com.groupcontroldroid.server.websocket.listener.ClientJoinToScreen;
import com.groupcontroldroid.server.websocket.listener.OpenPackageActivityListener;
import com.groupcontroldroid.server.websocket.listener.OpenWebsiteListener;
import com.groupcontroldroid.server.websocket.listener.PressedEventListener;
import com.groupcontroldroid.server.websocket.listener.PushFileListener;
import com.groupcontroldroid.server.websocket.listener.ReleasedEventListener;
import com.groupcontroldroid.server.websocket.listener.ShowServiceListener;
import com.groupcontroldroid.server.websocket.listener.StartScreenMonitorListener;
import com.groupcontroldroid.server.websocket.listener.StopScreenMonitorListener;
import com.groupcontroldroid.server.websocket.listener.TextListener;

/**
 * Websocket服务器
 */
public class WebsocketServer extends Thread {
	final static Logger logger = LoggerFactory.getLogger(WebsocketServer.class);
	private static SocketIOServer server = null;

	public WebsocketServer(String name) {
		super(name);
		if (server == null) {
			Configuration config = new Configuration();
			//config.setHostname(WebsocketConfig.hostname);
			config.setPort(WebsocketConfig.port);
			config.setAuthorizationListener(new AuthorizationListener() {
				
				@Override
				public boolean isAuthorized(HandshakeData data) {
					// TODO Auto-generated method stub
					return !AuthConfig.isContinueLockMain();
				}
			});
			server = new SocketIOServer(config);
			
			server.addConnectListener(new ClientConnectListener());
			server.addDisconnectListener(new ClientDisconnectListener());
			
			server.addEventListener(WebsocketConfig.DEVICE_GET_DEVICE_LIST, String.class, new GetDeviceListListener());
			
			//开启屏幕传输
			server.addEventListener(WebsocketConfig.EVENT_START_SCREEN,
					String.class, new StartScreenMonitorListener());
			//关闭屏幕传输
			server.addEventListener(WebsocketConfig.EVENT_STOP_SCREEN,
					String.class, new StopScreenMonitorListener());
			//把浏览器客户端加入到屏幕传输中
			server.addEventListener(WebsocketConfig.CLIENT_JOIN_TO_SCREEN, String.class, new ClientJoinToScreen());
			
			server.addEventListener(WebsocketConfig.touchPressed, String.class,
					new PressedEventListener());
			server.addEventListener(WebsocketConfig.touchDragged, String.class,
					new DraggedEventListener());
			server.addEventListener(WebsocketConfig.touchReleased,
					String.class, new ReleasedEventListener());
			server.addEventListener(WebsocketConfig.touchEntity, String.class,
					new EntityEventListener());
			server.addEventListener(WebsocketConfig.DEVICE_INPUT_TEXT, String.class,new TextListener());
			server.addEventListener(WebsocketConfig.DEVICE_KEYEVENT, String.class, new CodeListener());
			
			server.addEventListener(WebsocketConfig.DEVICE_INSTALL, String.class, new InstallListener());
			server.addEventListener(WebsocketConfig.DEVICE_PUSH_FILE, String.class, new PushFileListener());
			server.addEventListener(WebsocketConfig.DEVICE_COMMAND, String.class, new CommandListener());
			//打开网址

			server.addEventListener(WebsocketConfig.DEVICE_OPEN_WEBSITE, String.class, new OpenWebsiteListener());
			
			//打开应用activity
			server.addEventListener(WebsocketConfig.DEVICE_OPEN_PACKAGE_ACTIVITY, String.class, new OpenPackageActivityListener());
			
		}
	}

	public static SocketIOServer getServer() {
		return server;
	}

	@Override
	public void run() {
		server.start();
		// 长时间监听
		try {
			logger.info("开启WebSocket.");
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			logger.error("开启Websocket错误", e);
		} finally {
			server.stop();
		}
	}
}
