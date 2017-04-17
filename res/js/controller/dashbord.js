radishApp.controller('dashbordController',
 	['$scope', '$rootScope','DeviceService', '$location', '$alert', 'ngNotify',
	 function ($scope, $rootScope, DeviceService, $location, $alert, ngNotify) {
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
			var is_first = true;
			for(var serialNumber in $rootScope.devices){
				$rootScope.control_devices[serialNumber] = $rootScope.devices[serialNumber];
				if(is_first){
					$rootScope.main_control_device_serial = serialNumber;
					is_first = false;
				}
			}
		};

		//从群控中删除
		$scope.removeFromGroup = function (serialNumber) {
			if ($rootScope.control_devices[serialNumber]) {
				delete ($rootScope.control_devices[serialNumber]);
				if ($rootScope.main_control_device_serial == serialNumber) {
					$rootScope.main_control_device_serial = null;
				}
			}
		};

		//选择主控机
		$scope.toggleMainControl = function (serialNumber) {
			if (serialNumber == $rootScope.main_control_device_serial) {
				$rootScope.main_control_device_serial = null;
			} else if ($rootScope.devices[serialNumber]) {
				$rootScope.main_control_device_serial = serialNumber;
			}

		};

		//开始群控手机
		$scope.gotoControl = function () {
			if (DeviceService.countControlDevices() === 0) {
				alert("请选择群控设备");
				return;
			}
			if (!$rootScope.main_control_device_serial || $rootScope.main_control_device_serial.length === 0) {
				alert("请选择主控设备");
				return;
			}

			$rootScope.isMultiControlMode = true;//默认开启的是群控模式

			$location.path("/device_control/" + $rootScope.main_control_device_serial);
		}

		//监听设备列表变化
		$scope.$watch(function(){return $rootScope.devices},function(new_devices,old_devices){
			$scope.devices_count = DeviceService.countDevices();
		},true);
	}]);