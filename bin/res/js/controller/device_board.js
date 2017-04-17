radishApp.controller('deviceBoardController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			var serialNumber = $scope.$parent.serialNumber;
			var socketio = SocketIOService.getInstance();
			if ($rootScope.devices[serialNumber]) {
				//设备基本信息
				$scope.deviceID = $rootScope.devices[serialNumber].deviceID;
				$scope.phoneType = $rootScope.devices[serialNumber].phoneType;
				$scope.productModel = $rootScope.devices[serialNumber].productModel;
			}

			/*根据按键名称发送按键代码到手机 */
			$scope.btnSendKey = function (keyname) {
				var keyJson = {
					entityKey: KEYNAMEMAP[keyname],
					serialNumList: DeviceService.getGroupControlDevices()
				};
				socketio.emit("touch.Entity", JSON.stringify(keyJson));
			}

			// upload later on form submit or something similar
			$scope.submit = function () {
				if ($scope.form.file.$valid && $scope.file) {
					$scope.upload($scope.file);
				}
			};

			// upload on file select or drop
			$scope.upload = function (file) {
				Upload.upload({
					url: 'upload/url',
					data: { file: file, 'username': $scope.username }
				}).then(function (resp) {
					console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
				}, function (resp) {
					console.log('Error status: ' + resp.status);
				}, function (evt) {
					var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
					console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
				});
			};
			// for multiple files:
			$scope.uploadFiles = function (files) {
				if (files && files.length) {
					for (var i = 0; i < files.length; i++) {
						//Upload.upload({..., data: {file: files[i]}, ...})...;
					}
				}
			}
		}]);