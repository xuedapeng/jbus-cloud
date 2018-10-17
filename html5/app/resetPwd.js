
var app = new Vue({
  el: '#app',
  data: {
    account: getQueryString("account"),
    password: '',
    confirmCode: '',
    errorInfo:'',
    title: G_LABEL_APP_TITLE,
  },
  methods:{
    sendCode:function() {
      clearError();
      _sendCode();
    },
    resetPwd:function() {
      clearError();
      if(!validate()) {
        return;
      }
      _resetPwd();
    },
  }
});

function clearError() {
  app.errorInfo = '';
}

function validate() {

    if (app.account == "") {
      app.errorInfo = "请输入用户名";
      return false;
    }
    if (app.password == "") {
      app.errorInfo = "请输入新密码";
      return false;
    }
    if (app.confirmCode == "") {
      app.errorInfo = "请输入验证码";
      return false;
    }

    return true;
}

function _sendCode() {

  if (app.account == "") {
    app.errorInfo = "请输入用户名";
    return;
  }

  var param = {"method":"user.confirmcode.send",
              "data":{
                "account":app.account,
                "func":"pwd"
              }};

  ajaxPost(G_RPC_URL, param,
    function(response){
      console.log(response.msg);
      if (response.status<0) {
        app.errorInfo = response.msg;
        return;
      }

      layer.msg("验证码已发送至邮箱。", {icon:1,time:3000});

  });
}

function _resetPwd() {

  var param = {"method":"user.password.reset",
              "data":{
                "account":app.account,
                "password":app.password,
                "confirmCode":app.confirmCode,
            }};

  ajaxPost(G_RPC_URL, param,
    function(response){
      console.log(response.msg);
      if (response.status<0) {
        app.errorInfo = response.msg;
        return;
      }

      layer.msg("密码重置成功! 稍后跳转至登录页面...", {icon:1,time:3000});

      setTimeout("_moveToLogin()", 3000);


  });
}

function  _moveToLogin() {
  window.location.href = "login.html?account="+app.account;
}
