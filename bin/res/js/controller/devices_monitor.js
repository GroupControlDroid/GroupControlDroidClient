radishApp.controller('devicesMonitorController',
	['$scope', '$rootScope', 'SocketIOService', '$routeParams', 'DeviceService',
		function ($scope, $rootScope, SocketIOService, $routeParams, DeviceService) {
			DeviceService.listenDevicesList();
			
			var col,row;
			var page;
			//页码
			if ($routeParams.page) {
				page = parseInt($routeParams.page);
				if (page < 1) {
					page = 1;
				}
			}

			$scope.page = page;

			document.body.clientHeight * 0.93
			$scope.canvas_container_style = {
				width:document.body.clientWidth*0.93,
				height:document.body.clientHeight*0.96
			};

			//行数
			if ($routeParams.row) {
				row = parseInt($routeParams.row);
				if (row < 1) {
					row = 1;
				}
				
				$scope.item_container_style = {
					height:parseInt($scope.canvas_container_style.height / row)
				};
			}

			//列数
			if ($routeParams.col) {
				col = parseInt($routeParams.col);
				if (col < 1 || col > 12) {
					col = 1;
				}
				//列数应为12的倍数
				if(12%col === 0){
					$scope.class_col = 12/col;
				}else{
					$scope.class_col = 1;
				}
			}

			$scope.col = col;
			$scope.row = row;

			//计算每个屏幕的大小

			//监听设备列表变化
			$scope.$watch(function () { return $rootScope.devices }, function (new_devices, old_devices) {
				var serialNumberList = DeviceService.getSerialNumberList();
				var num_per_page = col*row; //每页最多显示多少个设备屏幕
				$scope.devices_total = DeviceService.countDevices();
				
				$scope.page_total = Math.ceil($scope.devices_total/num_per_page);

				//页码列表
				var page_num_array = [];
				for(var i=1;i<=$scope.page_total;i++){
					page_num_array.push(i);
				}
				$scope.page_num_array = page_num_array;

				//此页码需要显示的设备屏幕列表
				var devices = {};
				if(page <= $scope.page_total){
					for(var i=(page-1)*num_per_page;i<page*num_per_page;i++){
						var device = $rootScope.devices[serialNumberList[i]];
						if(device){
							devices[device.serialNumber] = device;
						}
					}
				}
				$scope.devices = devices;
			}, true);
		}]);