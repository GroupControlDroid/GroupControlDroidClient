radishApp.controller('deviceScreenItemController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService', '$timeout',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService, $timeout) {
			// var BLANK_IMG = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
			var socketio = SocketIOService.getInstance();
			var canvas;
			var $canvas;
			var context;

			var screen_start_event_listener,screen_restart_event_listener;

			var is_allow_restart_screen = true;//是否允许重启屏幕

			var cache_canvas = document.createElement("canvas"), cache_context = cache_canvas.getContext("2d");//双缓冲

			var scale = 0;//缩放比例
			var virtualWidth = 0, virtualHeight = 0;//屏幕虚拟宽度高度

			/*
			 * 初始化
			 * @param serialNumber 设备串号
			 * @param height canvas高度
			 */
			$scope.init = function (serialNumber, height) {
				$scope.serialNumber = serialNumber;
				//socketio.emit("screen.stop_monitor", serialNumber);
				setTimeout(function () {
					canvas = document.getElementById("canvas_item_" + serialNumber);
					$canvas = $(canvas);
					context = canvas.getContext("2d");

					if ($rootScope.devices[serialNumber]) {
						virtualHeight = Math.round(height);
						scale = virtualHeight / $rootScope.devices[serialNumber].screenHeight;
						virtualWidth = Math.round(($rootScope.devices[serialNumber].screenWidth * scale));

						canvas.width = virtualWidth;
						canvas.height = virtualHeight;
						cache_canvas.width = virtualWidth;
						cache_canvas.height = virtualHeight;

						startScreen(serialNumber, virtualWidth, virtualHeight);
					}
				}, 50);
			};

			function startScreen(serialNumber, virtualWidth, virtualHeight) {
				var img = new Image();
				var URL = window.URL || window.webkitURL;
				var blob;
				var blob_options = { type: 'image/jpeg' };

				socketio.emit("screen.join_to_screen", serialNumber);
				//socketio.emit("screen.stop_monitor", serialNumber);
				drawLoading();
				// setTimeout(function () {
				// 	socketio.emit("screen.start_monitor", JSON.stringify({
				// 		serialNumber: serialNumber,
				// 		width: virtualWidth,
				// 		height: virtualHeight
				// 	}));
				// }, 495);

				screen_start_event_listener = socketio.on("screen.image_" + serialNumber, function (data) {
					blob = new Blob([data], blob_options);
					img.src = URL.createObjectURL(blob);
					img.onload = imgOnLoad;
				});

				screen_restart_event_listener = socketio.on("screen.restart_screen_"+serialNumber,function(data){
					if(is_allow_restart_screen){
						socketio.removeAllListeners(screen_start_event_listener);
						startScreen(serialNumber,virtualWidth,virtualHeight);
						is_allow_restart_screen = false;
						$timeout(function(){
							is_allow_restart_screen = true;//五秒后才能再次去执行发送屏幕重启请求
						},5000);
					}
				});

				function imgOnLoad() {
					cache_context.drawImage(img, 0, 0, img.width, img.height, 0, 0, virtualWidth, virtualHeight);
					context.drawImage(cache_canvas, 0, 0);
					img.onload = null;
					blob = null;
				}
			};

			function drawLoading() {
				var str = "Loading...";
				context.font = "15px Arial";
				context.fillStyle = "white";
				context.fillText(str, virtualWidth / 2 - 30, virtualHeight / 2);
			}

			//控制器销毁
			$scope.$on("$destroy", function () {
				console.log("销毁控制器");
				socketio.removeAllListeners(screen_start_event_listener);
				//socketio.emit("screen.stop_monitor", $scope.serialNumber);
			});
		}]);