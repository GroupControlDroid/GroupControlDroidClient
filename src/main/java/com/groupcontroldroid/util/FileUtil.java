package com.groupcontroldroid.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.groupcontroldroid.main.Main;

public class FileUtil {

	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 *            文件路径
	 * @return Boolean
	 */
	public static Boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static Queue<String> parsePathToStack(String path) {
		Queue<String> queue = new LinkedList<>();
		String[] pathParts = path.split("/");
		if (pathParts != null && pathParts.length > 0) {
			for (String part : pathParts) {
				if (part.length() > 0) {
					queue.add(part);
				}
			}
		}
		return queue;
	}
}
