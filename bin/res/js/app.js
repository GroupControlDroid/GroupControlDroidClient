var radishApp = angular.module('RadishApp', ['mgcrea.ngStrap', 'ngRoute', 'ngFileUpload', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.navbar'])
	.config([
		'$routeProvider',
		function ($routeProvider) {
			$routeProvider
				.when('/', {
					templateUrl: 'views/dashbord.html',
					controller: 'dashbordController'
				})
				.when('/device_control/:serialNumber',{
					templateUrl: 'views/device_control.html',
					controller: 'deviceController'
				})
				.when('/devices_list',{
					templateUrl: 'views/devices_list.html',
					controller: 'devicesListController'
				})
				.when('/device_board',{
					templateUrl: 'views/devices_board.html',
					controller: 'devicesBoardController'
				})
				.when('/devices_monitor/:page/:row/:col',{
					templateUrl: 'views/devices_monitor.html',
					controller: 'devicesMonitorController'
				})
				.when('/device_screen_item/:serialNumber/:width/:height',{
					templateUrl: 'views/device_screen_item.html',
					controller: 'deviceScreenItemController'
				});
		}
	]);

var appConfig = {
	websocket: {
		host: "127.0.0.1",
		port: 9092
	}
};
