

var app = new Vue({
  el: '#app',
  data: {
    cmd_hint:"请输入命令参数...",
    cmdParam:"",
    deviceCount:"",
    sensorCount:"",
    cmdCount:"",
    mqttClient:null,
    mqttClientStatus:null,
    mqttStatusTopics:[],
    mqttTopic:"",
    myDeviceInfo:[],// result
    mySensorInfo:[],
    myCmdEncodeInfo:[],
    myParamSchema:"",
    selectedDeviceInfo:{}, //{"deviceId":"", "sensorList":[{...}],"datDecode":{...}}
    selectedSensorInfo:{},  // {"sensorId":"", "cmdEncodeList":[{...}]}
    selectedCmdEncodeInfo:{},  // {"cmdNo":"", ", "cmdName":"",...}
    messageList:[],
    viewsource:false,
    hexSend:false

  },
  methods:{
    connect:function(){
      connectMqtt();
    },
    disconnect:function(){

      if (app.mqttClient) {
        app.mqttClient.disconnect();
      }
    },
    clear:function(){
      app.cmdParam="";
    },
    send:function(){

      if (app.mqttClient) {
        sendMessage();
      } else {

        layer.msg("连接已断开！", {icon:1,time:1000});
      }
    },
    cmdParamChanged:function(){

    },
    selectDevice:function(deviceId){
      if (deviceId == app.selectedDeviceInfo.deviceId ) {
        return;
      }
      deviceSelected(deviceId);
      reSubscribe();
      getDeviceStatus(); // 选择设备时，更新所有设备的状态
      app.messageList.length = 0;
    },
    selectSensor:function(sensorId){
      sensorSelected(sensorId);

    },
    selectCmdEncode:function(cmdNo){
      cmdEncodeSelected(cmdNo);

    }

  }
});

init();
function init() {
  searchDevice();
}

function subscribeDeviceStatus() {
    if (app.mqttClientStatus) {
      app.mqttClientStatus.disconnect();
    }

    var clientId = guid();
    console.log("guid_status="+clientId);
     var client= new Paho.MQTT.Client(G_MQTT_HOST, Number(G_MQTT_PORT), clientId);//建立客户端实例

     client.onConnectionLost = function() {
       console.log("mqttClientStatus connection lost.");
     };//注册连接断开处理事件

     client.onMessageArrived = function(message){
       var deviceInfo = app.myDeviceInfo;
       console.log("receive topic:"+ message.destinationName);
       console.log("receive data:"+ message.payloadString);
       data = JSON.parse(message.payloadString);
       for(i in deviceInfo) {
         if (data.deviceSn == deviceInfo[i].deviceSn) {
           if (data.onlineCount > 0) {
             deviceInfo[i].status = "on";
           } else {
             deviceInfo[i].status = "off";
           }
         }
       }

       app.myDeviceInfo = deviceInfo;

     };//注册消息接收处理事件

     client.connect({
       onSuccess:function(){
         console.log("mqttClientStatus connected.");
         // subscribe
         app.mqttStatusTopics.length = 0;
         if (app.myDeviceInfo.length > 0) {
           for(i in app.myDeviceInfo) {
             app.mqttStatusTopics[i] = "TC/STS/" + app.myDeviceInfo[i].deviceSn;
           }
         }
         if (app.mqttStatusTopics.length > 0) {
           for (i in app.mqttStatusTopics) {
             client.subscribe(app.mqttStatusTopics[i]);
             console.log("subscribe:" + app.mqttStatusTopics[i]);
           }
         }
       },
       userName:G_MQTT_USER,
       password:G_MQTT_USER
     });//连接服务器并注册连接成功处理事件

     app.mqttClientStatus = client;
}

function connectMqtt() {

  if (!checkAuth()) {
    return;
  }

  if (!app.selectedDeviceInfo || !app.selectedDeviceInfo.deviceSn) {
    lay.msg("请选择设备！", {icon:1,time:1000});
    return;
  }

  if (app.mqttClient) {
    app.mqttClient.disconnect();
    console.log("app.mqttClient.disconnect()");
  }
  var clientId = guid();
  console.log("guid="+clientId);
   var client= new Paho.MQTT.Client(G_MQTT_HOST, Number(G_MQTT_PORT), clientId);//建立客户端实例
   client.onConnectionLost = onConnectionLost;//注册连接断开处理事件
   client.onMessageArrived = onMessageArrived;//注册消息接收处理事件

   client.connect({
     onSuccess:onConnect,
     userName:G_MQTT_USER,
     password:G_MQTT_USER
   });//连接服务器并注册连接成功处理事件

   app.mqttClient = client;
}

