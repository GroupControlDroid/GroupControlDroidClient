/*
 * 设备服务
 */
radishApp.service('DeviceService', ['$rootScope', 'SocketIOService', function ($rootScope, SocketIOService) {
	var socketio = SocketIOService.getInstance();

	socketio.emit("device.get_device_list", "");

	//设备列表
	if (!$rootScope.devices) {
		$rootScope.devices = {};
	}

	//设备serialNumber 列表
	if (!$rootScope.serialNumberList) {
		$rootScope.serialNumberList = [];
	}

	//groups 列表
	if (!$rootScope.groupList) {
		$rootScope.groupList = {};
	}

	//监听设备列表
	this.listenDevicesList = function () {
		if (!$rootScope.isListeningDeviceList) {
			//上线中的设备
			socketio.on("device.installing_device",function(serialNumber){
				if(!$rootScope.devices[serialNumber]){
					$rootScope.devices[serialNumber] = {};//上线中的设备
				}
			});

			//设备上线
			socketio.on("device.new_device", function (device_str) {
				var device = JSON.parse(device_str);
				if(!$rootScope.devices[device.serialNumber]){
					$rootScope.devices[device.serialNumber] = device;
				}else{
					for(var key in device){
						$rootScope.devices[device.serialNumber][key] = device[key];
					}
				}
				
				$rootScope.$apply();
				console.log($rootScope.devices);
			});

			//设备列表
			socketio.on("device.device_list", function (device_list_str) {
				var device_list = JSON.parse(device_list_str);
				$rootScope.devices = {};
				for (var i in device_list) {
					$rootScope.devices[device_list[i].serialNumber] = device_list[i];
				}
				$rootScope.$apply();
				console.log($rootScope.devices);
			});

			//设备下线
			socketio.on("device.offline_device", function (serialNumber) {
				if ($rootScope.devices[serialNumber]) {
					delete ($rootScope.devices[serialNumber]);
				}
				$rootScope.$apply();
			});

			$rootScope.isListeningDeviceList = true;
		}
	};

	//获取需要群控的设备的serialNumber 列表
	//参数 ignoreControlMode true/false 是否忽略群控模式，直接返回群控列表
	this.getGroupControlDevices = function (ignoreControlMode) {
		if (!arguments[0]) {
			ignoreControlMode = false;//默认不忽略群控模式
		}
		//console.log(ignoreControlMode);
		if ($rootScope.control_devices && ($rootScope.isMultiControlMode || ignoreControlMode)) {
			//群控模式
			var list = [];
			for (var serialNumber in $rootScope.control_devices) {
				if ($rootScope.devices[serialNumber]) {
					list.push(serialNumber);
				}
			}
			return list;
		} else if ($rootScope.main_control_device_serial) {
			//单控模式
			return [$rootScope.main_control_device_serial];
		} else {
			return null;
		}
	};

	//统计控制的设备总数
	this.countControlDevices = function () {
		var i = 0;
		if ($rootScope.control_devices) {
			for (var serialNumber in $rootScope.control_devices) {
				i++;
			}
		}
		return i;
	};

	//统计设备总数
	this.countDevices = function () {
		var i = 0;
		if ($rootScope.devices) {
			for (var serialNumber in $rootScope.devices) {
				i++;
			}
		}
		return i;
	};

	/*
	 * 获取设备串号列表（已排序）
	 */
	this.getSerialNumberList = function () {
		$rootScope.serialNumberList = [];
		for (var serialNumber in $rootScope.devices) {
			$rootScope.serialNumberList.push(serialNumber);
		}
		$rootScope.serialNumberList.sort();
		return $rootScope.serialNumberList;
	};
}]);