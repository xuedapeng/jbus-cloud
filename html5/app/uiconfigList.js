

var app = new Vue({
  el: '#app',
  data: {
    projectList:[],
    projectListTemp:[],
    fieldValues:{},
    deviceOnlineStatus:{},
    lastLoadTime:"",
    graphSrc:"",
    projectId:"0",
    itemCount:0,
    doneCount:0,
    elapsedtime:0,
    valueMap:{},
    styleMap:{}

  },
  methods:{
    query:function(){
      loadData();
    },
    sendCmd:function(sn, cmd) {
      sendCommand(sn, cmd);
    },
    showGraph:function(deviceId, sno, field) {
      this.graphSrc = "hydrograph.html?field=" + field + "&deviceId=" + deviceId + "&sensorNo=" + sno + "&showTitle=yes&height=200" + "&ts="+new Date().getTime();
      $("#modal-graph").modal("show");
    }
  },
  computed:{
    lastLoadTimeShort(){
      return this.lastLoadTime.substr(11);
    }
  }
});


loadData();
function loadData() {

  if (!checkAuth()) {
    return;
  }

  app.doneCount = 0;
  app.elapsedtime = 0;
  var projectId = getQueryString("projectId");
  if (projectId) {
    app.projectId = projectId;
  }
  // app.projectList=[];

  var param = {"method":"uiconfig.projects.get",
              "data":{"projectId":app.projectId+""},
              "auth":[getStorage("appId"), getStorage("appToken")]
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var result = response.result;
      if (result.length == 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }
      makeProjectList(result);

      layer.msg("正在加载，请稍候...",{icon:1,time:5000});

    });
}


function makeProjectList(result) {
  app.projectListTemp = result;
  // app.projectList = dummyData().projectList;
  // app.fieldValues = dummyData().fieldValues;
  updateOnlineStatus();
  // updateRealtimeData(); // move to  finish of updateOnlineStatus
}

function updateOnlineStatus() {

  for(var i in app.projectListTemp) {

    var pj = app.projectListTemp[i];
    for(var key in pj.deviceSnList) {
      app.deviceOnlineStatus[key] = "off";
    }
  }

  getDeviceStatus();
}

function getDeviceStatus() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"realtime.device.online.query",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data": { "deviceIds": Object.keys(app.deviceOnlineStatus)}
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var result = response.result;

      for(var key in app.deviceOnlineStatus) {
        app.deviceOnlineStatus[key] = result[key];
      }

      updateRealtimeData();
    });
}

function sendCommand(sn, cmd) {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"realtime.device.sendcmd",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data": { 
                "deviceSn": sn,
                "cmd":cmd
              }
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      layer.msg("指令发送成功<br/>"+sn+","+cmd,{icon:1,time:3000});

    });
}

function updateRealtimeData() {

  app.fieldValues = {};
  app.valueMap = {};
  app.styleMap = {};
  var page = app;

  console.log("updateRealtimeData:projectId="+page.projectId);
  var param = {
    "method": "uiconfig.data.get",
    "data": { "projectId": page.projectId },
    "auth": [getStorage("appId"), getStorage("appToken")]
  };

  ajaxPost(
    G_RPC_URL,
    param,
    function(response) {
      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      page.valueMap = response.resultMap;
      page.styleMap = response.styleMap;

      for (var i in page.projectListTemp) {

        var pj = page.projectListTemp[i];
        for (var j in pj.cover) {
          var fd = pj.cover[j];
          getRealtimeData(pj.seq, fd.seq, fd.deviceSn, fd.sensorNo, fd.field);
        }
      }
      updateAllData();
    }
  );

  // var count = 0;
  // for(var i in app.projectListTemp) {

  //   var pj = app.projectListTemp[i];
  //   for (var j in pj.cover) {
  //     var fd = pj.cover[j];
  //     // if (!fd.data) {
  //     //   fd.data = {"val":""};
  //     // }
  //     getRealtimeData(pj.seq, fd.seq, pj.deviceSnList[fd.deviceSn].id, fd.sensorNo, fd.field);
  //     count++;
  //     // alert(j);
  //     // sleep(200);
  //   }
  // }
  // app.itemCount = count;
  // // alert(count);
  // setTimeout("isDone()", 2000);
}

// function isDone() {
//   app.elapsedtime++;
//   if (app.itemCount == app.doneCount || app.elapsedtime>60) {
//     // alert(app.itemCount + "," + app.doneCount);
//     updateAllData();
//   } else {

//     layer.msg("正在加载，请稍候...(" + app.doneCount + "/" + app.itemCount +") " + app.elapsedtime*2 + "s",{icon:1,time:2000});
//     setTimeout("isDone()", 2000);
//   }
// }

