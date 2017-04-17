radishApp.controller('devicesGroupController',
    ['$scope', '$rootScope', 'DeviceService', '$location', '$alert', 'ngNotify', '$modal', '$routeParams', '$timeout',
        function ($scope, $rootScope, DeviceService, $location, $alert, ngNotify, $modal, $routeParams, $timeout) {
            DeviceService.listenDevicesList();
            // var add_group_modal = $modal({scope: $scope, template: 'views/other_group.html?v='+Math.random(), show: false});
            var add_group_modal = $modal({ scope: $scope, templateUrl: 'views/modals/add_group_modal.html', show: false });
            var modify_group_modal = $modal({ scope: $scope, templateUrl: 'views/modals/modify_group_modal.html', show: false });
            var add_to_group_modal = $modal({ scope: $scope, templateUrl: 'views/modals/add_to_group_modal.html', show: false });

            $scope.selected_group_id = $routeParams.group_id;//当前被选中的分组id
            $scope.selected_devices_count = 0;
            $scope.selected_devices = {};//被选中的设备

            $scope.groups_devices = {};
            $scope.groups_devices_count_map = {};

            $scope.is_show_ok_icon_map = {};//用于判断是否显示设备上面那个小勾是否显示

            //获取群控组
            $scope.groups = [];
            function getGroupsFromServer() {
                $.getJSON("/group/get_groups.cgi", function (data) {
                    if (data) {
                        $scope.$apply(function () {
                            $scope.groups = data;
                        });
                    }
                });
            }

            function getAllDevicesGroupsFromServer() {
                $.getJSON("/group/get_all_groups.cgi", function (data) {
                    if (data) {
                        $scope.$apply(function () {
                            $scope.groups_devices = data;
                        });
                    }
                });
            }

            getGroupsFromServer();
            getAllDevicesGroupsFromServer();

            // 获取某个组的数量
            $scope.getAGroupCount = function (sernumList) {
                var i = 0;
                for (var sernum in sernumList) {
                    i++;
                }
                return i;
            }

           	//监听变量变化
            $scope.$watch(function () { return [$scope.groups_devices, $scope.groups, $rootScope.devices, $scope.selected_group_id] }, function (new_obj, old_obj) {
                //统计每个分组下面有多少台设备
                var groups_devices = $scope.groups_devices;
                for (var i in $scope.groups) {
                    $scope.groups[i].devices_count = 0;
                    if (groups_devices[$scope.groups[i].group_id]) {
                        for(var j in groups_devices[$scope.groups[i].group_id]){
                            var device = groups_devices[$scope.groups[i].group_id][j];
                            if($rootScope.devices[device.serial]){
                                $scope.groups[i].devices_count++;
                            }
                        }
                    }
                }

                var grouped_serial_num_map = {};//已被分组的设备串号
                var total_grouped_devices = 0;//统计已分组的设备数
                for (var group_id in $scope.groups_devices) {
                    for (var i in $scope.groups_devices[group_id]) {
                        var device = $scope.groups_devices[group_id][i];
                        if($rootScope.devices[device.serial]){
                            grouped_serial_num_map[device.serial] = device;
                            total_grouped_devices++;
                        }
                    }
                }

                var devices = {};//当前分组所需要显示的设备
                if ($scope.selected_group_id == -1) {
                    for (var serial in $rootScope.devices) {
                        if (!grouped_serial_num_map[serial] && $rootScope.devices[serial]) {
                            devices[serial] = $rootScope.devices[serial];
                        }
                    }
                } else if (groups_devices[$scope.selected_group_id]) {
                    for (var i in groups_devices[$scope.selected_group_id]) {
                        var serial = groups_devices[$scope.selected_group_id][i].serial;
                        if ($rootScope.devices[serial]) {
                            devices[serial] = $rootScope.devices[serial];
                        }
                    }
                }

                $scope.devices = devices;
                $scope.total_no_group_devices = DeviceService.countDevices() - total_grouped_devices;
            }, true);

            //添加设备到多选
            $scope.addToSelectedDevice = function (serialNumber) {
                if ($scope.selected_devices[serialNumber]) {
                    delete ($scope.selected_devices[serialNumber]);
                    $scope.selected_devices_count--;
                } else if ($rootScope.devices[serialNumber] && !$scope.selected_devices[serialNumber]) {
                    $scope.selected_devices[serialNumber] = $rootScope.devices[serialNumber];
                    $scope.selected_devices_count++;
                }
            };

            //添加设备组对话框
            $scope.showAddGroupModal = function () {
                $scope.input_group_name = "";
                add_group_modal.$promise.then(add_group_modal.show);
            };

            //修改设备组对话框
            $scope.showModifyGroupModal = function () {
                if ($scope.selected_group_id !== -1) {
                    for (var i in $scope.groups) {
                        if ($scope.groups[i].group_id === $scope.selected_group_id) {
                            $scope.input_group_name = $scope.groups[i].name;
                        }
                    }
                    modify_group_modal.$promise.then(modify_group_modal.show);
                }
            };

            //添加设备到设备组对话框
            $scope.addToGroupModal = function () {
                if ($scope.selected_devices_count > 0) {
                    add_to_group_modal.$promise.then(add_to_group_modal.show);
                } else {
                    alert("请先选择设备");
                }
            };

            //从分组中移除设备
            $scope.deleteFromGroup = function () {
                if ($scope.selected_group_id != -1 && $scope.selected_devices_count > 0) {
                    var serial_number_list = [];
                    for (var serial in $scope.selected_devices) {
                        serial_number_list.push(serial);
                    }
                    $.post(
                        "/group/delete_device_from_group.cgi",
                        {
                            group_id: $scope.selected_group_id,
                            serial_number_list: JSON.stringify(serial_number_list)
                        }, function (data) {
                            if (data) {
                                getAllDevicesGroupsFromServer();
                                $scope.selected_devices = {};
                                $scope.selected_devices_count = 0;
                                $rootScope.control_devices = {};
                                $rootScope.main_control_device_serial = "";
                            }
                        }, "json"
                    );
                } else {
                    alert("请先选择设备");
                }
            };

            //添加设备到设备组
            $scope.addDeviceToGroup = function (group, selected_devices) {
                if (group && selected_devices) {
                    var serial_number_list = [];
                    for (var serial in selected_devices) {
                        serial_number_list.push(serial);
                    }
                    $.post(
                        "/group/add_device_to_group.cgi", {
                            serial_number_list: JSON.stringify(serial_number_list),
                            group_id: group.group_id
                        }, function (data) {
                            if (data) {
                                getAllDevicesGroupsFromServer();
                                add_to_group_modal.hide();
                                $scope.selected_devices = {};
                                $scope.selected_devices_count = 0;
                                $rootScope.control_devices = {};
                                $rootScope.main_control_device_serial = "";
                            }
                        }, "json"
                    );
                } else {
                    alert("请选择分组后再进行添加");
                }
            }

            //添加设备组
            $scope.addGroup = function (group_name) {
                $.post(
                    "/group/add_group.cgi", {
                        name: group_name
                    }, function (data) {
                        if (data && data.group_id) {
                            data.devices_count = 0;//默认设备数为0
                            $scope.groups.push(data);
                            ngNotify.set("添加分组成功");
                        } else {
                            alert("添加分组出错");
                        }
                        add_group_modal.hide();
                    }
                    , "json");
            };

            //删除设备组
            $scope.deleteGroup = function (group_id) {
                var name = "";
                var index = 0;
                for (var i in $scope.groups) {
                    if ($scope.groups[i].group_id === $scope.selected_group_id) {
                        name = $scope.groups[i].name;
                        index = i;
                        break;
                    }
                }
                var ret = confirm("您是否要确定删除“" + name + "”分组");
                if (ret) {
                    $.post(
                        "/group/delete_group.cgi", {
                            group_id: group_id
                        }, function (data) {
                            if (data) {
                                if (data.code == 200) {
                                    $scope.groups.splice(index, 1);
                                    if ($scope.groups_devices[group_id]) {
                                        delete ($scope.groups_devices[group_id]);
                                    }
                                    if ($scope.groups_devices_count_map[group_id]) {
                                        delete ($scope.groups_devices_count_map[group_id]);
                                    }
                                    ngNotify.set("删除“" + name + "”分组成功");
                                } else {
                                    alert(data.msg);
                                }
                            }
                        }, "json"
                    );
                }
            }

            //修改设备组
            $scope.modifyGroup = function (group_id, group_name) {
                if (typeof group_id === "number" && group_id > 0 && group_name.length > 0) {
                    if (group_name != $scope.input_group_name) {
                        $.post(
                            "/group/edit_group.cgi",
                            {
                                group_id: group_id,
                                name: group_name
                            }, function (data) {
                                if (data) {
                                    if (data.code == 200) {
                                        for (var i in $scope.groups) {
                                            if ($scope.groups[i].group_id === $scope.selected_group_id) {
                                                $scope.groups[i].name = group_name;
                                                modify_group_modal.hide();
                                                ngNotify.set("修改成功");
                                            }
                                        }
                                    } else {
                                        alert(data.msg);
                                    }
                                }
                            }, "json"
                        );
                    } else {

                    }

                } else {
                    alert("设备组名称不能为空");
                }
            }

            //点击选中设备组
            $scope.clickSelectGroupItem = function (group_id) {
                $scope.selected_group_id = group_id;
                $scope.selected_devices = {};
                $scope.selected_devices_count = 0;
            };



            /* 群控功能 */
            //添加到群控
            $scope.addToGroup = function (serialNumber) {
                if (!$rootScope.control_devices) {
                    $rootScope.control_devices = {};
                }

                if ($rootScope.devices[serialNumber]) {
                    $rootScope.control_devices[serialNumber] = $rootScope.devices[serialNumber];
                }
            };

            //添加当前分组的所有设备到控制组
            $scope.addCurrentGroupDevicesToControl = function () {
                $rootScope.control_devices = {};
                var is_first = true;
                var devices = $scope.devices;
                if (devices) {
                    for (var i in devices) {
                        var device = devices[i];
                        if ($rootScope.devices[device.serialNumber]) {
                            $rootScope.control_devices[device.serialNumber] = $rootScope.devices[device.serialNumber];
                            if (is_first) {
                                $rootScope.main_control_device_serial = device.serialNumber;
                                is_first = false;
                            }
                        }
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
        }

    ]

)