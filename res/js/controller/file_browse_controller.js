radishApp.controller('fileBrowseController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			var serialNumber = $scope.$parent.serialNumber;
			var socketio = SocketIOService.getInstance();

			$scope.current_path = "/sdcard/";//当前文件路径

			/* 进入手机的某个路径 */
			$scope.goToPath = function (path) {
				console.log("path:" + path);
				$.getJSON(
					"file_browse.cgi",
					{
						serial: serialNumber,
						path: path
					}, function (data) {
						if (data) {
							$scope.$apply(function () {
								$scope.files = data;
								$scope.current_path = path;
							});
						}
					}
				);
			};

			/*返回当前目录的上一级 */
			$scope.goBackPath = function (path) {
				if (path.length >= 1 && path != "/") {
					var last_index = path.length - 1;
					if (path[last_index] === '/') {
						last_index--;
					}

					var index = path.lastIndexOf("/", last_index);
					var new_path = "";
					for (var i = 0; i <= index; i++) {
						new_path += path[i]
					}
					if (new_path.length === 0) {
						new_path = "/";
					}
					console.log("new_path:" + new_path);

					$scope.goToPath(new_path);
				}
			};

			$scope.inputFileChange = function (e) {
				console.log(e.value);
			};

			$scope.complete = function(data){
				console.log(data);
				if(data && data.code === 200){
					var json = {
						serialNumberList:DeviceService.getGroupControlDevices(),
						localPath:"./upload/tmp/"+data.msg,
						remotePath:$scope.current_path+data.msg
					}
					socketio.emit("device.push_file", JSON.stringify(json),function(){
						$scope.goToPath($scope.current_path);
					});
				}
			};

			//闭包，初始化
			(function () {
				$scope.goToPath($scope.current_path);
				$scope.container_style = {
					height: document.body.clientHeight - 300,
					overflow: "auto"
				};
			})();

			//监听主控设备的变化
			$scope.$watch(function () { return $scope.$parent.serialNumber }, function (new_serial_number, old_serial_number) {
				serialNumber = new_serial_number;
				$scope.current_path = "/sdcard/";
				$scope.goToPath($scope.current_path);
			});
		}
	]);