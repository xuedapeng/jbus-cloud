

var app = new Vue({
  el: '#app',
  data: {
    titleList:[],
    dataList:[],
    deviceId:getQueryString("deviceId"),
    sensorNo:getQueryString("sensorNo"),
    fromTime:getQueryString("fromTime"),
    toTime:getQueryString("toTime"),
    currentFromTime:'',
    currentToTime:'',
    direction:-1,
    pageSize:50,
    deviceSn:'',
    deviceName:'',
    sensorName:'',
    fieldStyle:{},
    showTitle:getQueryString("showTitle")=='no'?'none':'block',

  },
  methods:{
    query:function(){
      loadData();
    },
  }
});


loadData();
function loadData() {

  if (!checkAuth()) {
    return;
  }

  // alert(app.deviceId);
  console.log("app.deviceId=" + app.deviceId );

  // if (!app.toTime) {
  //   app.toTime = dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
  // }

  if (!app.fromTime || app.fromTime == "" ) {
    // 24小时前
    var  fromDate = new Date(Date.parse(new Date()) - 1000*60*60*24);
    app.fromTime = dateFormat(fromDate, "yyyy-MM-dd HH:mm:ss");
  }

  app.dataList=[];

  var param = {"method":"history.data.query",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"deviceId":app.deviceId,
                      "sensorNo":app.sensorNo,
                      "direction":app.direction+"",
                      "pageSize":app.pageSize+"",
                      }
            };

  if (app.direction==-1) {
    if (app.toTime && app.toTime != '') {
      param.data.fromTime = app.toTime;
    }
  } else {
    if (app.fromTime && app.fromTime != '') {
      param.data.fromTime = app.fromTime;
    } 
  }

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      app.deviceSn = response.deviceSn;
      app.deviceName = response.deviceName;
      app.sensorName = response.sensorName;
      app.fieldStyle = response.fieldStyle;

      var result = response.result;
      makeDataList(result);

      // layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function makeDataList(result) {

  var tList =[];
  var fList =[];
  var dList =[];
  tList = ["No.","time"];
  for(k in app.fieldStyle) {
    tList.push(app.fieldStyle[k].display+'('+app.fieldStyle[k].unit+')');
    fList.push(k);
  }

  for (var i=0;i<result["time"].length;i++) {
    var item = [];
    item[0] = i+1;
    item[1] = result["time"][i];
    for(var j=0; j<fList.length; j++) {
      item[j+2] = result[fList[j]][i];
    }

    dList.push(item);

  }

  app.titleList = tList;
  app.dataList = dList;
  console.log(app.dataList.length);

  // setTimeout("makeGraph()", 1000);
  makeGraph();
}

function makeGraph() {
  
  if (app.dataList.length==0) {
    // layer.msg("没有找到符合条件的数据！", {icon:1,time:5000});
  }

  setParentFrame();
}

function setParentFrame() {
  if (window.parent) {
    window.parent.setGraphCount(5);
  }
}