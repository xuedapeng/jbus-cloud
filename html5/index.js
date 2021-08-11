
$(function(){
		var appId = getStorage("appId");
		var appToken = getStorage("appToken");

		if (!appId || !appToken) {
			//if (confirm("登录信息已失效，是否转到登录页面？")) {
				moveToLogin();
			//}
		}
});



var app = new Vue({
  el: '#app',
  data: {
		account:getStorage("account"),
		nickName:getStorage("nickName"),
    secretId:getStorage("appId"),
    mqttUser:getStorage("emqUser"),
    sysAdmin:getStorage("sysAdmin")==1?true:false,
    adminMode:getStorage("sysAdmin")==1?true:false,
    secretKey:'******',
    secretKeyShow:'显示',
    secretKeyUpdate:'',
		title:G_LABEL_APP_TITLE + " " + G_LABEL_APP_VER,
    oldPassword:'',
    newPassword:'',
    newPasswordRe:'',
    errorInfo:'',
	},
	methods:{
		logout:function(){
			clearStorage();
			moveToLogin();
		},
    updatePwd:function() {
      $("#modal-updatePwd").modal("show");
    },

    showSecretKey:function() {

      if (!checkAuth()) {
        return;
      }

      if (app.secretKeyShow=="显示") {
        app.secretKeyShow = "隐藏";
        app.secretKey = getStorage("appToken");
        app.secretKeyUpdate = "更新";
      } else {
        app.secretKeyShow = "显示";
        app.secretKey = "******";
        app.secretKeyUpdate = "";
      }
    },

    updateSecretKey:function() {
      if(confirm("确定要更新secretKey吗？")) {
        doUpdateSecretKey();
      }
    },

    gotoAdminMode:function(){
      app.adminMode = !app.adminMode;
    }
	}
});

function doUpdatePwd() {

  if (!checkAuth()) {
    return;
  }

  if (!validatePassword()){
    return;
  }


  var param = {"method":"user.password.update",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{
                "oldPassword":app.oldPassword,
                "newPassword":app.newPassword,
            }};

  ajaxPost(G_RPC_URL, param,
    function(response){
      console.log(response.msg);
      if (response.status<0) {
        app.errorInfo = response.msg;
        return;
      }

      layer.msg("密码修改成功! ", {icon:1,time:3000});

      app.errorInfo='';
      app.oldPassword='';
      app.newPassword='';
      app.newPasswordRe='';


  });
}

function validatePassword() {

    app.errorInfo = '';

    if (app.oldPassword.length == 0) {
      app.errorInfo = "请输入旧密码。";
      return false;
    }

    if (app.newPassword.length < 6) {
      app.errorInfo = "请输入新密码，不少于6位。";
      return false;
    }

    if (app.newPassword == app.oldPassword) {
      app.errorInfo = "新密码和旧密码不能相同。";
      return false;
    }


    return true;
}

function doUpdateSecretKey() {

    if (!checkAuth()) {
      return;
    }


    var param = {"method":"user.secretKey.update",
                "auth":[getStorage("appId"), getStorage("appToken")]};

    ajaxPost(G_RPC_URL, param,
      function(response){
        console.log(response.msg);
        if (response.status<0) {
          app.errorInfo = response.msg;
          return;
        }

        layer.msg("secretKey更新成功! ", {icon:1,time:3000});
        var secretKey = response.secretKey;
        setStorage("appToken", secretKey);
        app.secretKey = getStorage("appToken");
    });

}
