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
    valueMap: {},
    styleMap: {},
    deviceOnlineStatus: {},
    lastLoadTime: "",
    projectId:"0",
    pageStack:["0"]
  },

  onLoad:function(options) {
    if (options.projectId) {
      this.setData({projectId:options.projectId});
    }
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
          duration: 5000,
        });
      }
    });

  },
  makeProjectList(result) {
    this.data.projectListTemp = result;
    this.updateOnlineStatus();
    // this.updateRealtimeData(); // move to finish of updateOnlineStatus
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

        page.updateRealtimeData();
      }
    });
  },

  
  updateRealtimeData() {
    this.data.fieldValues = {};
    this.data.valueMap = {};
    this.data.styleMap = {};
    var page = this;

    console.log("updateRealtimeData:projectId="+page.data.projectId);
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "uiconfig.data.get",
        "data": { "projectId": page.data.projectId },
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
        page.data.valueMap = res.data.resultMap;
        page.data.styleMap = res.data.styleMap;

        for (var i in page.data.projectListTemp) {

          var pj = page.data.projectListTemp[i];
          for (var j in pj.cover) {
            var fd = pj.cover[j];
            page.getRealtimeData(pj.seq, fd.seq, fd.deviceSn, fd.sensorNo, fd.field);
          }
        }
        page.updateAllData();
      }
    })
  },
  updateAllData() {
    for (var i in this.data.projectListTemp) {

      var pj = this.data.projectListTemp[i];

      for (var k in pj.deviceSnList) {
        pj.deviceSnList[k].status = this.data.deviceOnlineStatus[k];
      }

      for (var j in pj.cover) {
        var fd = pj.cover[j];

        if (this.data.fieldValues[pj.seq] && this.data.fieldValues[pj.seq][fd.seq]) {
          fd.data = this.data.fieldValues[pj.seq][fd.seq];

          if(fd.valuePtn) {
            if (fd.valuePtn[fd.data.val + ""]) {
              fd.data.val = fd.valuePtn[fd.data.val + ""];
            } else if(fd.valuePtn["default"]) {
              fd.data.val = fd.valuePtn["default"] + "(" + fd.data.val + ")";
            }
          }
        }
      }
    }
    this.setData({ "projectList": this.data.projectListTemp });

    wx.showToast({
      title: "加载数据成功！",
      icon: 'none',
      duration: 500,
    });

  },
  getRealtimeData(p_seq, f_seq, deviceSn, sensorNo, field) {


    var fvs = this.data.fieldValues;
    var dv = this.getRealtimeValDisplay(p_seq, f_seq, deviceSn, sensorNo, field);
    if (dv) {
      if (!fvs[p_seq]) {
        fvs[p_seq] = {};
      }
      fvs[p_seq][f_seq] = dv;
    }

    this.data.fieldValues = fvs;

  },

  getRealtimeValDisplay(p_seq, f_seq, deviceSn, sensorNo, field) {
    var display = [];
    var fieldStyle = this.data.styleMap[deviceSn][sensorNo][field];
    var val = this.data.valueMap[deviceSn][sensorNo][field];
    var time =  this.data.valueMap[deviceSn][sensorNo]["time"];
    
    if (!val) {
      return "";
    }

    if(fieldStyle) {
      
      var item = {
        "name": fieldStyle.display,
        "val": val,
        "unit": fieldStyle.unit,
        "color": "black",
        "agoTime": this.getAgoTime(time)
      };

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

    this.data.pageStack.push(this.data.projectId);

    var projectId = e.currentTarget.dataset.projectId;
    this.onLoad({projectId:projectId});
    this.onShow();
   
  },

  back(e) {

    var projectId = this.data.pageStack.pop();
    if(this.data.pageStack.length == 0) {
      this.data.pageStack.push("0");
    }
    this.onLoad({projectId:projectId});
    this.onShow();
  }


})



