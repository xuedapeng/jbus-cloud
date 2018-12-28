
var app = new Vue({
  el: '#app',
  data: {
    account: '',
    password: '',
    message:'',
    title: G_LABEL_APP_TITLE,
  },
  methods:{
    login:function() {
      app.message = '';
      return  _login(app.account, app.password);
    },
    goReg(){
      moveTo("reg.html");
    },
    resetPwd(){
      moveTo("resetPwd.html?account="+app.account)
    },
  }
});

function _login(account, password) {
  if (account == "") {
    app.message = "请输入账号";
    return;
  }
  if (password == "") {
    app.message = "请输入密码";
    return;
  }

  var param = {"method":"user.login", "data":{"account":account, "password":password}};

  ajaxPost(G_RPC_URL, param,
    function(response){
      console.log(response.msg);
      if (response.status<0) {
        app.message = response.msg;
        return;
      }
      setStorage("appId", response.secretId);
      setStorage("appToken",response.secretKey);
      setStorage("account", app.account);
      setStorage("nickName", response.nickName);
      setStorage("emqUser", response.emqUser);
      setStorage("emqPwd", response.emqPwd);

      window.location.href = "../index.html";
  });

}
