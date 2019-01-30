
var app = new Vue({
  el: '#app',
  data: {
    deviceId:getQueryString("deviceId"),
    sensorList:[],
    orgSensorList:[],
    newItem:makeNewItem(),
    isAddMode:false,
    pageOption : {
          "pageTotal":1, //必填,总页数
          "pageAmount":5, //每页多少条
          "dataTotal":1, //总共多少条数据
          "curPage":1, //初始页码,不填默认为1
          "pageSize": 5, //分页个数,不填默认为5
        },
  },
  methods:{
    toEditMode:function(sensorId) {
      doToEditMode(sensorId);
    },
    cancelEditMode:function(sensorId) {
      doCancelEditMode(sensorId);
    },
    toViewMode:function(sensorId) {
      doToViewMode(sensorId);
    },
    updateSensor:function(sensorId) {
      doUpdateSensor(sensorId);
    },
    deleteSensor:function(sensorId) {

      if(!confirm("是否删除传感器？")) {
        return;
      }

      doDeleteSensor(sensorId);
    },
    toAddMode:function() {
      app.isAddMode = true;
    },
    cancelAddMode:function(){
      app.newItem = makeNewItem();
      app.isAddMode = false;
    },
    addSensor:function(){
      doAddSensor();
    }
  }
});

window.onload = function() {
  loadData();
};

function loadData(silent) {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"sensor.list",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId,
                  "page":app.pageOption.curPage+"",
                  "pageSize": app.pageOption.pageAmount+""}
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;


      makeSensorList(result);
      if (!silent) {
        layer.msg("传感器查询成功！", {icon:1,time:1000});
      }

      var pageOption = app.pageOption;
      pageOption.dataTotal = response.total;
      pageOption.pageTotal = Math.ceil(app.pageOption.dataTotal/app.pageOption.pageAmount);
      app.pageOption = pageOption;
      console.log("app.pageOption.dataTotal=" + app.pageOption.dataTotal);
      console.log("app.pageOption.pageTotal=" + app.pageOption.pageTotal);
      setPaginator();
    });
}


function makeSensorList(result, seq) {

  var dList =[];
  var oList = [];

  var seq = (app.pageOption.curPage-1)*app.pageOption.pageAmount;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.sensorId = record['sensorId'];
    item.sensorNo = record['sensorNo'];
    item.sensorName = record['sensorName'];
    item.memo = record['memo']?record['memo']:"";
    item.isEdit = false;
    dList[i] = item;

    var oitem={};
    oitem.seq = seq;
    oitem.sensorId = record['sensorId'];
    oitem.sensorNo = record['sensorNo'];
    oitem.sensorName = record['sensorName'];
    oitem.memo = record['memo']?record['memo']:"";
    oitem.isEdit = false;
    oList[i] = oitem;
  }
  app.sensorList = dList;
  app.orgSensorList = cloneSensorList(app.sensorList);
  console.log("sensorList.length:" + app.sensorList.length);

}

function setPaginator() {

  $("#pagination").empty();
  if (app.pageOption.dataTotal == 0) {
    return;
  }

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
         loadData(true);
      }
  });

  return obj;
}

function doToEditMode(sensorId) {

    var list = app.sensorList;

    for(i in list) {
      if (list[i].isEdit) {
        layer.msg("请先完成编辑！", {icon:1,time:1000});
        return;
      }
    }

    for(i in list) {
      if (list[i].sensorId == sensorId) {
        list[i].isEdit = true;
        break;
      }
    }

    app.sensorList = list;
}

function doCancelEditMode(sensorId) {

  var list = app.sensorList;
  for(i in list) {
    if (list[i].sensorId == sensorId) {

        list = cloneSensorList(app.orgSensorList);
        list[i].isEdit = false;
        break;
    }
  }

  app.sensorList = list;
}

function doToViewMode(sensorId) {

  var list = app.sensorList;
  for(i in list) {
      list[i].isEdit = false;
  }
  app.sensorList = list;
}

function doUpdateSensor(sensorId) {
  var sensor = getSelectedSensor(sensorId);

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"sensor.update",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId + "",
                  "sensorId":sensor.sensorId+"",
                  "sensorNo": sensor.sensorNo+"",
                  "sensorName": sensor.sensorName,
                  "memo": sensor.memo
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("传感器修改成功！", {icon:1,time:1000});
      sensor.isEdit = false;
      app.orgSensorList = cloneSensorList(app.sensorList);
      doToViewMode(sensor.sensorId);

    });
}

function doDeleteSensor(sensorId) {
  var sensor = getSelectedSensor(sensorId);

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"sensor.delete",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId + "",
                  "sensorId":sensor.sensorId+""
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("传感器删除成功！", {icon:1,time:1000});
      removeSensor(sensor.sensorId);
      app.orgSensorList = cloneSensorList(app.sensorList);
      doToViewMode(sensor.sensorId);
      setParentFrame();

    });

}

function doAddSensor() {
  if (!checkAuth()) {
    return;
  }

  var param = {"method":"sensor.add",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId + "",
                  "sensorNo": app.newItem.sensorNo+"",
                  "sensorName": app.newItem.sensorName,
                  "memo": app.newItem.memo
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("传感器添加成功！", {icon:1,time:1000});

      var list = app.sensorList;
      list.unshift({
        "sensorId":response.sensorId,
        "sensorNo":app.newItem.sensorNo,
        "sensorName":app.newItem.sensorName,
        "memo":app.newItem.memo,
        "isEdit":false
      });
      app.sensorList = list;
      app.orgSensorList = cloneSensorList(app.sensorList);
      app.newItem = makeNewItem();

      setParentFrame();
    });
}

function getSelectedSensor(sensorId) {

  var list = app.sensorList;

  for(i in list) {
    if (list[i].sensorId == sensorId) {
      return list[i];
    }
  }

  return  null;
}

function removeSensor(sensorId) {

    var list = app.sensorList;

    for(i in list) {
      if (list[i].sensorId == sensorId) {
        list.splice(i, 1);
      }
    }

    app.sensorList = list;
}

function cloneSensorList(srcList) {
  var destList = [];
  for (i in srcList) {
    var item = srcList[i];
    var dItem={};
    dItem.seq = item.seq;
    dItem.sensorId = item.sensorId;
    dItem.sensorNo = item.sensorNo;
    dItem.sensorName = item.sensorName;
    dItem.memo = item.memo;
    dItem.isEdit = item.isEdit;
    destList[i] = dItem;
  }
  return destList;
}

function makeNewItem() {
  var newItem = {
      sensorNo:"",
      sensorName:"",
      memo:""
  }

  return newItem;

}

// 设备列表刷新
function setParentFrame() {
  if (window.parent) {
    window.parent.loadData(true);
  }
}
