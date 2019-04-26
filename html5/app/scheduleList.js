
var app = new Vue({
  el: '#app',
  data: {
    deviceId:getQueryString("deviceId"),
    scheduleList:[],
    orgScheduleList:[],
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
    toEditMode:function(scheduleId) {
      doToEditMode(scheduleId);
    },
    cancelEditMode:function(scheduleId) {
      doCancelEditMode(scheduleId);
    },
    toViewMode:function(scheduleId) {
      doToViewMode(scheduleId);
    },
    updateSchedule:function(scheduleId) {
      doUpdateSchedule(scheduleId);
    },
    deleteSchedule:function(scheduleId) {

      if(!confirm("是否删除计划任务？")) {
        return;
      }

      doDeleteSchedule(scheduleId);
    },
    toAddMode:function() {
      app.isAddMode = true;
    },
    cancelAddMode:function(){
      app.newItem = makeNewItem();
      app.isAddMode = false;
    },
    addSchedule:function(){
      doAddSchedule();
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

  var param = {"method":"setting.schedule.list",
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


      makeScheduleList(result);
      if (!silent) {
        layer.msg("计划任务查询成功！", {icon:1,time:1000});
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


function makeScheduleList(result, seq) {

  var dList =[];
  var oList = [];

  var seq = (app.pageOption.curPage-1)*app.pageOption.pageAmount;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.scheduleId = record['scheduleId'];
    item.cmdHex = record['cmdHex'];
    item.datPtn = record['datPtn']?record['datPtn']:"";
    item.interval = record['interval'];
    item.status = record['status'];
    item.isEdit = false;
    dList[i] = item;

    var oitem={};
    oitem.seq = seq;
    oitem.scheduleId = record['scheduleId'];
    oitem.cmdHex = record['cmdHex'];
    oitem.datPtn = record['datPtn']?record['datPtn']:"";
    oitem.interval = record['interval'];
    oitem.status = record['status'];
    oitem.isEdit = false;
    oList[i] = oitem;
  }
  app.scheduleList = dList;
  app.orgScheduleList = cloneScheduleList(app.scheduleList);
  console.log("scheduleList.length:" + app.scheduleList.length);

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

function doToEditMode(scheduleId) {

    var list = app.scheduleList;

    for(i in list) {
      if (list[i].isEdit) {
        layer.msg("请先完成编辑！", {icon:1,time:1000});
        return;
      }
    }

    for(i in list) {
      if (list[i].scheduleId == scheduleId) {
        list[i].isEdit = true;
        break;
      }
    }

    app.scheduleList = list;
}

function doCancelEditMode(scheduleId) {

  var list = app.scheduleList;
  for(i in list) {
    if (list[i].scheduleId == scheduleId) {

        list = cloneScheduleList(app.orgScheduleList);
        list[i].isEdit = false;
        break;
    }
  }

  app.scheduleList = list;
}

function doToViewMode(scheduleId) {

  var list = app.scheduleList;
  for(i in list) {
      list[i].isEdit = false;
  }
  app.scheduleList = list;
}

function doUpdateSchedule(scheduleId) {
  var schedule = getSelectedSchedule(scheduleId);

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"setting.schedule.update",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId + "",
                  "scheduleId":schedule.scheduleId+"",
                  "cmdHex": schedule.cmdHex,
                  "datPtn": schedule.datPtn,
                  "interval": schedule.interval + "",
                  "status": "1"
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:5000});
        return;
      }

      layer.msg("计划任务修改成功！", {icon:1,time:1000});
      schedule.isEdit = false;
      app.orgScheduleList = cloneScheduleList(app.scheduleList);
      doToViewMode(schedule.scheduleId);

    });
}

function doDeleteSchedule(scheduleId) {
  var schedule = getSelectedSchedule(scheduleId);

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"setting.schedule.delete",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId + "",
                  "scheduleId":schedule.scheduleId+""
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:5000});
        return;
      }

      layer.msg("计划任务删除成功！", {icon:1,time:1000});
      removeSchedule(schedule.scheduleId);
      app.orgScheduleList = cloneScheduleList(app.scheduleList);
      doToViewMode(schedule.scheduleId);
      // setParentFrame();

    });

}

function doAddSchedule() {
  if (!checkAuth()) {
    return;
  }

  var param = {"method":"setting.schedule.add",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "deviceId":app.deviceId + "",
                  "cmdHex": app.newItem.cmdHex+"",
                  "datPtn": app.newItem.datPtn,
                  "interval": app.newItem.interval + "",
                  "status": "1",
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:5000});
        return;
      }

      layer.msg("计划任务添加成功！", {icon:1,time:1000});

      var list = app.scheduleList;
      list.unshift({
        "scheduleId":response.scheduleId,
        "cmdHex":app.newItem.cmdHex,
        "datPtn":app.newItem.datPtn,
        "interval":app.newItem.interval,
        "status":app.newItem.status,
        "isEdit":false
      });
      app.scheduleList = list;
      app.orgScheduleList = cloneScheduleList(app.scheduleList);
      app.newItem = makeNewItem();

      // setParentFrame();
    });
}

function getSelectedSchedule(scheduleId) {

  var list = app.scheduleList;

  for(i in list) {
    if (list[i].scheduleId == scheduleId) {
      return list[i];
    }
  }

  return  null;
}

function removeSchedule(scheduleId) {

    var list = app.scheduleList;

    for(i in list) {
      if (list[i].scheduleId == scheduleId) {
        list.splice(i, 1);
      }
    }

    app.scheduleList = list;
}

function cloneScheduleList(srcList) {
  var destList = [];
  for (i in srcList) {
    var item = srcList[i];
    var dItem={};
    dItem.seq = item.seq;
    dItem.scheduleId = item.scheduleId;
    dItem.cmdHex = item.cmdHex;
    dItem.datPtn = item.datPtn;
    dItem.interval = item.interval;
    dItem.status = item.status;
    dItem.isEdit = item.isEdit;
    destList[i] = dItem;
  }
  return destList;
}

function makeNewItem() {
  var newItem = {
      cmdHex:"",
      datPtn:"",
      interval:"",
      status:"1"
  }

  return newItem;

}

// 设备列表刷新
// function setParentFrame() {
//   if (window.parent) {
//     window.parent.loadData(true);
//   }
// }
