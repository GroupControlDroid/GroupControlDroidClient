var radishApp = angular.module('RadishApp', ['mgcrea.ngStrap', 'ngRoute', 'ngUpload', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.navbar', 'ngNotify'])
	.config([
		'$routeProvider',
		function ($routeProvider) {

			$routeProvider
				.when('/', {
					templateUrl: 'views/dashbord.html',
					controller: 'dashbordController'
				})
				.when('/device_control/:serialNumber', {
					templateUrl: 'views/device_control.html',
					controller: 'deviceControlController'
				})
				.when('/devices_list', {
					templateUrl: 'views/devices_list.html',
					controller: 'devicesListController'
				})
				.when('/device_board', {
					templateUrl: 'views/devices_board.html',
					controller: 'devicesBoardController'
				})
				.when('/devices_monitor/:page/:row/:col', {
					templateUrl: 'views/devices_monitor.html',
					controller: 'devicesMonitorController'
				})
				.when('/devices_group/:group_id', {
					templateUrl: 'views/devices_group.html',
					controller: 'devicesGroupController'
				})
				.when('/device_screen_item/:serialNumber/:width/:height', {
					templateUrl: 'views/device_screen_item.html',
					controller: 'deviceScreenItemController'
				})
				.when('/devices_wall/:size',{
					templateUrl: 'views/devices_wall.html',
					controller: 'devicesWallController'
				});
		}
	]);

/* electron 鼠标右键事件绑定 */
if (typeof require != "undefined") {
	const {remote} = require('electron');
	const {Menu, MenuItem} = remote;

	const menu = new Menu();
	menu.append(new MenuItem({
		label: '刷新',
		click() {
			window.location.reload();
		}
	}));

	window.addEventListener('contextmenu', (e) => {
		e.preventDefault();
		menu.popup(remote.getCurrentWindow());
	}, false);
}