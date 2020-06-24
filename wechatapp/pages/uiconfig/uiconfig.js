// pages/uiconfig/uiconfig.js
const app = getApp(); 

Page({

  /**
   * 页面的初始数据
   */
  data: {
    scrollTop: 10,
    projectList: [],
    projectListTemp: [],
    fieldValues: {},
    deviceOnlineStatus: {},
    lastLoadTime: "",
    projectId:"0",
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onShow: function (options) {
    var page = this;
    this.loadData();
    // this.data.projectList = dummyData();
    // page.setData({ "projectList": dummyData()});
    console.log(this.data.projectList);
  },

  sendCmd(event) {
    var sn = event.currentTarget.dataset.sn;
    var cmd = event.currentTarget.dataset.cmd;
    console.log(sn + "," + cmd);


    var page = this;
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "realtime.device.sendcmd",
        "auth": app.globalData.auth(),
        "data": {
          "deviceSn": sn,
          "cmd": cmd
        }
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        if (res.data.status < 0) {

          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000,
          });

          return;
        }

        wx.showToast({
          title: "指令发送成功!",
          icon: 'none',
          duration: 2000,
        });

      }
    })
  },

  loadData(hasparam, projectId) {

    var page = this;
    console.log("loadData:projectId=" + projectId);
    console.log("loadData:data.projectId=" + page.data.projectId);
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "uiconfig.projects.get",
        "data": { "projectId": hasparam==1?projectId:page.data.projectId },
        "auth": app.globalData.auth()
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        if (res.data.status < 0) {

          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000,
          });

          return;
        }
        
        if (hasparam==1) {
          page.setData({ "projectId": projectId });
        }
        page.makeProjectList(res.data.result);


        wx.showToast({
          title: "正在加载，请稍候...",
          icon: 'none',
          duration: 2000,
        });
      }
    });

  },
  makeProjectList(result) {
    this.data.projectListTemp = result;
    this.updateOnlineStatus();
    this.updateRealtimeData();
  },

  updateOnlineStatus() {

    for (var i in this.data.projectListTemp) {

      var pj = this.data.projectListTemp[i];
      for (var key in pj.deviceSnList) {
        this.data.deviceOnlineStatus[key] = "off";
      }
    }

    this.getDeviceStatus();
  },
  getDeviceStatus() {

    var page = this;
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "realtime.device.online.query",
        "auth": app.globalData.auth(),
        "data": { "deviceIds": Object.keys(page.data.deviceOnlineStatus) }
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        if (res.data.status < 0) {

          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000,
          });

          return;
        }

        var result = res.data.result;

        for (var key in page.data.deviceOnlineStatus) {
          page.data.deviceOnlineStatus[key] = result[key];
        }
      }
    });
  },

  updateRealtimeData() {

    this.data.fieldValues = {};
    for (var i in this.data.projectListTemp) {

      var pj = this.data.projectListTemp[i];
      for (var j in pj.cover) {
        var fd = pj.cover[j];
        
        this.getRealtimeData(pj.seq, fd.seq, pj.deviceSnList[fd.deviceSn].id, fd.sensorNo, fd.field);
      }
    }

    setTimeout(this.updateAllData, 3000);
  },
  updateAllData() {
    for (var i in this.data.projectListTemp) {

      var pj = this.data.projectListTemp[i];

      for (var k in pj.deviceSnList) {
        pj.deviceSnList[k].status = this.data.deviceOnlineStatus[k];
      }

      for (var j in pj.cover) {
        var fd = pj.cover[j];

        // console.log(pj.seq + "," + fd.seq);
        if (this.data.fieldValues[pj.seq] && this.data.fieldValues[pj.seq][fd.seq]) {
          fd.data = this.data.fieldValues[pj.seq][fd.seq];

          if (fd.valuePtn && fd.valuePtn[fd.data.val + ""]) {
            fd.data.val = fd.valuePtn[fd.data.val + ""];
          }

          // console.log(fd.data.val);
        }
      }
    }
    // var t = app.projectList
    // this.data.projectList = this.data.projectListTemp;
    this.setData({ "projectList": this.data.projectListTemp });

    wx.showToast({
      title: "加载数据成功！",
      icon: 'none',
      duration: 2000,
    });

    // this.data.lastLoadTime = dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
    //console.log(JSON.stringify(this.data.projectList));

  },
  getRealtimeData(p_seq, f_seq, deviceId, sensorNo, field) {

    this.requestRealtimeVal(p_seq, f_seq, deviceId, sensorNo, field);
  },

  requestRealtimeVal(p_seq, f_seq, deviceId, sensorNo, field) {

    var page = this;
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "history.data.query",
        "auth": app.globalData.auth(),
        "data": {
          "deviceId": deviceId + "",
          "sensorNo": sensorNo + "",
          "pageSize": "1" 
        }
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        if (res.data.status < 0) {

          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000,
          });

          return;
        }

        var result = res.data.result;

        var fvs = page.data.fieldValues;
        var dv = page.getRealtimeValDisplay(res.data, p_seq, f_seq, deviceId, sensorNo, field);
        if (dv) {
          if (!fvs[p_seq]) {
            fvs[p_seq] = {};
          }
          fvs[p_seq][f_seq] = dv;
        }

        page.data.fieldValues = fvs;
      }
    });
  },
  getRealtimeValDisplay(resdata, p_seq, f_seq, deviceId, sensorNo, field) {
    var display = [];
    var fieldStyle = resdata.fieldStyle;
    var result = resdata.result;
    // log(pagedata, "result.time.length=" + result.time.length);
    if (result.time.length == 0) {
      return "";
    }

    var len = Object.keys(fieldStyle).length;
    for (var key in fieldStyle) {
      if (key != field) {
        continue;
      }
      var val = result[key][0];
      var item = {
        "name": fieldStyle[key].display,
        "val": val,
        "unit": fieldStyle[key].unit,
        "color": "black",
        "agoTime": this.getAgoTime(result["time"][0])
      };

      // console.log("val="+item.val);

      var range = fieldStyle[key].range;
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
  },
  getAgoTime(time) {
    // time = "2019-06-04 10:52";
    time = time.replace(/[-]/g, "/");
    var now = new Date();
    var orgTime = Date.parse(time);
    var intv = (now - orgTime) / 1000;
    // return (time);
    if (intv > 24 * 60 * 60) {
      return "(" + Math.floor(intv / (24 * 60 * 60)) + "天前)";
    }
    if (intv > 1 * 60 * 60) {
      return "(" + Math.floor(intv / (60 * 60)) + "小时前)";
    }

    if (intv > 1 * 60) {
      return "(" + Math.floor(intv / (60)) + "分钟前)";
    }

    return "";
  },

  detail(e) {

    var page = this;
    var projectId = e.currentTarget.dataset.projectId;
    page.loadData(1,projectId);

    console.log("detail:projectId=" + projectId);
    console.log("detail:data.projectId=" + page.data.projectId);
   
  }


})




