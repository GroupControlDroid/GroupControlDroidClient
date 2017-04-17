radishApp.controller('devicesListController', ['$scope', '$rootScope',
	'DeviceService', '$location', function ($scope, $rootScope, DeviceService, $location) {
		DeviceService.listenDevicesList();
	}]);