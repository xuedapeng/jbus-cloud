// pages/login/login.js

const app = getApp(); 

Page({

  /**
   * 页面的初始数据
   */
  data: {
    account: wx.getStorageSync('account'),
    password: '',
    accountMap:{},
    isShowAccountList:false,
  },

  onLoad() {
    // wx.clearStorageSync();
  },

  onShow(){
    
    this.setData({
      account: wx.getStorageSync('account')
    });

    var accountMap = wx.getStorageSync('accountMap');
    if (accountMap) {
      accountMap = JSON.parse(accountMap);
      this.setData({"accountMap":accountMap});
    }
    console.log("accountMap:"+JSON.stringify(this.data.accountMap));
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

  showAccountList(){
    this.setData({"isShowAccountList":!this.data.isShowAccountList});
  },

  changeAccount(e) {
    var accountInfo = this.data.accountMap[e.currentTarget.dataset.account];
    wx.setStorageSync('auth', accountInfo.auth);
    wx.setStorageSync('mqtt.user', accountInfo["mqtt.user"]);
    wx.setStorageSync('mqtt.pwd', accountInfo["mqtt.pwd"]);
    wx.setStorageSync('account', accountInfo.account);
    wx.setStorageSync('nickName', accountInfo.nickName);

    wx.navigateBack({
          
    })
  },

  login() {
    // wx.clearStorageSync();
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
        wx.setStorageSync('nickName', res.data.nickName);

        // 账号切换
        var accountMap = wx.getStorageSync('accountMap');
        console.log("accountMap2:"+accountMap);
        if (!accountMap) {
          accountMap = {};
        } else {
          accountMap = JSON.parse(accountMap);
        }

        console.log("accountMap3:"+ JSON.stringify(accountMap));

        accountMap[page.data.account] = {
          "auth":[res.data.secretId, res.data.secretKey],
          "mqtt.user": res.data.emqUser,
          "mqtt.pwd": res.data.secretKey,
          "account":page.data.account,
          "nickName": res.data.nickName
        }
        wx.setStorageSync('accountMap', JSON.stringify(accountMap));
        page.data.accountMap = accountMap;

        console.log("accountMap4:"+JSON.stringify(page.data.accountMap));

        wx.navigateBack({
          
        })
      }
    })

  },


})