/*
 * SocketIO 服务
 */
radishApp.service('SocketIOService', ['$rootScope', 'ngNotify', function ($rootScope,ngNotify) {
    var manager;

    this.init = function () {
        if (!$rootScope.socketio) {
            var host = "";
            for (var i; i < window.location.host.length; i++) {
                if (window.location.host[i] === ":") {
                    break;
                }
                host += window.location.host[i];
            }
            $rootScope.socketio = io(host + ":" + 9092);

            $rootScope.socketio.on("disconnect", function () {
                ngNotify.set("服务端连接中断","error");
                console.log("disconnect");
            });

            $rootScope.socketio.on("reconnect", function () {
                ngNotify.set("重连服务端中...","error");
                console.log("disconnect");
            });
        }
    };

    //获取socket.io实例
    this.getInstance = function () {
        this.init();
        return $rootScope.socketio;
    };
}]);