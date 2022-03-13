

var app = new Vue({
  el: '#app',
  data: {

    myDeviceInfo:[],// result
    selectedDeviceInfo:{}, //{"deviceId":"", "sensorList":[{...}],"datDecode":{...}}
    fromTimeDp:"",
    toTimeDp:"",
    deviceTotalPage:1,
    deviceCurrentPage:1,
    devicePageSize:10,
    titleList:[],
    dataList:[],
    deviceSn:'',

  },
  computed: {

  },
  methods:{

    selectDevice:function(deviceId){
      if (deviceId == app.selectedDeviceInfo.deviceId ) {
        return;
      }

      deviceSelected(deviceId);
    },

    query:function() {
      console.log(app.fromTimeDp);
      console.log(app.toTimeDp);
      loadData();
      
    },

    devicePrev:function(){
      if(app.deviceCurrentPage==1) {
        return;
      }

      app.deviceCurrentPage--;
      searchDevice();


    },
    deviceNext:function(){
      if(app.deviceCurrentPage==app.deviceTotalPage) {
        return;
      }

      app.deviceCurrentPage++;
      searchDevice();

    }
  }
});

window.onload = function(){
  init();
}

function init() {
  searchDevice();
  setDatetimepicker();
}

function loadData() {

  if (!checkAuth()) {
    return;
  }

  // alert(app.deviceId);
  console.log("app.deviceSn=" + app.deviceSn);

  if (app.fromTimeDp == "" || app.fromTime == "" ) {
    layer.msg("请输入期间！", {icon:2,time:1000});
    return;
  }

  app.dataList=[];

  var param = {"method":"monitor.msglog.query",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"deviceSn":app.deviceSn,
                      "fromTime":app.fromTimeDp,
                      "toTime":app.toTimeDp,
                      }
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var result = response.result;
      makeDataList(result);

      // layer.msg("查询成功！", {icon:1,time:1000});

    });
}

function setDatetimepicker() {
  var  fromDate = new Date(Date.parse(new Date()) - 1000*60*60);
  var  toDate = new Date();
  app.fromTimeDp = dateFormat(fromDate, "yyyy-MM-dd HH:mm");
  app.toTimeDp = dateFormat(toDate, "yyyy-MM-dd HH:mm");

  $('#datetimepicker_from').datetimepicker({
    mask:'9999/12/31 23:59',
    yearStart:2018,     //设置最小年份
    yearEnd:new Date().getFullYear(),        //设置最大年份
  });
  $('#datetimepicker_to').datetimepicker({
    mask:'9999/12/31 23:59',
    yearStart:2018,     //设置最小年份
    yearEnd:new Date().getFullYear(),        //设置最大年份

  });

  $.datetimepicker.setLocale('ch');
  
}

function searchDevice() {
    if (!checkAuth()) {
      return;
    }

    var param = {"method":"realtime.device.search",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{
                    "page":app.deviceCurrentPage + "",
                    "pageSize":app.devicePageSize + "",
                }
              };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        // 分页设置
        var total = response.total;
        app.deviceTotalPage = Math.ceil(total/app.devicePageSize);

        var result = response.result;

          var queryDeviceId = getQueryString("deviceId");
          if (queryDeviceId) {
            for(var i in result) {
              if (result[i].deviceId == queryDeviceId) {
                result = [result[i]];
                break;
              }
            }
          }

        for(i in result) {
          result[i].status = "";
        }

        fillSelectCondition(result);
        layer.msg("查询成功！"+result.length + " 条记录。", {icon:1,time:1000});

        loadData();
      });
  }

function fillSelectCondition(deviceInfo) {

    if (!deviceInfo) {
      return;
    }
    app.myDeviceInfo = deviceInfo;

    for(i in app.myDeviceInfo) {
      // app.myDeviceInfo[i].dev_deviceId = "dev_" + app.myDeviceInfo[i].deviceId;
      if (i==0) {
        console.log("deviceSelected:"+app.myDeviceInfo[i].deviceId);
        deviceSelected(app.myDeviceInfo[i].deviceId);
      }
    }

}

function deviceSelected(deviceId) {

  console.log("deviceSelected:"+deviceId);
  for(i in app.myDeviceInfo) {
    if (deviceId == app.myDeviceInfo[i].deviceId) {
      var selectedDeviceInfo = {};
      selectedDeviceInfo.deviceId = app.myDeviceInfo[i].deviceId;
      selectedDeviceInfo.deviceSn = app.myDeviceInfo[i].deviceSn;
      app.selectedDeviceInfo = selectedDeviceInfo;
      app.deviceSn = selectedDeviceInfo.deviceSn;
      loadData();
      return;
    }
  }
}

function dtpStartBlur() {
  
  var oldVal = app.fromTimeDp;
  app.fromTimeDp = $("#datetimepicker_from").val().replaceAll("/","-");
  if (app.fromTimeDp.indexOf("_")>-1 || app.fromTimeDp.indexOf("1899")>-1 ) {
    app.fromTimeDp = oldVal;
  } 

  var toDate = new Date(Date.parse(app.fromTimeDp) + 1000*60*60);
  app.toTimeDp = dateFormat(toDate, "yyyy-MM-dd HH:mm");

}

function dtpEndBlur() {
  
  var oldVal = app.toTimeDp;
  app.toTimeDp = $("#datetimepicker_to").val().replaceAll("/","-");
  if (app.toTimeDp.indexOf("_")>-1 || app.toTimeDp.indexOf("1899")>-1 ) {
    app.toTimeDp = oldVal;
  }

  var fromDate = new Date(Date.parse(app.toTimeDp) - 1000*60*60);
  app.fromTimeDp = dateFormat(fromDate, "yyyy-MM-dd HH:mm");
}

function makeDataList(result) {

      app.titleList = ["No.", "time", "direction", "content"];
      // app.dataList = [
      //   [1,'03-12 05:34:34.208', "DOWN", "68 10 80 22 01 10 01 82 01 01 78 00 22 03 12 13 34 34 51 16" ],
      //   [2,'03-12 05:34:34.165', "UP", "68 0c 40 22 01 10 01 82 22 03 12 13 34 18 48 16" ]
        
      // ]

      var dataList = [];
      for(var i=0;i<result.length;i++) {
        var item = result[i];
        dataList.push([i+1, item.time.substr(5), item.type=="TC/DAT/"?"UP":"DOWN", item.msg]);
      }

      app.dataList = dataList;

}