function updateAllData() {
  
  for(var i in app.projectListTemp) {

    var pj = app.projectListTemp[i];

    for(var k in pj.deviceSnList) {
      pj.deviceSnList[k].status = app.deviceOnlineStatus[k];
    }

    for (var j in pj.cover) {
      var fd = pj.cover[j];
      
      // console.log(pj.seq + "," + fd.seq);
      if (app.fieldValues[pj.seq]&&app.fieldValues[pj.seq][fd.seq]){
        fd.data = app.fieldValues[pj.seq][fd.seq];
        
          if (fd.valuePtn && fd.valuePtn[fd.data.val + ""]) {
            fd.data.val = fd.valuePtn[fd.data.val + ""];
          }
            
        // console.log(fd.data.val);
      }
    }
  }
  // var t = app.projectList
  app.projectList = app.projectListTemp;

  layer.msg("加载数据成功！",{icon:1,time:500});
  app.lastLoadTime = dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
  console.log(JSON.stringify(app.projectList));

}

function getRealtimeData(p_seq, f_seq, deviceSn, sensorNo, field) {

  var fvs = app.fieldValues;
  var dv = getRealtimeValDisplay(p_seq, f_seq, deviceSn, sensorNo, field);
  if (dv) {
    if (!fvs[p_seq]) {
      fvs[p_seq] = {};
    }
    fvs[p_seq][f_seq] = dv;
  }

  app.fieldValues = fvs;

  // requestRealtimeVal(p_seq, f_seq, deviceId, sensorNo, field);
}

// function requestRealtimeVal(p_seq, f_seq, deviceId, sensorNo, field) {

//   if (!checkAuth()) {
//     return;
//   }

//   var param = {"method":"history.data.query",
//               "auth":[getStorage("appId"), getStorage("appToken")],
//               "data": { "deviceId": deviceId + "", 
//                         "sensorNo": sensorNo + "", 
//                         "pageSize": "1" 
//                       }
//             };

//   ajaxPost(G_RPC_URL, param,
//     function(response){

//       app.doneCount++;

//       if (response.status < 0) {
//         layer.msg(response.msg,{icon:2,time:2000});
//         return;
//       }

//       var result = response.result;

//       var fvs = app.fieldValues;
//       var dv = getRealtimeValDisplay(response,p_seq, f_seq, deviceId, sensorNo, field);
//       if (dv) {
//         if (!fvs[p_seq]) {
//           fvs[p_seq] = {};
//         }
//         fvs[p_seq][f_seq] = dv;
//         // console.log("p_seq="+p_seq);
//         // console.log("f_seq="+f_seq);
//         // console.log("fvs="+fvs[p_seq][f_seq].val);
//       }

//       app.fieldValues = fvs;

//     });
// }

function getRealtimeValDisplay(p_seq, f_seq, deviceSn, sensorNo, field) {
  var display = [];

  console.log("styleMap="+JSON.stringify(app.styleMap));
  console.log("deviceSn,sensorNo,field:"+deviceSn  +"," + sensorNo + "," + field);
  var fieldStyle = app.styleMap[deviceSn][sensorNo][field];
  var val = app.valueMap[deviceSn][sensorNo][field];
  var time =  app.valueMap[deviceSn][sensorNo]["time"];
  
  if (!val) {
    return "";
  }

  if(fieldStyle) {
    
    var item = {
      "name": fieldStyle.display,
      "val": val,
      "unit": fieldStyle.unit,
      "color": "black",
      "agoTime": getAgoTime(time)
    };

    // console.log("val="+item.val);

    var range = fieldStyle.range;
    if (range) {
      var minmax = range.split(",");
      if (val < minmax[0]) {
        item.color = "blue";
      } else if (val > minmax[1]) {
        item.color = "red";
      }
    }
    
    return item;
  }
  return null;
}

function getAgoTime(time) {
  // time = "2019-06-04 10:52";
  time = time.replace(/[-]/g,"/");
  var now = new Date();
  var orgTime = Date.parse(time);
  var intv = (now - orgTime) / 1000;
  // return (time);
  if (intv > 24*60*60) {
    return "(" + Math.floor(intv/(24*60*60)) + "天前)";
  }
  if (intv > 1*60*60) {
    return "(" + Math.floor(intv/(60*60)) + "小时前)";
  }

  if (intv > 1 * 60) {
    return "(" + Math.floor(intv / (60)) + "分钟前)";
  }

  return "";
}

