
var app = new Vue({
  el: '#app',
  data: {
    sensorId:getQueryString("sensorId"),
    cmdList:[],
    orgCmdList:[],
    newItem:makeNewItem(),
    isAddMode:false,
    pageOption : {
          "pageTotal":1, //必填,总页数
          "pageAmount":2, //每页多少条
          "dataTotal":1, //总共多少条数据
          "curPage":1, //初始页码,不填默认为1
          "pageSize": 5, //分页个数,不填默认为5
        }
  },
  methods:{
    toEditMode:function(cmdId) {
      doToEditMode(cmdId);
    },
    cancelEditMode:function(cmdId) {
      doCancelEditMode(cmdId);
    },
    toViewMode:function(cmdId) {
      doToViewMode(cmdId);
    },
    updateCmd:function(cmdId) {
      doUpdateCmd(cmdId);
    },
    deleteCmd:function(cmdId) {

      if(!confirm("是否删除指令？")) {
        return;
      }

      doDeleteCmd(cmdId);
    },
    toAddMode:function() {
      app.isAddMode = true;
    },
    cancelAddMode:function(){
      app.newItem = makeNewItem();
      app.isAddMode = false;
    },
    addCmd:function(){
      doAddCmd();
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

  var param = {"method":"codec.cmd.list",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "sensorId":app.sensorId,
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


      makeCmdList(result);
      if (!silent) {
        layer.msg("指令查询成功！", {icon:1,time:1000});
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


function makeCmdList(result, seq) {

  var dList =[];
  var oList = [];

  var seq = (app.pageOption.curPage-1)*app.pageOption.pageAmount;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.cmdId = record['cmdId'];
    item.cmdNo = record['cmdNo'];
    item.cmdName = record['cmdName'];
    item.scriptText = record['scriptText'];
    item.isEdit = false;
    dList[i] = item;

    var oitem={};
    oitem.seq = seq;
    oitem.cmdId = record['cmdId'];
    oitem.cmdNo = record['cmdNo'];
    oitem.cmdName = record['cmdName'];
    oitem.scriptText = record['scriptText'];
    oitem.isEdit = false;
    oList[i] = oitem;
  }
  app.cmdList = dList;
  app.orgCmdList = cloneCmdList(app.cmdList);
  console.log("cmdList.length:" + app.cmdList.length);

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

function doToEditMode(cmdId) {

    var list = app.cmdList;

    for(i in list) {
      if (list[i].isEdit) {
        layer.msg("请先完成编辑！", {icon:1,time:1000});
        return;
      }
    }

    for(i in list) {
      if (list[i].cmdId == cmdId) {
        list[i].isEdit = true;
        break;
      }
    }

    app.cmdList = list;
}

function doCancelEditMode(cmdId) {

  var list = app.cmdList;
  for(i in list) {
    if (list[i].cmdId == cmdId) {

        list = cloneCmdList(app.orgCmdList);
        list[i].isEdit = false;
        break;
    }
  }

  app.cmdList = list;
}

function doToViewMode(cmdId) {

  var list = app.cmdList;
  for(i in list) {
      list[i].isEdit = false;
  }
  app.cmdList = list;
}

function doUpdateCmd(cmdId) {
  var cmd = getSelectedCmd(cmdId);

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"codec.cmd.update",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "sensorId":app.sensorId + "",
                  "cmdId":cmd.cmdId+"",
                  "cmdNo": cmd.cmdNo+"",
                  "cmdName": cmd.cmdName,
                  "scriptText": cmd.scriptText
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("指令修改成功！", {icon:1,time:1000});
      cmd.isEdit = false;
      app.orgCmdList = cloneCmdList(app.cmdList);
      doToViewMode(cmd.cmdId);

    });
}

function doDeleteCmd(cmdId) {
  var cmd = getSelectedCmd(cmdId);

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"codec.cmd.delete",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "sensorId":app.sensorId + "",
                  "cmdId":cmd.cmdId+""
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("指令删除成功！", {icon:1,time:1000});
      removeCmd(cmd.cmdId);
      app.orgCmdList = cloneCmdList(app.cmdList);
      doToViewMode(cmd.cmdId);
      setParentFrame();

    });

}

function doAddCmd() {
  if (!checkAuth()) {
    return;
  }

  var param = {"method":"codec.cmd.add",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                  "sensorId":app.sensorId + "",
                  "cmdNo": app.newItem.cmdNo+"",
                  "cmdName": app.newItem.cmdName,
                  "scriptText": app.newItem.scriptText
                }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("指令添加成功！", {icon:1,time:1000});

      var list = app.cmdList;
      list.unshift({
        "cmdId":response.cmdId,
        "cmdNo":app.newItem.cmdNo,
        "cmdName":app.newItem.cmdName,
        "scriptText":app.newItem.scriptText,
        "isEdit":false
      });
      app.cmdList = list;
      app.orgCmdList = cloneCmdList(app.cmdList);
      app.newItem = makeNewItem();

      setParentFrame();
    });
}

function getSelectedCmd(cmdId) {

  var list = app.cmdList;

  for(i in list) {
    if (list[i].cmdId == cmdId) {
      return list[i];
    }
  }

  return  null;
}

function removeCmd(cmdId) {

    var list = app.cmdList;

    for(i in list) {
      if (list[i].cmdId == cmdId) {
        list.splice(i, 1);
      }
    }

    app.cmdList = list;
}

function cloneCmdList(srcList) {
  var destList = [];
  for (i in srcList) {
    var item = srcList[i];
    var dItem={};
    dItem.seq = item.seq;
    dItem.cmdId = item.cmdId;
    dItem.cmdNo = item.cmdNo;
    dItem.cmdName = item.cmdName;
    dItem.scriptText = item.scriptText;
    dItem.isEdit = item.isEdit;
    destList[i] = dItem;
  }
  return destList;
}

function makeNewItem() {
  var newItem = {
      cmdNo:"",
      cmdName:"",
      scriptText:""
  }

  return newItem;

}

// 设备列表刷新
function setParentFrame() {
  // if (window.parent) {
  //   window.parent.loadData(true);
  // }
}
