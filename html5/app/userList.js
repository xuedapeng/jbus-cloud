
var app = new Vue({
  el: '#app',
  data: {
    userList:[],
    pageOption : {
          "pageTotal":0, //必填,总页数
          "pageAmount":10, //每页多少条
          "dataTotal":0, //总共多少条数据
          "curPage":1, //初始页码,不填默认为1
          "pageSize": 10, //分页个数,不填默认为5
        },
    selectedUserId:null,
    selectedUser:null,
  },
  methods:{
    
    openEditUser:function(userId){
      loadUserData(userId);
      $("#modal-editUser").modal("show");
    },

  }
});

window.onload = function() {
  loadData();
};

function loadData(silent) {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"admin.user.list",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"page":app.pageOption.curPage+"", 
                      "pageSize": app.pageOption.pageAmount+"",
                    }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;

      makeUserList(result);
      if(response.total==0) {
        layer.msg("没有符合条件的数据！", {icon:3,time:2000});
        $("#pagination").empty();
        return;
      } 

      if (!silent) {
        layer.msg("用户查询成功！", {icon:1,time:1000});
      }

      var pageOption = app.pageOption;
      pageOption.dataTotal = response.total;
      pageOption.pageTotal = Math.ceil(app.pageOption.dataTotal/app.pageOption.pageAmount);
      app.pageOption = pageOption;
      console.log("app.pageOption.dataTotal=" + app.pageOption.dataTotal);
      console.log("app.pageOption.pageTotal=" + app.pageOption.pageTotal);
      setPaginator();

    });
}


function makeUserList(result, seq) {

  var dList =[];

  var seq = (app.pageOption.curPage-1)*app.pageOption.pageAmount;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.userId = record['userId'];
    item.account = record['account'];
    item.nickName = record['nickName'];
    item.sysAdmin = record['sysAdmin'];
    item.status = record['status'];
    item.createTime = record['createTime'];
    dList[i] = item;
  }

  app.userList = dList;
  console.log(app.userList.length);

}


var app_add_user = new Vue({
  el: '#modal-addUser',
  data: {
    account:'',
    nickName:'',
  },
  methods:{
    openAddUser:function(){
      app_add_user.account = '';
      app_add_user.nickName = '';
      $("#modal-addUser").modal("show");
    },
  }
});

function doAddUser() {

    if (!checkAuth()) {
      return;
    }

    var param = {"method":"admin.user.add",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{
                  "account":app_add_user.account,
                  "nickName":app_add_user.nickName,
                }
              };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        layer.msg("新增设备成功！", {icon:1,time:2000});

        setTimeout('reloadDataOnAdd()', 2000);

      });
}

function reloadDataOnAdd() {
    $("#modal-addUser").modal("hide");
    setTimeout('loadData()', 1000);
}


var app_edit_user = new Vue({
  el: '#modal-editUser',
  data: {
    userId:'',
    account:'',
    nickName:'',
    resetPwd:false,
    status:1,
  },
  methods:{

  }
});


function loadUserData(userId) {
  for(var i in app.userList) {
    var item = app.userList[i];
    if (item.userId == userId) {
      app_edit_user.userId = item.userId;
      app_edit_user.account = item.account;
      app_edit_user.nickName = item.nickName;
      app_edit_user.status = item.status;
      break;
    }
  }
}

function doUpdateUser() {

    if (!checkAuth()) {
      return;
    }

    var param = {"method":"admin.user.update",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{
                  "userId":app_edit_user.userId+'',
                  "account":app_edit_user.account,
                  "nickName":app_edit_user.nickName,
                  "resetPassword":app_edit_user.resetPwd?'yes':'no',
                  "status":app_edit_user.status,
                }
              };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:2000});
          return;
        }

        layer.msg("修改用户成功！", {icon:1,time:2000});

        setTimeout('reloadDataOnUpdate()', 2000);

      });
}

function reloadDataOnUpdate() {
    $("#modal-editUser").modal("hide");
    setTimeout('loadData()', 1000);
}


function getSelectedUser(userId) {

  var list = app.userList;

  for(i in list) {
    if (list[i].userId == userId) {
      return list[i];
    }
  }

  return  null;
}



function setPaginator() {

  $("#pagination").empty();

  // return;
  var obj = new Page({
      id: 'pagination',
      pageTotal: app.pageOption.pageTotal, //必填,总页数
      pageAmount: app.pageOption.pageAmount,  //每页多少条
      dataTotal: app.pageOption.dataTotal, //总共多少条数据
      curPage:app.pageOption.curPage, //初始页码,不填默认为1
      pageSize: app.pageOption.pageSize, //分页个数,不填默认为5
      showPageTotalFlag:true, //是否显示数据统计,不填默认不显示
      showSkipInputFlag:true, //是否支持跳转,不填默认不显示
      getPage: function (page) {
          //获取当前页数
         console.log("获取当前页数:" + page);
         app.pageOption.curPage = page;
         loadData();
      }
  });

  return obj;
}