function dummyData() {
  var d = {
    projectList:[{"cover":[{"seq":"1","deviceSn":"3DF5C433","sensorNo":"11","field":"sw","name":"电动阀(11)","valuePtn":{"0":"关","1":"开"},"data":{"name":"阀门状态","val":"开","unit":" ","color":"black","agoTime":"(2天前)"}},{"seq":"2","deviceSn":"3DF5C433","sensorNo":"21","field":"sw","name":"电动阀(21)","valuePtn":{"0":"关","1":"开"},"data":{"name":"阀门状态","val":"开","unit":" ","color":"black","agoTime":"(2天前)"}},{"seq":"3","deviceSn":"7835DA60","sensorNo":"32","field":"h","name":"水位(32)","valuePtn":{},"data":{"name":"水层","val":"10","unit":"毫米","color":"black","agoTime":"(2天前)"}},{"seq":"4","deviceSn":"7835DA60","sensorNo":"22","field":"h","name":"水位(22)","valuePtn":{},"data":{"name":"水层","val":"391","unit":"毫米","color":"black","agoTime":"(2天前)"}}],"deviceSnList":{"3DF5C433":{"name":"平湖水稻-电动阀","id":"68"},"7835DA60":{"name":"平湖水稻-水位计","id":"71"}},"title":"云南滴灌B区","projectId":"2","seq":"1"},{"cover":[{"deviceSn":"3DF5C433","sensorNo":"11","field":"sw","name":"电动阀(11)","valuePtn":{"0":"关","1":"开"},"seq":"1","data":{"name":"阀门状态","val":"开","unit":" ","color":"black","agoTime":"(2天前)"}},{"deviceSn":"3DF5C433","sensorNo":"21","field":"sw","name":"电动阀(21)","valuePtn":{"0":"关","1":"开"},"seq":"2","data":{"name":"阀门状态","val":"开","unit":" ","color":"black","agoTime":"(2天前)"}},{"deviceSn":"7835DA60","sensorNo":"32","field":"h","name":"水位(32)","valuePtn":{},"seq":"3","data":{"name":"水层","val":"10","unit":"毫米","color":"black","agoTime":"(2天前)"}},{"deviceSn":"7835DA60","sensorNo":"22","field":"h","name":"水位(22)","valuePtn":{},"seq":"4","data":{"name":"水层","val":"391","unit":"毫米","color":"black","agoTime":"(2天前)"}}],"deviceSnList":{"3DF5C433":{"name":"平湖水稻-电动阀","id":"68"},"7835DA60":{"name":"平湖水稻-水位计","id":"71"}},"title":"云南滴灌A区","projectId":"1","seq":"2"}],
    // projectList:[
    //   {"seq":"1", "projectId":"1", "title":"云南滴灌A区", "deviceSnList":{"3DF5C433":{"id":"68","name":"平湖水稻-电动阀","status":"on"}, "7835DA60":{"id":"71","name":"平湖水稻-水位计","status":"off"}},"cover":[
    //     {"seq":"1","deviceSn":"3DF5C433","sensorNo":"11","field":"sw","name":"电动阀(11)","valuePtn":{"1":"开","0":"关"}},
    //     {"seq":"2","deviceSn":"3DF5C433","sensorNo":"21","field":"sw","name":"电动阀(21)","valuePtn":{"1":"开","0":"关"},"data":{"val":""}},
    //     {"seq":"3","deviceSn":"7835DA60","sensorNo":"32","field":"h","name":"水位(32)","valuePtn":{},"data":{"val":""}},
    //     {"seq":"4","deviceSn":"7835DA60","sensorNo":"22","field":"h","name":"水位(22)","valuePtn":{},"data":{"val":""}} 
    //   ]},
    //   {"seq":"2", "projectId":"2", "title":"云南滴灌B区", "deviceSnList":{"3DF5C433":{"id":"68","name":"平湖水稻-电动阀","status":"on"}, "7835DA60":{"id":"71","name":"平湖水稻-水位计","status":"off"}},"cover":[
    //     {"seq":"1","deviceSn":"3DF5C433","sensorNo":"11","field":"sw","name":"电动阀(11)","valuePtn":{"1":"开","0":"关"},"data":{"val":""}},
    //     {"seq":"2","deviceSn":"3DF5C433","sensorNo":"21","field":"sw","name":"电动阀(21)","valuePtn":{"1":"开","0":"关"},"data":{"val":""}},
    //     {"seq":"3","deviceSn":"7835DA60","sensorNo":"32","field":"h","name":"水位(32)","valuePtn":{},"data":{"val":""}},
    //     {"seq":"4","deviceSn":"7835DA60","sensorNo":"22","field":"h","name":"水位(22)","valuePtn":{},"data":{"val":""}} 
    //   ]},
    // ],

    fieldValues:{
      "1":{"1":"开","2":"关","3":"100","4":"200"},
      "2":{"1":"开","2":"关","3":"102","4":"202"}
    }
  }
  return d;

};