function dummyData() {

var projectList =
  [{
    "cover": [{
      "type": "dat",
      "deviceSn": "BJW401",
      "sensorNo": "1",
      "field": "wd",
      "name": "大厅＝ 温度",
      "valuePtn": {},
      "seq": "1",
      "data": {
        "name": "温度",
        "val": "30.7",
        "unit": "℃",
        "color": "black",
        "agoTime": ""
      }
    }, {
      "type": "dat",
      "deviceSn": "BJW401",
      "sensorNo": "1",
      "field": "sd",
      "name": "　湿度",
      "valuePtn": {},
      "nowrap": "true",
      "seq": "2",
      "data": {
        "name": "湿度",
        "val": "47.3",
        "unit": "%",
        "color": "black",
        "agoTime": ""
      }
    }, {
      "type": "dat",
      "deviceSn": "5E05C07E",
      "sensorNo": "1",
      "field": "wd",
      "name": "办公区＝ 温度",
      "valuePtn": {},
      "seq": "3",
      "data": {
        "name": "温度",
        "val": "31.5",
        "unit": "℃",
        "color": "black",
        "agoTime": ""
      }
    }, {
      "type": "dat",
      "deviceSn": "5E05C07E",
      "sensorNo": "1",
      "field": "sd",
      "name": "　湿度",
      "valuePtn": {},
      "nowrap": "true",
      "seq": "4",
      "data": {
        "name": "湿度",
        "val": "42.6",
        "unit": "%",
        "color": "black",
        "agoTime": ""
      }
    }, {
      "type": "dat",
      "deviceSn": "5E05C07E",
      "sensorNo": "3",
      "field": "wd",
      "name": "办公区2＝ 温度",
      "valuePtn": {},
      "seq": "5",
      "data": {
        "name": "温度",
        "val": "28.7",
        "unit": "℃",
        "color": "black",
        "agoTime": ""
      }
    }, {
      "type": "dat",
      "deviceSn": "5E05C07E",
      "sensorNo": "3",
      "field": "sd",
      "name": "　湿度",
      "valuePtn": {},
      "nowrap": "true",
      "seq": "6",
      "data": {
        "name": "湿度",
        "val": "58.0",
        "unit": "%",
        "color": "black",
        "agoTime": ""
      }
    }],
    "deviceSnList": {
      "BJW401": {
        "name": "北京路气象站",
        "id": "47",
        "status": "on"
      },
      "5E05C07E": {
        "name": "绿地气象站",
        "id": "62",
        "status": "on"
      }
    },
    "coverCmd": [{
      "type": "cmd",
      "deviceSn": "BJW401",
      "cmd": "01 03 00 00 00 02 c4 0b",
      "name": "客厅温湿度=查",
      "seq": "1"
    }, {
      "type": "cmd",
      "deviceSn": "5E05C07E",
      "cmd": "02 03 00 0a 00 02 e4 3a",
      "name": "办公区温湿度=查",
      "seq": "2"
    }, {
      "type": "cmd",
      "deviceSn": "5E05C07E",
      "cmd": "03 03 02 00 00 03 05 91",
      "name": "办公区2温湿度=查",
      "seq": "3"
    }],
    "title": "镜湖气象站",
    "projectId": "1",
    "seq": "1"
  }, {
      "cover": [{
        "deviceSn": "3DF5C433",
        "sensorNo": "11",
        "field": "sw",
        "name": "电动阀(11)",
        "valuePtn": {
          "0": "关",
          "1": "开"
        },
        "seq": "1",
        "data": {
          "name": "阀门状态",
          "val": "开",
          "unit": " ",
          "color": "black",
          "agoTime": "(21天前)"
        }
      }, {
        "deviceSn": "3DF5C433",
        "sensorNo": "21",
        "field": "sw",
        "name": "电动阀(21)",
        "valuePtn": {
          "0": "关",
          "1": "开"
        },
        "seq": "2",
        "data": {
          "name": "阀门状态",
          "val": "开",
          "unit": " ",
          "color": "black",
          "agoTime": "(21天前)"
        }
      }, {
        "deviceSn": "7835DA60",
        "sensorNo": "32",
        "field": "h",
        "name": "水位(32)",
        "valuePtn": {},
        "seq": "3",
        "data": {
          "name": "水层",
          "val": "10",
          "unit": "毫米",
          "color": "black",
          "agoTime": "(21天前)"
        }
      }, {
        "deviceSn": "7835DA60",
        "sensorNo": "22",
        "field": "h",
        "name": "水位(22)",
        "valuePtn": {},
        "seq": "4",
        "data": {
          "name": "水层",
          "val": "391",
          "unit": "毫米",
          "color": "black",
          "agoTime": "(20天前)"
        }
      }],
      "deviceSnList": {
        "3DF5C433": {
          "name": "平湖水稻-电动阀",
          "id": "68",
          "status": "off"
        },
        "7835DA60": {
          "name": "平湖水稻-水位计",
          "id": "71",
          "status": "off"
        }
      },
      "coverCmd": [{
        "type": "cmd",
        "deviceSn": "3DF5C433",
        "cmd": "51 01 a5 01 16 00 00 F2 0D 0A",
        "name": "电动阀11=开",
        "seq": "1"
      }, {
        "type": "cmd",
        "deviceSn": "3DF5C433",
        "cmd": "51 02 a5 01 16 00 00 F1 0D 0A",
        "name": "关",
        "nowrap": "true",
        "seq": "2"
      }, {
        "type": "cmd",
        "deviceSn": "3DF5C433",
        "cmd": "51 00 a5 01 16 00 00 F3 0D 0A",
        "name": "查",
        "nowrap": "true",
        "seq": "3"
      }, {
        "type": "cmd",
        "deviceSn": "3DF5C433",
        "cmd": "51 01 a1 01 16 00 00 F6 0D 0A",
        "name": "电动阀21=开",
        "seq": "4"
      }, {
        "type": "cmd",
        "deviceSn": "3DF5C433",
        "cmd": "51 02 a1 01 16 00 00 F5 0D 0A",
        "name": "关",
        "nowrap": "true",
        "seq": "5"
      }, {
        "type": "cmd",
        "deviceSn": "3DF5C433",
        "cmd": "51 00 a1 01 16 00 00 F7 0D 0A",
        "name": "查",
        "nowrap": "true",
        "seq": "6"
      }],
      "title": "平湖水稻示范区",
      "projectId": "2",
      "seq": "2"
    }];

    return projectList;
}