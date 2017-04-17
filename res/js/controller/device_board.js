radishApp.controller('deviceBoardController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			var serialNumber = $scope.$parent.serialNumber;
			var socketio = SocketIOService.getInstance();
			var device_status_listener;

			if ($rootScope.devices[serialNumber]) {
				//设备基本信息
				$scope.deviceID = $rootScope.devices[serialNumber].deviceID;
				$scope.phoneType = $rootScope.devices[serialNumber].phoneType;
				$scope.productModel = $rootScope.devices[serialNumber].productModel;
			}

			//apk安装上传时需要发送过去servlet的json数据
			$scope.installApkJson = JSON.stringify({
				sernumList: DeviceService.getGroupControlDevices(),
				apknum: 1
			});

			/*根据按键名称发送按键代码到手机 */
			$scope.btnSendKey = function (keyname) {
				var keyJson = {
					entityKey: KEYNAMEMAP[keyname],
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			/*打开浏览器地址*/
			$scope.openWebsite = function (website_address) {
				var url = null;
				if (website_address.indexOf("http://") >= 0 || website_address.indexOf("https://") >= 0) {
					url = website_address;
				} else {
					url = "http://" + website_address;
				}

				var json = {
					url: url.trim(),
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("device.open_website", JSON.stringify(json));
			};

			/*发送文本到设备剪贴板 */
			$scope.sendTextToClipboard = function (text) {
				var json = DeviceService.getGroupControlDevices();
				json.push(text);
				console.log(json);
				socketio.emit("device.input_clipboard", JSON.stringify(json));
			};

			/*直接发送文本到手机 */
			$scope.sendTextToDevice = function(text){
				var json = {
					serList:DeviceService.getGroupControlDevices(),
					text:text
				};
				socketio.emit("device.input_text",JSON.stringify(json));
			}

			/*打开某款应用*/
			$scope.openApp = function (package_name, activity_name) {
				var json = {
					packageName: package_name.trim(),
					activityName: activity_name.trim(),
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("device.open_package_activity", JSON.stringify(json));
			};

			/*上传安装完成*/
			$scope.complete = function (content) {
				console.log(content);
				if (content && content.code === 200) {
					var json = {
						serialNumberList: DeviceService.getGroupControlDevices(),
						apkPath: "./upload/tmp/" + content.msg
					};
					socketio.emit("device.install", JSON.stringify(json));
				}
			};

			/*input 文件标签更改事件 */
			$scope.inputFileChange = function (e) {
				if (e.value.lastIndexOf(".apk") > 0) {
					$scope.$apply(function () {
						$scope.upload_file_path = e.value;
					});
				} else {
					alert("请上传正确的apk安装文件!");
					e.value = "";
				}
			};

			/*设备执行命令 */
			$scope.executeCommand = function(command){
				var json = {
					serList:DeviceService.getGroupControlDevices(),
					command:command
				};
				socketio.emit("device.command",JSON.stringify(json));
			};

			device_status_listener = socketio.on("device.status_" + serialNumber, function (jsonStr) {
				var data = JSON.parse(jsonStr);
				$scope.$apply(function () {
					$scope.control_id = data.id;
					if(data.airplane == "on"){
						$scope.airplane = "开";
					}else{
						$scope.airplane = "关";
					}
					if(data.wifi == "true"){
						$scope.wifi = "开";
					}else{
						$scope.wifi = "关";
					}
					$scope.connTypeName = data.connTypeName;
					$scope.batteryLevel = data.batteryLevel;
					$scope.batterySourceLabel = data.batterySourceLabel;
				});
				if ($rootScope.devices[serialNumber].deviceID != serialNumber) {
					$rootScope.devices[serialNumber].deviceID
				}
			});

			//监听设备列表变化
			$scope.$watch(function () { return $rootScope.devices }, function (new_devices, old_devices) {

			}, true);

			//控制器销毁
			$scope.$on("$destroy", function () {
				socketio.removeAllListeners(device_status_listener);
			});
		}]);