function onConnect() {
  console.log("onConnected");
  reSubscribe();
  // layer.msg("已连接！", {icon:1,time:1000});
}



function reSubscribe() {

  if(!app.mqttClient) {
    return;
  }

  if (app.mqttTopic.length > 0) {
    app.mqttClient.unsubscribe(app.mqttTopic);
    console.log("unsubscribe:"+ app.mqttTopic);
  }
  app.mqttTopic = "TC/DAT/"+app.selectedDeviceInfo.deviceSn;

  app.mqttClient.subscribe(app.mqttTopic);//订阅主题
  console.log("subscribe:"+ app.mqttTopic);
}

function onConnectionLost(responseObject) {
    app.mqttClient = null;
    if (responseObject.errorCode !== 0) {
        console.log("onConnectionLost:"+responseObject.errorMessage);
        console.log("连接已断开，被动");
        layer.msg("连接已断开！", {icon:1,time:1000});
        return;
     }

     // layer.msg("已断开连接！", {icon:1,time:1000});

     console.log("连接已断开，主动");

}
function onMessageArrived(message) {
  console.log("receive:"+ app.mqttTopic + "->" + byteArray2hexStr(message.payloadBytes));

  var source = byteArray2hexStr(message.payloadBytes);
  var parsed = source;
  if (app.selectedDeviceInfo
        && app.selectedDeviceInfo.datDecode
        && app.selectedDeviceInfo.datDecode.scriptText) {

    eval(app.selectedDeviceInfo.datDecode.scriptText);
    parsed= decodeDat(message.payloadBytes);
    jsonObj = JSON.parse(parsed);
    if (jsonObj.sno != app.selectedSensorInfo.sensorNo) {
      console.log("sensorNo->rcv/sel: " + jsonObj.sno +"/"+ app.selectedSensorInfo.sensorNo);
      return;
    }
  }
  // var parsed = eval(script);
  var rowsStr = addMessage(parsed, source,"received");
  if (g_seq==1) {
    $('#table_rows').append(rowsStr);
  } else {
    $('#table_rows').prepend(rowsStr);
  }
}

// hex发送
function sendMessageHex() {
  var cmdHexStr = app.cmdParam;
  if (app.selectedCmdEncodeInfo.includeCrc==0) {
    cmdHexStr = cmdHexStr + " " + CRC.ToModbusCRC16(app.cmdParam);
  }
  var msgByte = hexStringToBytes(cmdHexStr);
  var source = byteArray2hexStr(msgByte);
  var parsed = source;

  message = new Paho.MQTT.Message(msgByte);
  message.destinationName = "TC/CMD/" + app.selectedDeviceInfo.deviceSn;
  app.mqttClient.send(message);
  console.log("send:"+ message.destinationName + "->" + source);

  addMessage(parsed, source, "sent");
}


//发送消息
function sendMessage() {

      if (app.hexSend) {
        sendMessageHex();
        return;
      }
      if (!app.selectedCmdEncodeInfo || !app.selectedCmdEncodeInfo.scriptText){
        layer.msg("传感器未设置指令！", {icon:1,time:1000});
        return;
      }
      var cmdName = app.selectedCmdEncodeInfo.cmdName;
      var cmdParamSchema = app.selectedCmdEncodeInfo.paramSchema;
      var cmdParam = cmdParamSchema=="void"?"":app.cmdParam;
      var cmdScript = app.selectedCmdEncodeInfo.scriptText;

      eval(cmdScript);
      var msgHexStr = encodeCmd(cmdParam);
      if (app.selectedCmdEncodeInfo.includeCrc==0) {
        msgHexStr = msgHexStr + " " + CRC.ToModbusCRC16(msgHexStr);
      }
      msgByte = hexStringToBytes(msgHexStr);
      var parsed = cmdName + "(" + cmdParam + ")";
      var source = byteArray2hexStr(msgByte);
      // [0x01,0x03,0x00,0x00,0x00,0x02,0xc4,0x0b];
      message = new Paho.MQTT.Message(msgByte);
  		message.destinationName = "TC/CMD/" + app.selectedDeviceInfo.deviceSn;
      app.mqttClient.send(message);
      console.log("send:"+ message.destinationName + "->" + source);

      addMessage(parsed, source, "sent");

}


