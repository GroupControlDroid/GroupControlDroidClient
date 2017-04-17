package com.groupcontroldroid.server.jetty.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.FileListingService;
import com.android.ddmlib.FileListingService.FileEntry;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.groupcontroldroid.entity.DeviceEntity;
import com.groupcontroldroid.server.bean.FileBean;
import com.groupcontroldroid.units.device.base.DeviceContainerHandler;
import com.groupcontroldroid.util.FileUtil;
import com.groupcontroldroid.util.JsonUtil;

/**
 * 文件浏览servlet
 */
public class FileBrowseServlet extends HttpServlet {
	final static Logger logger = LoggerFactory
			.getLogger(FileBrowseServlet.class);
	private static final long serialVersionUID = 8412354608658116760L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String serialNumber = req.getParameter("serial");
		String path = req.getParameter("path");
		String outputJson = "[]";
		OutputStream os = resp.getOutputStream();
		if (serialNumber != null && path != null) {
			DeviceEntity deviceEntity = DeviceContainerHandler
					.getDevice(serialNumber);
			Queue<String> pathQueue = FileUtil.parsePathToStack(path.trim());// 解析路径
			if (deviceEntity != null && pathQueue != null) {
				IDevice device = deviceEntity.getIdevice();
				if (device != null) {
					FileListingService fileListingService = device
							.getFileListingService();
					FileEntry iterationFileEntry = fileListingService.getRoot();
					FileEntry[] fileEntries = null;
					do {
						String pathPart = pathQueue.poll();
						try {
							fileEntries = fileListingService
									.getChildrenSync(iterationFileEntry);
							if (fileEntries != null) {
								for (FileEntry fileEntry : fileEntries) {
									if (fileEntry.getName().equals(pathPart)
											&& fileEntry.isDirectory()) {
										// 迭代查找
										iterationFileEntry = fileEntry;
									}
								}
							}
						} catch (TimeoutException | AdbCommandRejectedException
								| ShellCommandUnresponsiveException e) {
							logger.info("获取文件列表出错,iterationFileEntry:"
									+ iterationFileEntry.getFullPath(), e);
							fileEntries = null;
						}
					}while (!pathQueue.isEmpty());

					try {
						fileEntries = fileListingService
								.getChildrenSync(iterationFileEntry);
						List<FileBean> fileBeanList = new ArrayList<>();
						if (fileEntries != null) {
							for (FileEntry fileEntry : fileEntries) {
								fileBeanList.add(new FileBean(fileEntry));
							}
							outputJson = JsonUtil.beanToJson(fileBeanList);
						}
					} catch (TimeoutException | AdbCommandRejectedException
							| ShellCommandUnresponsiveException e) {
						logger.info("获取文件列表出错,iterationFileEntry:"
								+ iterationFileEntry.getFullPath(), e);
					}
				} else {
					logger.warn("device is null");
				}
			} else {
				logger.warn("找不到设备实体");
			}
		}
		os.write(outputJson.getBytes("UTF-8"));
		os.close();
	}

}
