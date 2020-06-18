//app.js
App({
  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })


    wx.showShareMenu({
      withShareTicket: true
    })
  },
  globalData: {
    userInfo: null,
    auth: function(){
      // return ["230119A306D3534958B3D29B", "4EFECC039DE248F288B64DE33C083AD9"];
      return wx.getStorageSync('auth');
    },
    auth4test:function(){
      return ["C7EBEB4D3DB64C72BD439BB11D41EA3E","72654A9980014591BC87548FB2F609BA"];
    },
    url:"https://cloud.moqbus.com/open/api/",
    mqtt:{
      user:function(){
        return wx.getStorageSync("mqtt.user");
        },
      pwd: function () {
        return wx.getStorageSync("mqtt.pwd");
        },
      // host:"cloud.moqbus.com",
      // port:8083
    },
  }
})