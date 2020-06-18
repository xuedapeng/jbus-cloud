

var MAX_POS = 365;
var DAYS_1H = 0.04167;
var DAYS_24H = 1;
var DAYS_3D = 3;
var DAYS_7D = 7;
var DAYS_30D = 30;

var app = new Vue({
  el: '#app',
  data: {

    myDeviceInfo:[],// result
    mySensorInfo:[],
    selectedDeviceInfo:{}, //{"deviceId":"", "sensorList":[{...}],"datDecode":{...}}
    selectedSensorInfo:{},  // {"sensorId":"", "cmdEncodeList":[{...}]}
    graphCount:1,
    fromPos:MAX_POS-30,
    toPos:MAX_POS,
    iframeSrc:'',
    shortcutDays:DAYS_30D,
    queryMode:'graph', // graph, detail
    dateMode:'slide', // pick, slide
    fromTimeDp:"",
    toTimeDp:"",

  },
  computed: {
    fromTime: function () {

      if (this.dateMode == "pick") {
        return this.fromTimeDp!=""?(this.fromTimeDp+":00"):"";
      }

      var  fromDate = new Date(Date.parse(new Date()) - 1000*60*60*24*(MAX_POS-this.fromPos));
      return dateFormat(fromDate, "yyyy-MM-dd HH:mm:ss");

    },
    fromDate:function(){
      return this.fromTime.substring(0,10);
    },

    toTime: function () {

      if (this.dateMode == "pick") {
        return this.toTimeDp!=""?(this.toTimeDp+":00"):"";
      }
      var  toDate = new Date(Date.parse(new Date()) - 1000*60*60*24*(MAX_POS-this.toPos));
      return dateFormat(toDate, "yyyy-MM-dd HH:mm:ss");
    },
    toDate:function(){
      return this.toTime.substring(0,10);
    },
    iframeSrcCalc:function() {
      console.log("frome-to:" +  this.fromTime + " - " + this.toTime);
      return Object.keys(this.selectedSensorInfo).length==0?'': (this.htmlfile + '?showTitle=no&deviceId='+this.selectedDeviceInfo.deviceId+'&sensorNo='+this.selectedSensorInfo.sensorNo + '&fromTime=' + this.fromTime + '&toTime=' + this.toTime) ;
    },
    htmlfile:function() {
      if (app.queryMode=="graph") {
        return "hydrograph.html";
      } else {
        return "historyDetail.html";
      }
    }
  },
  methods:{

    selectDevice:function(deviceId){
      if (deviceId == app.selectedDeviceInfo.deviceId ) {
        return;
      }
      app.mySensorInfo = [];

      deviceSelected(deviceId);
    },
    selectSensor:function(sensorId){

      if (sensorId == app.selectedSensorInfo.sensorId) {
        return;
      }

      sensorSelected(sensorId);
    },
    refreshGraph:function() {
      console.log(app.fromTimeDp);
      console.log(app.toTimeDp);

      var src = app.iframeSrcCalc;
      if (src != '') {
        app.iframeSrc = src + "&ts="+new Date().getTime();
      }
    },

    resetPos:function(days) {
        if (days<=0) {
          return;
        }
        app.shortcutDays = days;
        app.toPos += 1 ;
        app.toPos = MAX_POS;
        app.fromPos += 1 ;
        app.fromPos = MAX_POS - days;

        $( "#slider" ).slider("values",0, app.fromPos);
        $( "#slider" ).slider("values",1, app.toPos);

        app.resetDatePicker();

        app.refreshGraph();
    },

    resetDatePicker:function(){
      var days = app.shortcutDays;
      if (days<=0) {
        return;
      }

      if (days <=1) {
        var  fromDate = new Date(Date.parse(new Date()) - 1000*60*60*24*days);
        var  toDate = new Date();
        app.fromTimeDp = dateFormat(fromDate, "yyyy-MM-dd HH:mm");
        app.toTimeDp = dateFormat(toDate, "yyyy-MM-dd HH:mm");

      } else {
        var  fromDate = new Date(Date.parse(new Date()) - 1000*60*60*24*(days-1));
        var  toDate = new Date(Date.parse(new Date()) + 1000*60*60*24*1);
        app.fromTimeDp = dateFormat(fromDate, "yyyy-MM-dd") + " 00:00";
        app.toTimeDp = dateFormat(toDate, "yyyy-MM-dd") + " 00:00";
      }
    },


    setDateMode:function(dateMode) {
      if (dateMode=='s') {
        app.dateMode = 'slide';
      } else if (dateMode=='c') {
        app.dateMode = 'pick';
      } 
      app.refreshGraph();
    },
    setQueryMode:function(queryMode) {
      if (queryMode=='g') {
        app.queryMode = 'graph';
        app.setDateMode("s");
      } else if (queryMode=='d') {
        app.queryMode = 'detail';
        app.setDateMode("c");
      } 
      app.refreshGraph();
    }
  }
});

window.onload = function(){
  init();
}

function init() {
  setSlider();
  app.resetPos(30);
  searchDevice();
  setDatetimepicker();
}

function setDatetimepicker() {

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
                "auth":[getStorage("appId"), getStorage("appToken")]};

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

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
        // layer.msg("查询成功！", {icon:1,time:1000});

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
      app.selectedDeviceInfo.deviceId = app.myDeviceInfo[i].deviceId;
      app.selectedDeviceInfo.deviceSn = app.myDeviceInfo[i].deviceSn;
      app.selectedDeviceInfo.sensorList = app.myDeviceInfo[i].sensorList;

      app.mySensorInfo = app.selectedDeviceInfo.sensorList;
      console.log("sensorList.length:" + app.selectedDeviceInfo.sensorList.length);

      if (app.selectedDeviceInfo.sensorList.length > 0) {
        app.selectedDeviceInfo.sensorList[0] = app.selectedDeviceInfo.sensorList[0];
        console.log("sensorSelected:"+app.selectedDeviceInfo.sensorList[0].sensorId);
        sensorSelected(app.selectedDeviceInfo.sensorList[0].sensorId);
      } else {
        app.selectedSensorInfo = {};
      }

      return;
    }
  }
}

function sensorSelected(sensorId) {
  for(i in app.selectedDeviceInfo.sensorList) {

      app.selectedSensorInfo= {};
      if (sensorId == app.selectedDeviceInfo.sensorList[i].sensorId) {
          app.selectedSensorInfo.sensorId = sensorId;
          app.selectedSensorInfo.sensorNo = app.selectedDeviceInfo.sensorList[i].sensorNo;
          app.graphCount = 1;
          app.iframeSrc = app.iframeSrcCalc;
          console.log("sensorSelected:"+app.selectedSensorInfo.sensorId);
          return;
      }
  }
}

function setGraphCount(count) {
  app.graphCount = count;
}

function setSlider() {
  $( "#slider" ).slider({
    range: true,
    values: [ app.fromPos, app.toPos ],
    min:0,
    max:365,
    slide: function( event, ui ) {
      app.fromPos = ui.values[0];
      app.toPos = ui.values[1];
      app.shortcutDays = -1;
    }
  });
}

function dtpStartBlur() {
  
  app.fromTimeDp = $("#datetimepicker_from").val().replaceAll("/","-");
  if (app.fromTimeDp.indexOf("_") || app.fromTimeDp.indexOf("1899") ) {
    app.fromTimeDp = "";
  }

}

function dtpEndBlur() {
  
  app.toTimeDp = $("#datetimepicker_to").val().replaceAll("/","-");
  if (app.toTimeDp.indexOf("_") || app.toTimeDp.indexOf("1899") ) {
    app.toTimeDp = "";
  }
}

