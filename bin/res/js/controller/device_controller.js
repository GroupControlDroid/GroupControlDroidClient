radishApp.controller('deviceController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			var BLANK_IMG = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
			var socketio = SocketIOService.getInstance();
			var serialNumber = $routeParams.serialNumber;
			var canvas = document.getElementById("device_canvas");
			var $canvas = $(canvas);
			var context = canvas.getContext("2d");
			var scale = 0;//缩放比例
			var isMouseDown = false;//鼠标是否点下
			var seq = 1;
			var virtualWidth = 0,virtualHeight = 0;//屏幕虚拟宽度高度
			var json = {
				seq: 0,
				contact: "",
				x: 0,
				y: 0,
				serialNumList: null
			};

			$scope.serialNumber = serialNumber;

			/*init 初始化*/
			$scope.touchhand_style = {};
			if ($rootScope.devices[serialNumber]) {
				virtualHeight = Math.round((document.body.clientHeight * 0.93));
				scale = virtualHeight / $rootScope.devices[serialNumber].screenHeight;
				virtualWidth = Math.round(($rootScope.devices[serialNumber].screenWidth * scale));

				$scope.control_panel_style = {
					width: virtualWidth
				}

				$scope.canvas_style = {
					width:virtualWidth,
					height:virtualHeight
				};

				$scope.control_td_width = (virtualWidth+5)+"px";
			}else{
				window.location.href = "/";
			}

			/* 开启屏幕监听请求 */
			var startScreenJson = {
				serialNumber:serialNumber,
				width:virtualWidth,
				height:virtualHeight
			};

			//先停止此设备的监控，然后再重新发起监控请求
			socketio.emit("screen.stop_monitor", serialNumber);
			setTimeout(function(){
				socketio.emit("screen.start_monitor", JSON.stringify(startScreenJson));
			},500);

			socketio.on("screen.image_" + serialNumber, function (data) {
				var blob = new Blob([data], { type: 'image/jpeg' });
				var URL = window.URL || window.webkitURL;
				var img = new Image();
				var u = URL.createObjectURL(blob);
				img.src = u;
				img.onload = function () {
					canvas.width = img.width;
					canvas.height = img.height;
					context.drawImage(img, 0, 0);
					img.onload = null;
					img.src = BLANK_IMG;
					u = null;
					blob = null;
				};
			});

			$canvas.bind("mousedown", function (e) {
				e.preventDefault();
				json.seq = seq;
				json.x = Math.round(e.offsetX/scale);
				json.y = Math.round(e.offsetY/scale);
				json.serialNumList = DeviceService.getGroupControlDevices();
				isMouseDown = true;
				socketio.emit("touch.Pressed", JSON.stringify(json));
			});

			$canvas.bind("mouseup", function (e) {
				e.preventDefault();
				json.seq = seq;
				json.x = Math.round(e.offsetX/scale);
				json.y = Math.round(e.offsetY/scale);
				//json.serialNumList = DeviceService.getGroupControlDevices();
				isMouseDown = false;
				socketio.emit("touch.Released", JSON.stringify(json));
			});

			$canvas.bind("mousemove", function (e) {
				e.preventDefault();
				if (isMouseDown) {
					json.seq = seq;
					json.x = Math.round(e.offsetX/scale);
					json.y = Math.round(e.offsetY/scale);
					//json.serialNumList = DeviceService.getGroupControlDevices();
					socketio.emit("touch.Dragged", JSON.stringify(json));
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
			$(document).bind("keydown",function(e){
				e.preventDefault();
				console.log(e.keyCode);
				var str_keycode = e.keyCode.toString();
				if(KEYCODEMAP[str_keycode]){
					var keyJson = {
						entityKey:KEYCODEMAP[str_keycode],
						serialNumList:DeviceService.getGroupControlDevices()
					};
					socketio.emit("touch.Entity", JSON.stringify(keyJson));
				}
			});

			//设备按钮——返回
			$scope.keyDownBack = function(){
				var keyJson = {
					entityKey:KEYNAMEMAP["KEYCODE_BACK"],
					serialNumList:DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			//设备按钮——Home
			$scope.keyDownHome = function(){
				var keyJson = {
					entityKey:KEYNAMEMAP["KEYCODE_HOME"],
					serialNumList:DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			//设备按钮——Menu
			$scope.keyDownMenu = function(){
				var keyJson = {
					entityKey:KEYNAMEMAP["KEYCODE_MENU"],
					serialNumList:DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			};

			//控制器销毁
			$scope.$on("$destroy", function () {
				console.log("销毁控制器");
				socketio.removeAllListeners("screen.image_" + serialNumber);
				socketio.emit("screen.stop_monitor", serialNumber);
				$(document).unbind("keydown");
			});
		}]);