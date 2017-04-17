radishApp.controller('deviceScreenItemController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			var BLANK_IMG = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
			var socketio = SocketIOService.getInstance();

			var serialNumber;
			var canvas;
			var $canvas;
			var context;
			var scale = 0;//缩放比例
			var virtualWidth = 0, virtualHeight = 0;//屏幕虚拟宽度高度

			/*
			 * 初始化
			 * @param serialNumber 设备串号
			 * @param height canvas高度
			 */
			$scope.init = function (serialNumber, height) {
				$scope.serialNumber = serialNumber;
				socketio.emit("screen.stop_monitor", serialNumber);
				setTimeout(function () {
					canvas = document.getElementById("canvas_item_" + serialNumber);
					console.log("canvas_item_" + serialNumber);
					$canvas = $(canvas);
					context = canvas.getContext("2d");

					if ($rootScope.devices[serialNumber]) {
						virtualHeight = Math.round(height);
						scale = virtualHeight / $rootScope.devices[serialNumber].screenHeight;
						virtualWidth = Math.round(($rootScope.devices[serialNumber].screenWidth * scale));

						canvas.width = virtualWidth;
						canvas.height = virtualHeight;

						startScreen(serialNumber, virtualWidth, virtualHeight);
					}
				}, 60);
			}

			function startScreen(serialNumber, virtualWidth, virtualHeight) {
				/* 开启屏幕监听请求 */
				var startScreenJson = {
					serialNumber: serialNumber,
					width: virtualWidth,
					height: virtualHeight
				};
				var img;
				var URL;
				var blob;
				var u;
				console.log(startScreenJson);
				drawLoading();
				
				socketio.emit("screen.start_monitor", JSON.stringify(startScreenJson));

				socketio.on("screen.image_" + serialNumber, function (data) {
					blob = new Blob([data], { type: 'image/jpeg' });
					URL = window.URL || window.webkitURL;
					img = new Image();
					u = URL.createObjectURL(blob);
					img.src = u;
					img.onload = imgOnLoad;
				});

				function imgOnLoad() {
					canvas.width = virtualWidth;
					canvas.height = virtualHeight;
					context.drawImage(img, 0, 0, img.width, img.height,0,0,virtualWidth,virtualHeight);
					img.onload = null;
					img.src = BLANK_IMG;
					u = null;
					blob = null;
				}
			}

			function drawLoading() {
				var str = "Loading...";
				context.font = "15px Arial";
				context.fillStyle = "white";
				context.fillText(str, virtualWidth / 2 - 30, virtualHeight / 2);
			}

			//控制器销毁
			$scope.$on("$destroy", function () {
				console.log("销毁控制器");
				socketio.removeAllListeners("screen.image_" + serialNumber);
				socketio.emit("screen.stop_monitor", serialNumber);
			});
		}]);