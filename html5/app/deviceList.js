
var app = new Vue({
  el: '#app',
  data: {
    deviceList:[],
    deviceSnView:'',
    secretKeyView:'',
    deviceIdView:'',
    pageOption : {
          "pageTotal":0, //必填,总页数
          "pageAmount":20, //每页多少条
          "dataTotal":0, //总共多少条数据
          "curPage":1, //初始页码,不填默认为1
          "pageSize": 10, //分页个数,不填默认为5
        },
    selectedDeviceId:null,
    selectedDevice:null,
    prefix_realtime:"realtime_",
    prefix_datDecode:"datDecode_",
    timestamp:0
  },
  methods:{
    showSecretKey:function(sn, key, deviceId){
      app.deviceSnView = sn;
      app.secretKeyView = byteToString(hexStringToBytes(key));
      app.deviceIdView = deviceId;
      $("#modal-showSecretKey").modal("show");
    },
    closeSecretKey:function() {
      app.deviceSnView = '';
      app.secretKeyView = '';
    },
    
    openEditDevice:function(deviceId){
      loadDeviceData(deviceId);
      $("#modal-editDevice").modal("show");
    },

    openEditContact:function(deviceId){
      app.selectedDeviceId = deviceId;
      app.selectedDevice = getSelectedDevice(deviceId)
      loadContactData(deviceId);
      // open action when loadContactData succeed
    },

    openCloneDevice:function(deviceId){
      app.selectedDeviceId = deviceId;
      app.selectedDevice = getSelectedDevice(deviceId)
      app_clone_device.deviceId = deviceId;
      app_clone_device.fromDeviceId = '';
      app_clone_device.fromDeviceSn = '';

      $("#modal-cloneDevice").modal("show");
    },
    openSensorList:function(deviceId) {
      app.selectedDeviceId = deviceId;
      app.selectedDevice = getSelectedDevice(deviceId);
      app.timestamp = new Date().getTime();
      $("#modal-sensorList").modal("show");
    },
    openScheduleList:function(deviceId) {
      app.selectedDeviceId = deviceId;
      app.selectedDevice = getSelectedDevice(deviceId)
      $("#modal-scheduleList").modal("show");
    },
  }
});

window.onload = function() {
  loadData();
};

function loadData(silent) {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"device.list",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"page":app.pageOption.curPage+"", "pageSize": app.pageOption.pageAmount+""}
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;


      makeDeviceList(result);
      if (!silent) {
        layer.msg("设备查询成功！", {icon:1,time:1000});
      }

      var pageOption = app.pageOption;
      pageOption.dataTotal = response.total;
      pageOption.pageTotal = Math.ceil(app.pageOption.dataTotal/app.pageOption.pageAmount);
      app.pageOption = pageOption;
      console.log("app.pageOption.dataTotal=" + app.pageOption.dataTotal);
      console.log("app.pageOption.pageTotal=" + app.pageOption.pageTotal);
      setPaginator();

      setTimeout('initContextMenu()', 1000);
    });
}


function makeDeviceList(result, seq) {

  var dList =[];

  var seq = (app.pageOption.curPage-1)*app.pageOption.pageAmount;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.deviceId = record['deviceId'];
    item.deviceSn = record['deviceSn'];
    item.deviceName = record['deviceName'];
    item.sensorAmount = record['sensorAmount'];
    item.crcMode = record['crcMode']==1?"CRC16_MODBUS":"";
    item.memo = record['memo']?record['memo']:"";
    item.secretKey = record['secretKey'];
    dList[i] = item;
  }
  app.deviceList = dList;
  console.log(app.deviceList.length);

}


var app_add_device = new Vue({
  el: '#modal-addDevice',
  data: {
    deviceName:'',
    memo:'',
  },
  methods:{
    openAddDevice:function(){
      app_add_device.deviceName = '';
      app_add_device.memo = '';
      $("#modal-addDevice").modal("show");
    },
  }
});

function doAddDevice() {

    if (!checkAuth()) {
      return;
    }

    var param = {"method":"device.add",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{
                  "deviceName":app_add_device.deviceName,
                  "memo":app_add_device.memo,
                }
              };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        var deviceSn = response.deviceSn;

        layer.msg("新增设备成功！" + "(" + deviceSn + ")", {icon:1,time:2000});

        setTimeout('reloadDataOnAdd()', 2000);

      });
}

function reloadDataOnAdd() {
    $("#modal-addDevice").modal("hide");
    setTimeout('loadData()', 1000);
}


var app_edit_device = new Vue({
  el: '#modal-editDevice',
  data: {
    deviceId:'',
    deviceName:'',
    memo:'',
    deviceSn:'',
    secretKey:'',
  },
  methods:{

  }
});


function loadDeviceData(deviceId) {
  for(var i in app.deviceList) {
    var item = app.deviceList[i];
    if (item.deviceId == deviceId) {
      app_edit_device.deviceId = item.deviceId;
      app_edit_device.deviceName = item.deviceName;
      app_edit_device.memo = item.memo;
      app_edit_device.deviceSn = item.deviceSn;
      app_edit_device.secretKey = byteToString(hexStringToBytes(item.secretKey));
    }
  }
}

function doUpdateDevice() {

    if (!checkAuth()) {
      return;
    }

    var param = {"method":"device.update",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{
                  "deviceId":app_edit_device.deviceId+'',
                  "deviceName":app_edit_device.deviceName,
                  "memo":app_edit_device.memo,
                  "deviceSn":app_edit_device.deviceSn,
                  "deviceSecretKey":app_edit_device.secretKey,
                }
              };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        var deviceSn = response.deviceSn;

        layer.msg("修改设备成功！", {icon:1,time:2000});

        setTimeout('reloadDataOnUpdate()', 2000);

      });
}

