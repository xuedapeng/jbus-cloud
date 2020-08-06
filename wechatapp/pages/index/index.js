//index.js
//获取应用实例
const app = getApp(); 
// import mqtt from '../../utils/mqtt.js';
import dateFormat from '../../utils/util.js';

var page=Page({
  data: {
    scrollTop: 10, 
    deviceList: [],
    mqttClientStatus: null, 
    mqttStatusTopics:[],
    console:"",
    acount:wx.getStorageSync('account')
  },
  scroll(e){

  },
  onShow(q) {
    if (!this.checkAuth()) {
      return;
    }
    if (this.data.deviceList.length > 0) {
      // return;
    }

    this.loadData();
    // setInterval(this.updateRealtimeData, 1000 * 60); 
  },

  loadData() {
    var page = this;
    wx.request({
      url: app.globalData.url, 
      data: {
        "method": "device.list",
        "auth": app.globalData.auth(),
        "data": { "page": "1", "pageSize": "20" }
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        console.log(res.data);
        page.setData({ "deviceList": res.data.result });
        // 设置在线状态
        page.getDeviceStatus();
        // 订阅设备连线事件
        // page.subscribeDeviceStatus();
      }
    })
  },

  getDeviceStatus() {

    var deviceInfo = this.data.deviceList;
    var deviceSns = [];
    for (var i in deviceInfo) {
      deviceSns[i] = deviceInfo[i].deviceSn;
    }


    var page = this;
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "realtime.device.online.query",
        "auth": app.globalData.auth(),
        "data": { "deviceIds": deviceSns }
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        console.log(res);
        var onFirstList =  [];
        for (var i in deviceInfo) {
          deviceInfo[i].status = res.data.result[deviceInfo[i].deviceSn];
          if (deviceInfo[i].status == 'on') {
            onFirstList.push(deviceInfo[i]);
          }
        }

        for (var i in deviceInfo) {
          if (deviceInfo[i].status == 'off') {
            onFirstList.push(deviceInfo[i]);
          }
        }
        page.setData({ "deviceList": onFirstList });
        console.log(page.data);
        
        // 查询实时数据 
        // page.updateRealtimeData();
      }
    })
  },

  subscribeDeviceStatus() {
      var page = this;
      var pagedata = page.data;
      
    if (pagedata.mqttClientStatus) {
      pagedata.mqttClientStatus.disconnect();
      }
      var clientId = guid();
      console.log("guid_status=" + clientId);
      log(page, pagedata.console + " # " + clientId);
    // var client = new Paho.MQTT.Client(app.globalData.mqtt.host, Number(app.globalData.mqtt.port), clientId);//建立客户端实例

    //MQTT连接的配置
    var options =  {
      protocolVersion: 4, //MQTT连接协议版本
      clientId: clientId,
      clean: false,
      password: app.globalData.mqtt.pwd(),
      username: app.globalData.mqtt.user(),
      reconnectPeriod: 1000, //1000毫秒，两次重新连接之间的间隔
      connectTimeout: 30 * 1000, //1000毫秒，两次重新连接之间的间隔
      resubscribe: true //如果连接断开并重新连接，则会再次自动订阅已订阅的主题（默认true）
    }
    var client = mqtt.connect('wss://cloud.moqbus.com/mqtt', options);

    log(page, "client connect");

    client.on("offline", function () {
      log(page, "mqttClientStatus connection lost.");
    });//注册连接断开处理事件

    client.on("error", function () {
      log(page, "mqttClientStatus connection error.");
    });

    client.on("message", function (topic, message) {
      var deviceInfo = pagedata.deviceList;
      log(page, "receive topic:" + topic);
      log(page, "receive data:" + message);
      var data = JSON.parse(message);
      for (var i in deviceInfo) {
        if (data.deviceSn == deviceInfo[i].deviceSn) {
          if (data.onlineCount > 0) {
            deviceInfo[i].status = "on";
          } else {
            deviceInfo[i].status = "off";
          }
        }
      }
        
      page.setData({ "deviceList": deviceInfo });
      page.sortDevice();

      });//注册消息接收处理事件

      client.on("connect", function () {
         log(page, "mqttClientStatus connected.");
          // subscribe
          pagedata.mqttStatusTopics.length = 0;
        if (pagedata.deviceList.length > 0) {
            for (var i in pagedata.deviceList) {
              pagedata.mqttStatusTopics[i] = "TC/STS/" + pagedata.deviceList[i].deviceSn;
            }
          }
          if (pagedata.mqttStatusTopics.length > 0) {
            for (var i in pagedata.mqttStatusTopics) {
              client.subscribe(pagedata.mqttStatusTopics[i]);
              log(page, "subscribe:" + pagedata.mqttStatusTopics[i]);
            }
          }
        },
      );//连接服务器并注册连接成功处理事件

      pagedata.mqttClientStatus = client;

    },

  sortDevice(){

    var page = this;
    var deviceInfo = this.data.deviceList;

    var onFirstList = [];
    for (var i in deviceInfo) {
      if (deviceInfo[i].status == 'on') {
        onFirstList.push(deviceInfo[i]);
      }
    }

    for (var i in deviceInfo) {
      if (deviceInfo[i].status == 'off') {
        onFirstList.push(deviceInfo[i]);
      }
    }

    page.setData({ "deviceList": onFirstList });
  },

  updateRealtimeData() {

    var page = this;
    var pagedata = page.data;
    
    for (var i=0;  i<pagedata.deviceList.length; i++) {
      pagedata.deviceList[i].realtimeVal = '';
      if (pagedata.deviceList[i].status == 'on') {
        page.getRealtimeData(i);
      }
    }

    // setTimeout(page.setDataDeviceList, 5000);
    
  },

  setDataDeviceList() {
    this.setData({ "deviceList": this.data.deviceList });
  },

  getRealtimeData(idx) {

    var page = this;
    var pagedata = page.data;
    var sensorList = pagedata.deviceList[idx].sensorList;
    pagedata.deviceList[idx].realtimeVal = {};
    for(var i in sensorList) {
      page.requestRealtimeVal(idx, i);
    }
  },

  requestRealtimeVal(dIdx, sIdx) {

    var page = this;
    var pagedata = page.data;
    var sensorList = pagedata.deviceList[dIdx].sensorList;
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "history.data.query",
        "auth": app.globalData.auth(),
        "data": { "deviceId": pagedata.deviceList[dIdx].deviceId + "", "sensorNo":sensorList[sIdx].sno + '', "pageSize": "1" }
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        console.log(res.data);
        if (res.data.status < 0) {
          return;
        }
        pagedata.deviceList[dIdx].realtimeVal[sensorList[sIdx].sno] = page.getRealtimeValDisplay(res.data);
        // var key = "deviceList[" + idx + "].realtimeVal";
        console.log("sIdx" + sIdx + "=" + sensorList[sIdx].sno);
        console.log(pagedata.deviceList[dIdx].realtimeVal);
        page.setData({ "deviceList": pagedata.deviceList });

      }
    })
  },

  getRealtimeValDisplay(resdata) {
    var display = [];
    var fieldStyle = resdata.fieldStyle;
    var result = resdata.result;
    // log(pagedata, "result.time.length=" + result.time.length);
    if (result.time.length == 0) {
      return "";
    }

    var index = 0;
    var len = Object.keys(fieldStyle).length;
    for (var key in fieldStyle) {
      var val = result[key][0];
      var item = {
        "name": fieldStyle[key].display,
        "val": val,
        "unit": fieldStyle[key].unit,
        "color":"black",
        "agoTime": ((index == (len-1))?getAgoTime(result["time"][0]):"")
      };

      var range = fieldStyle[key].range;
      if (range) {
        var minmax = range.split(",");
        if (val < minmax[0]) {
          item.color = "blue";
        } else if (val > minmax[1]) {
          item.color = "red";
        }
      }
      
      display.push(item);
      index++;
    }
    return display;
  },

  checkAuth() {
    var auth = wx.getStorageSync('auth');
    if(!auth) {

      wx.setStorageSync('auth', app.globalData.auth4test());
      auth = wx.getStorageSync('auth');
      console.log("auth:" + auth)
    }
    // if (!auth) {
    //   wx.navigateTo({
    //     url: '../login/login',
    //   })
    //   return false;
    // }

    return true;
  },

  detail(e) {
    if (e.currentTarget.dataset.status != 'on') {
      return;
    }
    var deviceId = e.currentTarget.dataset.deviceId;
    var sno = e.currentTarget.dataset.sno;
    // console.log(e.currentTarget.dataset.deviceId);
    wx.navigateTo({
      url: '../history/history?deviceId='+deviceId + '&sno=' + sno
    })
    
  }



});

function guid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function log(obj, msg) {
  console.log(msg);
  // obj.setData({ "console": obj.data.console + " # " + msg});
}

function getAgoTime(time) {
  // time = "2019-06-04 10:52";
  time = time.replace(/[-]/g,"/");
  var now = new Date();
  var orgTime = Date.parse(time);
  var intv = (now - orgTime) / 1000;
  // return (time);
  if (intv > 24*60*60) {
    return "( + Math.floor(intv/(24*60*60)) + 天前)";
  }
  if (intv > 1*60*60) {
    return "(" + Math.floor(intv/(60*60)) + "小时前)";
  }

  if (intv > 1 * 60) {
    return "(" + Math.floor(intv / (60)) + "分钟前)";
  }

  return "";
}

