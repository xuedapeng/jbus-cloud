

var app = new Vue({
  el: '#app',
  data: {
    deviceList:[],
    deviceSnView:'',
    secretKeyView:'',
  },
  methods:{
    showSecretKey:function(sn, key){
      app.deviceSnView = sn;
      app.secretKeyView = byteToString(hexStringToBytes(key));
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
  }
});


loadData();
function loadData() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"device.list",
              "auth":[getStorage("appId"), getStorage("appToken")]};

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;
      makeDeviceList(result);

      layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function makeDeviceList(result, seq) {

  var dList =[];

  var seq = 0;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.deviceId = record['deviceId'];
    item.deviceSn = record['deviceSn'];
    item.deviceName = record['deviceName'];
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
