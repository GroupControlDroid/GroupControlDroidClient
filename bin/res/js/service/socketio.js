/*
 * SocketIO 服务
 */
radishApp.service('SocketIOService', ['$rootScope', function ($rootScope) {
    if (!$rootScope.socketio) {
        $rootScope.socketio = io(appConfig.websocket.host + ":" + appConfig.websocket.port);
    }

    //获取socket.io实例
    this.getInstance = function () {
        return $rootScope.socketio;
    };
}]);