radishApp.controller('dashbordController', ['$scope', '$rootScope',
	'DeviceService', '$location', '$alert', function ($scope, $rootScope, DeviceService, $location, $alert) {
		DeviceService.listenDevicesList();

		//添加到群控
		$scope.addToGroup = function (serialNumber) {
			if (!$rootScope.control_devices) {
				$rootScope.control_devices = {};
			}

			if ($rootScope.devices[serialNumber]) {
				$rootScope.control_devices[serialNumber] = $rootScope.devices[serialNumber];
			}
		};

		//添加所有设备到群控
		$scope.addAllToGroup = function(){
			$rootScope.control_devices = {};
			for(var serialNumber in $rootScope.devices){
				$rootScope.control_devices[serialNumber] = $rootScope.devices[serialNumber];
			}
		};

		//从群控中删除
		$scope.removeFromGroup = function (serialNumber) {
			if ($rootScope.control_devices[serialNumber]) {
				delete ($rootScope.control_devices[serialNumber]);
				if ($scope.main_control_device_serial == serialNumber) {
					$scope.main_control_device_serial = null;
				}
			}
		};

		//选择主控机
		$scope.toggleMainControl = function (serialNumber) {
			if (serialNumber == $scope.main_control_device_serial) {
				$scope.main_control_device_serial = null;
			} else if ($rootScope.devices[serialNumber]) {
				$scope.main_control_device_serial = serialNumber;
			}

		};

		//开始群控手机
		$scope.gotoControl = function () {
			if (DeviceService.countControlDevices() === 0) {
				alert("请选择群控设备");
				return;
			}
			if (!$scope.main_control_device_serial || $scope.main_control_device_serial.length === 0) {
				alert("请选择主控设备");
				return;
			}

			$location.path("/device_control/" + $scope.main_control_device_serial);
		}

		//监听设备列表变化
		$scope.$watch(function(){return $rootScope.devices},function(new_devices,old_devices){
			$scope.devices_count = DeviceService.countDevices();
		},true);
	}]);