radishApp.controller('controlledDevicesController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			/*init */
			$scope.container_style = {
				height:document.body.clientHeight - 290,
				overfollow:"auto"
			};
			$scope.rand_num = Math.random();
			$scope.item_container_style = {
				height:320
			};

			var main_control_serial = $scope.$parent.serialNumber;//主控设备的串号
			updateControlledDevices();

			/* 更新被控制设备 */
			function updateControlledDevices(){
				$scope.devices = {};
				var controlledSerialNumberList = DeviceService.getGroupControlDevices(true);//忽略群控模式，获取群控列表
				for(var i in controlledSerialNumberList){
					if(controlledSerialNumberList[i] != main_control_serial){
						var device = $rootScope.devices[controlledSerialNumberList[i]];
						if(device){
							$scope.devices[device.serialNumber] = device;
						}
					}
				}
				$scope.controlledSerialNumberList = controlledSerialNumberList;
			}

			$scope.$watch(function () { return $rootScope.devices }, function (new_devices, old_devices) {
				updateControlledDevices();
			}, true);

			/* 监听主控，如果主控发生了变换，那么就更新被控屏幕列表 */
			$scope.$watch(function () { return $scope.$parent.serialNumber }, function (new_serialNumber, old_serialNumber) {
				main_control_serial = $scope.$parent.serialNumber;
				updateControlledDevices();
			}, true);
		}
	]);