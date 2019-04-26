// pages/login/login.js

const app = getApp(); 

Page({

  /**
   * 页面的初始数据
   */
  data: {
    account: wx.getStorageSync('account'),
    password: ''
  },

  onLoad() {
    // wx.clearStorageSync();
  },

  accountInput(event) {
    this.setData({
      account: event.detail.value
    })

  },
  passwordInput(event) {
    this.setData({
      password: event.detail.value
    })

  },

  login() {
    wx.clearStorageSync();
    var page = this;
    if (this.data.account == '' || this.data.password == '') {
      wx.showToast({
        title: '请输入用户名和密码',
        icon: 'none',
        duration: 2000,
      });
      return false;
    }
    
    var page = this;
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "user.login",
        "data": { "account": page.data.account, "password": page.data.password}
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        console.log(res.data);
        if (res.data.status < 0) {
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000,
          });

          return;
        }

        wx.setStorageSync('auth', [res.data.secretId, res.data.secretKey]);
        wx.setStorageSync('mqtt.user', res.data.emqUser);
        wx.setStorageSync('mqtt.pwd', res.data.secretKey);
        wx.setStorageSync('account', page.data.account);

        wx.navigateBack({
          
        })
      }
    })

  },


})