function reloadDataOnUpdate() {
    $("#modal-editDevice").modal("hide");
    setTimeout('loadData()', 1000);
}

function doDeleteDevice() {
    if (!confirm("确定要删除该设备吗？\n 注意：删除后不可恢复！")) {
      return;
    }
    if (!checkAuth()) {
      return;
    }

    var param = {"method":"device.delete",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{
                  "deviceId":app_edit_device.deviceId+'',
                }
              };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        var deviceSn = response.deviceSn;

        layer.msg("删除设备成功！", {icon:1,time:2000});

        setTimeout('reloadDataOnUpdate()', 2000);

      });
}


function getSelectedDevice(deviceId) {

  var list = app.deviceList;

  for(i in list) {
    if (list[i].deviceId == deviceId) {
      return list[i];
    }
  }

  return  null;
}



function setPaginator() {

  $("#pagination").empty();

  // return;
  var obj = new Page({
      id: 'pagination',
      pageTotal: app.pageOption.pageTotal, //必填,总页数
      pageAmount: app.pageOption.pageAmount,  //每页多少条
      dataTotal: app.pageOption.dataTotal, //总共多少条数据
      curPage:app.pageOption.curPage, //初始页码,不填默认为1
      pageSize: app.pageOption.pageSize, //分页个数,不填默认为5
      showPageTotalFlag:true, //是否显示数据统计,不填默认不显示
      showSkipInputFlag:true, //是否支持跳转,不填默认不显示
      getPage: function (page) {
          //获取当前页数
         console.log("获取当前页数:" + page);
         app.pageOption.curPage = page;
         loadData();
      }
  });

  return obj;
}

/*
  context menu start
*/

function initContextMenu() {
  $('.needContextMenu').each(
    function(index, element) {

      var arr = element.id.split("_"); // edit_deviceId, ctrl_deviceId
      if (!arr) {
        return;
      } 
      var type = arr[0];
      var id = arr[1];
      // var tag = element.innerHTML;
      addContextMenu(element.id, getActions(type, id));
  });
}


function addContextMenu(_id, _actions) {
  if (_actions.length == 0) {
    return;
  }

  var menuRight = new BootstrapMenu('#'+_id, {
    menuEvent: 'click',
    menuSource: 'element',
    menuPosition: 'belowLeft',
    actions: _actions,
  });
}


function getActions(type, id) {

  var actions = [];
  if (type=='conf') {
    actions = [
      {
        name:'修改设备',
        onClick:function() {
          app.openEditDevice(id);
        }
      },
      {
        name:'设置解码器',
        onClick:function() {
          if (confirm("是否跳转 解码器设置 页面？")) {
            document.getElementById("11"+id).click();
          }
        }
      },
      {
        name:'计划任务',
        onClick:function() {
          app.openScheduleList(id);
        }
      },
      {
        name:'导入配置',
        onClick:function() {
          app.openCloneDevice(id);
        }
      },
      {
        name:'报警设置',
        onClick:function() {
          app.openEditContact(id);
        }
      }
    ];
  }

  if (type=='ctrl') {
    actions = [
      {
        name:'实时控制',
        onClick:function() {
          document.getElementById("21"+id).click();
        }
      },
      // {
      //   name:'历史数据',
      //   onClick:function() {
      //   }
      // },
      // {
      //   name:'状态监控',
      //   onClick:function() {
      //   }
      // }
    ];
  }


  return actions;
}

var app_edit_contact = new Vue({
  el: '#modal-editContact',
  data: {
    deviceId:'',
    email:'',
  },
  methods:{

  }
});


function loadContactData(deviceId) {
  
  app_edit_contact.deviceId = '';
  app_edit_contact.email = '';

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"setting.contact.get",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                "deviceId":deviceId+'',
              }
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      app_edit_contact.deviceId = deviceId;
      if(response.status == 1) {
        app_edit_contact.email = response.email.replaceAll(",", ",\n");
      } 

      if (response.status == 11) {
        layer.msg("没有添加联系人！", {icon:1,time:2000});
      } else {
        layer.msg("联系人获取成功！", {icon:1,time:2000});
      }

      $("#modal-editContact").modal("show");

    });
  
}

function doSaveContact() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"setting.contact.save",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                "deviceId":app_edit_contact.deviceId+'',
                "email":app_edit_contact.email
              }
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("联系人保存成功！", {icon:1,time:2000});

      setTimeout('hideEditContact()', 2000);

    });
}

function hideEditContact() {
  $("#modal-editContact").modal("hide");
}


var app_schedule_list = new Vue({
  el: '#modal-scheduleList',
  data: {
    deviceId:'',
  },
  methods:{

  }
});


var app_clone_device = new Vue({
  el: '#modal-cloneDevice',
  data: {
    deviceId:'',
    fromDeviceId:'',
    fromDeviceSn:'',
  },
  methods:{

  }
});


function doCloneDevice() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"device.clone",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                "deviceId":app_clone_device.deviceId+'',
                "fromDeviceId":app_clone_device.fromDeviceId+'',
                "fromDeviceSn":app_clone_device.fromDeviceSn

              }
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("导入配置成功！", {icon:1,time:2000});

      setTimeout('hideCloneDevice()', 2000);

    });
}


function hideCloneDevice() {
  $("#modal-cloneDevice").modal("hide");
  loadData();

}
