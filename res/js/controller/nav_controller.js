radishApp.controller('navController',
	['$scope', '$alert', '$rootScope', '$routeParams', 'ngNotify', 'SocketIOService', '$aside',
		function ($scope, $alert, $rootScope, $routeParams, ngNotify, SocketIOService, $aside) {
			/*nav 是具有全局性的，所以一些操作暂定放在这里*/
			var socketio = SocketIOService.getInstance();
			$scope.oldPassword = "";
			$scope.newPassword1 = "";
			$scope.newPassword2 = "";

			$.getJSON("/userinfo.cgi", function (data) {
				if (data && data.username) {
					console.log(data);
					$scope.username = data.username;
					$scope.device_limit = data.device_limit;
					$scope.out_time = data.out_time;
				}
			});

			socketio.on("system.msg", function (data) {
				ngNotify.set(data);
			});

			socketio.on("system.warn", function (data) {
				ngNotify.set(data, 'warn');
			});

			socketio.on("system.error", function (data) {
				ngNotify.set(data, 'error');
			});

			$scope.aside = {
				"title": "zengyan",
				"content": "Hello Aside<br />This is a multiline message!"
			}

			$scope.changePassword = function (username, oldPassword, newPassword1, newPassword2) {
				if(oldPassword.length===0){
					ngNotify.set("旧密码不能为空");
					return;
				}
				if(newPassword1.length===0 && newPassword2.length===0){
					ngNotify.set("新密码不能为空");
					return;
				}
				if (newPassword1 != newPassword2) {
					ngNotify.set("您两处输入的新密码不一致，请重新输入");
					return;
				} else {
					$.post(
						"/changepassword.cgi",
						{
							username: username,
							oldPassword: oldPassword,
							newPassword1: newPassword1
						}, function (data) {
							if (data.code === 100) {
								ngNotify.set("修改成功");
							} else if (data.code === 200) {
								ngNotify.set("旧密码输入错误");
							}
						}
					);

				}
			};
		}
	]);