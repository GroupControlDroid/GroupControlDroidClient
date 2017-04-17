radishApp.controller('deviceControlController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService', 'ngNotify', '$timeout',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService, ngNotify, $timeout) {
			var BLANK_IMG = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
			var socketio = SocketIOService.getInstance();
			//var serialNumber = $routeParams.serialNumber;
			var canvas = document.getElementById("device_canvas");
			var $canvas = $(canvas);
			var context = canvas.getContext("2d");
			var cache_canvas = document.createElement("canvas"), cache_context = cache_canvas.getContext("2d");//双缓冲
			var scale = 0;//缩放比例
			var isMouseDown = false;//鼠标是否点下
			var seq = 1;
			var virtualWidth = 0, virtualHeight = Math.round((document.body.clientHeight * 0.96));//屏幕虚拟宽度高度

			var lock_change_main_control = false;//是否锁定主控机切换

			var screen_restart_event_listener;//监听minicap是否挂了的listerner
			var is_allow_restart_screen = true;//是否允许屏幕重启

			var json = {
				seq: 0,
				contact: "",
				x: 0,
				y: 0,
				serialNumList: null
			};
			$scope.rand_num = Math.random();

			/* 开启主控 */
			$scope.startControl = function (serialNumber) {
				$scope.serialNumber = serialNumber;

				/*init 初始化*/
				$scope.touchhand_style = {};
				if ($rootScope.devices[serialNumber]) {
					scale = virtualHeight / $rootScope.devices[serialNumber].screenHeight;
					virtualWidth = Math.round(($rootScope.devices[serialNumber].screenWidth * scale));

					$scope.control_panel_style = {
						width: virtualWidth
					}

					$scope.canvas_style = {
						width: virtualWidth,
						height: virtualHeight
					};

					$scope.control_td_width = (virtualWidth + 5) + "px";
				} else {
					window.location.href = "/";
				}

				//先停止此设备的监控，然后再重新发起监控请求
				socketio.emit("screen.join_to_screen", serialNumber);
				// socketio.emit("screen.stop_monitor", serialNumber);
				// setTimeout(function () {
				// 	socketio.emit("screen.start_monitor", JSON.stringify({
				// 		serialNumber: serialNumber,
				// 		width: virtualWidth,
				// 		height: virtualHeight
				// 	}));
				// }, 500);

				canvas.width = virtualWidth;
				canvas.height = virtualHeight;
				cache_canvas.width = virtualWidth;
				cache_canvas.height = virtualHeight;

				socketio.on("screen.image_" + serialNumber, function (data) {
					var blob = new Blob([data], { type: 'image/jpeg' });
					var URL = window.URL || window.webkitURL;
					var img = new Image();
					var u = URL.createObjectURL(blob);
					img.src = u;
					img.onload = imgOnLoad;

					function imgOnLoad() {
						cache_context.drawImage(img, 0, 0, img.width, img.height, 0, 0, virtualWidth, virtualHeight);
						context.drawImage(cache_canvas, 0, 0);
						img.onload = null;
						//img.src = BLANK_IMG;
						u = null;
						blob = null;
					}
				});

				/* 监听minicap是否挂了 */
				screen_restart_event_listener = socketio.on("screen.restart_screen_" + serialNumber, function (data) {
					$scope.refreshScreen();
				});
			};

			/* 切换主控机 */
			$scope.changeMainControl = function (serialNumber) {
				if (!lock_change_main_control) {
					$rootScope.main_control_device_serial = serialNumber;
					socketio.removeAllListeners("screen.image_" + $scope.serialNumber);
					ngNotify.set("主控设备切换中，请稍后...");
					$scope.startControl(serialNumber);

					lock_change_main_control = true;//锁定一段时间不能继续切换主控
					$timeout(function () {
						lock_change_main_control = false;
					}, 4000);
				} else {
					ngNotify.set("切换操作请不要太频繁", "warn");
				}
			};

			$canvas.bind("mousedown", function (e) {
				e.preventDefault();
				json.seq = seq;
				json.x = Math.round(e.offsetX / scale);
				json.y = Math.round(e.offsetY / scale);
				json.serialNumList = DeviceService.getGroupControlDevices();
				isMouseDown = true;
				socketio.emit("touch.Pressed", JSON.stringify(json));
			});

			$canvas.bind("mouseup", function (e) {
				e.preventDefault();
				json.seq = seq;
				json.x = Math.round(e.offsetX / scale);
				json.y = Math.round(e.offsetY / scale);
				isMouseDown = false;
				socketio.emit("touch.Released", JSON.stringify(json));
			});

			$canvas.bind("mousemove", function (e) {
				e.preventDefault();
				if (isMouseDown) {
					if (seq % 2 !== 0) {
						json.seq = seq;
						json.x = Math.round(e.offsetX / scale);
						json.y = Math.round(e.offsetY / scale);
						socketio.emit("touch.Dragged", JSON.stringify(json));
					}
					seq++;
				}
			});

			$canvas.bind("mouseout", function (e) {
				e.preventDefault();
				if (isMouseDown) {
					//当鼠标离开以后提交最后一次数据
					socketio.emit("touch.Released", JSON.stringify(json));
					isMouseDown = false;
				}
			});

			//键盘按键
			$(document).bind("keydown", function (e) {
				if (!$(e.target).is("input")) {
					e.preventDefault();
					var str_keycode = e.keyCode.toString();
					if (KEYCODEMAP[str_keycode]) {
						var keyJson = {
							entityKey: KEYCODEMAP[str_keycode],
							serialNumList: DeviceService.getGroupControlDevices()
						};
						socketio.emit("touch.Entity", JSON.stringify(keyJson));
					}
				}
			});

			//设备按钮——返回
			$scope.keyDownBack = function () {
				var keyJson = {
					entityKey: KEYNAMEMAP["KEYCODE_BACK"],
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			//设备按钮——Home
			$scope.keyDownHome = function () {
				var keyJson = {
					entityKey: KEYNAMEMAP["KEYCODE_HOME"],
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			//设备按钮——Menu
			$scope.keyDownMenu = function () {
				var keyJson = {
					entityKey: KEYNAMEMAP["KEYCODE_MENU"],
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			/* 切换控制模式，单控或多控 */
			$scope.changeControlMode = function () {
				if ($rootScope.isMultiControlMode) {
					$rootScope.isMultiControlMode = false;
				} else {
					$rootScope.isMultiControlMode = true;
				}
			};

			/* 手动主动刷新屏幕 */
			$scope.refreshScreen = function () {
				if (is_allow_restart_screen) {
					socketio.removeAllListeners("screen.image_" + $scope.serialNumber);
					ngNotify.set("屏幕重启中");
					$scope.startControl($scope.serialNumber);
					is_allow_restart_screen = false;
					$timeout(function () {
						is_allow_restart_screen = true;//五秒后才能再次去执行发送屏幕重启请求
					}, 5000);
				}else{
					ngNotify.set("重启屏幕操作不能过于频繁");
				}
			}

			//控制器销毁
			$scope.$on("$destroy", function () {
				socketio.removeAllListeners("screen.image_" + $scope.serialNumber);
				socketio.removeAllListeners(screen_start_event_listener);
				//socketio.emit("screen.stop_monitor", $scope.serialNumber);
				$(document).unbind("keydown");
			});

			(function () {
				/* Main */
				$scope.startControl($routeParams.serialNumber);
			})();
		}]);