var g_seq = 1;
function addMessage(parsed, source, type) {

  var item = {};
  item.seq = g_seq++;
  item.time = dateFormat(new Date(),"MM-dd HH:mm:ss.ms");
  item.type = type;
  item.parsedData =  parsed;
  item.sourceData = source;
  app.messageList.push(item);
  if (app.messageList.length > 500) {
    app.messageList.splice(0, app.messageList.length-100);
  }

}

function searchDevice() {

    if (!checkAuth()) {
      return;
    }

    var param = {"method":"realtime.device.search",
                "auth":[localStorage.appId, localStorage.appToken]};

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        var result = response.result;
        for(i in result) {
          result[i].status = "";
        }

        fillSelectCondition(result);
        layer.msg("查询成功！", {icon:1,time:1000});

        // 设置在线状态
        getDeviceStatus();

        // 订阅设备连线事件
        subscribeDeviceStatus();
      });
  }

  function getDeviceStatus() {

        if (!checkAuth()) {
          return;
        }

        var deviceInfo = app.myDeviceInfo;
        var deviceSns = [];
        for (i in deviceInfo) {
          deviceSns[i] = deviceInfo[i].deviceSn;
        }
        var param = {"method":"realtime.device.online.query",
                    "auth":[localStorage.appId, localStorage.appToken],
                    "data":{"deviceIds":deviceSns}};

        ajaxPost(G_RPC_URL, param,
          function(response){

            if (response.status < 0) {
              layer.msg(response.msg,{icon:2,time:2000});
              return;
            }

            var result = response.result;

            for(i in deviceInfo) {
              deviceInfo[i].status = result[deviceInfo[i].deviceSn];
              console.log(deviceInfo[i].deviceSn + ":" + deviceInfo[i].status);
            }
            app.myDeviceInfo = deviceInfo;
            // layer.msg("在线状态查询成功！", {icon:1,time:1000});

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
        app.selectedDeviceInfo.datDecode = app.myDeviceInfo[i].datDecode;

        app.mySensorInfo = app.selectedDeviceInfo.sensorList;
        console.log("sensorList.length:" + app.selectedDeviceInfo.sensorList.length);

        if (app.selectedDeviceInfo.sensorList.length > 0) {
          app.selectedDeviceInfo.sensorList[0] = app.selectedDeviceInfo.sensorList[0];
          console.log("sensorSelected:"+app.selectedDeviceInfo.sensorList[0].sensorId);
          sensorSelected(app.selectedDeviceInfo.sensorList[0].sensorId);
        } else {
          app.selectedSensorInfo = {};
          app.selectedCmdEncodeInfo = {};
        }

        return;
      }
    }
  }

  function sensorSelected(sensorId) {
    for(i in app.selectedDeviceInfo.sensorList) {

        if (sensorId == app.selectedDeviceInfo.sensorList[i].sensorId) {
            app.selectedSensorInfo.sensorId = sensorId;
                app.selectedSensorInfo.sensorNo = app.selectedDeviceInfo.sensorList[i].sensorNo;
            app.selectedSensorInfo.cmdEncodeList = app.selectedDeviceInfo.sensorList[i].cmdEncodeList;

            console.log("cmdEncodeList.length:"+app.selectedSensorInfo.cmdEncodeList.length);
            app.myCmdEncodeInfo = app.selectedSensorInfo.cmdEncodeList;

            if (app.selectedSensorInfo.cmdEncodeList.length > 0) {
                cmdEncodeSelected(app.selectedSensorInfo.cmdEncodeList[0].cmdNo);
            } else {
                app.selectedCmdEncodeInfo = {};
                app.myParamSchema = "";
            }

            return;
        }
    }
  }

  function cmdEncodeSelected(cmdNo) {
      console.log("cmdEncodeList.length:"+app.selectedSensorInfo.cmdEncodeList.length);
      console.log("cmdNo:"+cmdNo);
      for(i in app.selectedSensorInfo.cmdEncodeList) {
        console.log("cmdEncodeList[i].cmdNo:"+app.selectedSensorInfo.cmdEncodeList[i].cmdNo);
        if(cmdNo == app.selectedSensorInfo.cmdEncodeList[i].cmdNo) {
          console.log("cmdEncodeList[i]:"+i);
          console.log("paramSchema:"+app.selectedSensorInfo.cmdEncodeList[i].paramSchema);
          app.selectedCmdEncodeInfo = app.selectedSensorInfo.cmdEncodeList[i];
          app.myParamSchema = app.selectedCmdEncodeInfo.paramSchema;


          return;
        }
      }
  }
