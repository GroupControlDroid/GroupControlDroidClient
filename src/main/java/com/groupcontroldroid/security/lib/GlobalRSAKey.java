package com.groupcontroldroid.security.lib;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局静态rsa公钥密钥
 */
public class GlobalRSAKey {
	final static Logger logger = LoggerFactory.getLogger(GlobalRSAKey.class);
	
	//public static final String FOLDER_PATH = "./res/security";
	public static RSAPublicKey RSAPublicKey;
	//public static RSAPrivateKey RSAPrivateKey;
	static {
		try {
			RSAPublicKey = RSAEncrypt.loadPublicKeyByStr("MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEA3AKPMXD0M6YBM5oP7LvC59/C84hTv162hSBUGrgn9AzUldGoefBoulcCyrGLsJX1E7QvHFiR6DzqFoulyoohoG3/ORtWKQeoqgSnNYgCvQNH4BIaOkZXLAOMk/LDEfEkNj1Fwa8iO1paQFS/+WtCZlnf7GP3abfbL2O7+Q0wyI8mS9RlRhcDpjnSqXgxEJDjRLXqiv90rtiYVLVzFNNxK90qjcz7rw1e9aJLtJa6Zu+8bj6LUNM0zMmbtcUdwkdFxopS5tMmqQvspOeNoXXUmHJljNaYr2VlosXOyi7c7htdevXgxzFS+Jx1J7hBH2SDwgRR4aO4F7pDnsepd+jNLuHTwF50cM3g6RfSSvxOCZgBAB9DF55feKjL+5eCFKAy/C/izSCvQYKoKi63BU2n1tnN5r2YcnoZnSCj1Su+Aj3piVUbnl7Tm+H+fLZ8zo2CwtXPQg8moQqdoH+vObBakYzobZGS+CiSzBdqVm/SwTuR2w7QoS0unjGNWWJ0U/HzAgMBAAE=");
//			RSAPrivateKey = RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt
//					.loadPrivateKeyByFile(FOLDER_PATH));
		} catch (Exception e) {
			logger.error("读取公钥密钥出错",e);
			System.exit(1);
		}
	}
}
