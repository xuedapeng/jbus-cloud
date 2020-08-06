
var DEFAULT_COVER = [
  {
    "type":"valuePtn",
    "pattern":{
              "sw":{
                "1":"开",
                "0":"关",
                "default":"未知状态" 
              }
    }
  },
  {
      "type":"dat",
      "deviceSn":"AABBCCDD",
      "sensorNo":"21",
      "field":"sw",
      "name":"电动阀A5(11)",
      "valuePtn":{
          "refptn":"sw"
      }               
  },
  {
      "type":"cmd",
      "name":"电动阀A5(11)=开",
      "deviceSn":"AABBCCDD",
      "cmd":"21:2"
  }

];

var app = new Vue({
  el: '#app',
  data: {
    pid:getQueryString("pid"),
    projectId:getQueryString("projectId"),
    codeMirror:null,
    title:"",
    sort:"",

    cmValue:JSON.stringify(DEFAULT_COVER),

    cmWidth : $( window ).width() - 80,
    cmHeight : $( window ).height() - 90,
    message:"",
    msgtype:"",
    showRemove:false
  },
  computed: {

  },
  methods:{
    format(){
      doFormat();
    },
    save(){
      doFormat();
      if (!formatJsonSyn(app.cmValue)) {
        layer.msg("json格式错误！", {icon:2,time:1000});
        return;
      }
      if(app.projectId) {
        doUpdate();
      } else {
        doAdd();
      }
    },
    remove(){
      for(var i=0;i<5;i++) {
        if(confirm("确定要删除吗？删除后将无法恢复。（"+ (i+1) + "/5）")) {

        } else {
          return;
        }
      }

      // 提醒5次后删除
      doRemove();
    }
  },
});

window.onload = function() {
  makeCodeMirror();
  if (app.projectId) {
    loadData(app.projectId);
  }
};

$(window).resize(function() {
    app.cmWidth = $( window ).width() - 80;
    app.cmHeight = $( window ).height() - 90;

    app.codeMirror.setSize(app.cmWidth,app.cmHeight);

});
  
function showRemoveBtn() {
  app.showRemove = true;
}

function hideRemoveBtn() {
  app.showRemove = false;
}

function doFormat() {
  app.msgtype="info";
  app.message="format json ...";
  setDoc2Val();
  setTimeout("setVal2Doc()", 500);

  if(app.msgtype!='ok') {
    return false;
  }

  return true;
}

function doAdd() {

  if (!checkAuth()) {
    return;
  }

  if(app.title=='' || app.sort=='') {
    layer.msg("title、sort 是必须项目！", {icon:2,time:1000});
    return;
  }

  var param = {"method":"uiconfig.project.add",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"pid":app.pid + '',
                      "title":app.title,
                      "cover":app.cmValue,
                      "sort":app.sort + ''
                    }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:5000});
        return;
      } 

      layer.msg("添加组态成功！", {icon:1,time:1000});
      app.projectId = response.projectId;
      setTimeout("change2EditMode()", 500);

    },
    function(status, ex) {
      
      layer.msg("网络不给力！"+status, {icon:2,time:5000});
    }
    
    );

}

function change2EditMode() {
  document.getElementById("11"+app.projectId).click();
  setTimeout("removePreIframe()", 500);
}

/*关闭pre iframe*/
function removePreIframe(){
	var topWindow = $(window.parent.document),
		iframe = topWindow.find('#iframe_box .show_iframe'),
		tab = topWindow.find(".acrossTab li"),
		showTab = topWindow.find(".acrossTab li.active"),
		showBox=topWindow.find('.show_iframe:visible'),
		i = showTab.index();
	// tab.eq(i-1).addClass("active");
	tab.eq(i-1).remove();
	// iframe.eq(i-1).show();	
	iframe.eq(i-1).remove();
}

function doUpdate() {

  if (!checkAuth()) {
    return;
  }

  if(app.title=='' || app.sort=='') {
    layer.msg("title、sort 是必须项目！", {icon:2,time:1000});
    return;
  }

  var param = {"method":"uiconfig.project.update",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"projectId":app.projectId + '',
                      "title":app.title,
                      "cover":app.cmValue,
                      "sort":app.sort + ''
                    }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:5000});
        return;
      } 

      layer.msg("修改组态成功！", {icon:1,time:1000});

    },
    function(status, ex) {
      
      layer.msg("网络不给力！"+status, {icon:2,time:5000});
    }
    
    );

}


function doRemove() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"uiconfig.project.delete",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"projectId":app.projectId
                    }
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:5000});
        return;
      } 

      layer.msg("删除组态成功！", {icon:1,time:3000});

      setTimeout("removeIframe()",500);

    },
    function(status, ex) {
      
      layer.msg("网络不给力！"+status, {icon:2,time:5000});
    }
    
    );

}


function loadData(projectId) {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"uiconfig.project.get",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"projectId":app.projectId}
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:3000});

        setTimeout("removeIframe()",500);
        return;
      }

      app.title = response.title;
      app.sort = response.sort;

      setCmValue(response.cover);

    },
    function(status, ex) {
      
      layer.msg("网络不给力！"+status, {icon:2,time:30000});
    }
    
    );

}

function makeCodeMirror() {
    var cmEl = document.getElementById("cover");
    var cm = CodeMirror(cmEl, 
      { 
          value: "",
          mode:"application/json",
          theme:"blackboard",
          styleActiveLine: true,
          matchBrackets: true,
          //快捷键

      });

      width = app.cmWidth;
      height = app.cmHeight;
      cm.setSize(width,height);

      app.codeMirror = cm;
      
      setTimeout("setCmOption()", 100);
      // setTimeout("setVal2Doc()", 100);
      setVal2Doc();


  }

  function setCmOption() {
    app.codeMirror.setOption("lineNumbers", true);
  }

  function setVal2Doc() {
    app.cmValue = myFormatJson(app.cmValue, errorHandle);
    setTimeout("setVal2DocImpl()", 10);
  }

  function setVal2DocImpl() {
      app.codeMirror.getDoc().setValue(app.cmValue);
  }

  function setDoc2Val() {

        app.cmValue = app.codeMirror.getDoc().getValue();
  }

  function setCmValue(result) {
      if (result) {
        app.cmValue = result;
      } else {
        app.cmValue = "";
      }

    setVal2Doc();

  }

  function formatJsonSyn(str) {
    try {
      formatJson(str);
      return true;
    } catch(err) {
     
      return false;
    }
  }

  function myFormatJson(str, errorHandle) {
    try {
      str = formatJson(str);
      successHandle();
      return str;
    } catch(err) {
      errorHandle(err);
      return str;
    }
  }

  function successHandle() {
    app.message= '正确的JSON';
    app.msgtype='ok';
  }
  function errorHandle(err) {

    // layer.msg(formatError(err),{icon:2,time:5000});

    // setDoc2Val();
    // alert(formatError(err));
    // setVal2Doc();
    app.message= formatError(err);
    app.msgtype='err';
  }

  function formatError(err) {
    var t = dateFormat(Date(), "yyyy-MM-dd HH:mm:ss") + "\n";
    var b = err.name + "\n" + (err.message?(err.message + "\n"):"") + "\n";
    return b;

  } 


  /* 当前页面高度 */
  function pageHeight() {
    return document.body.scrollHeight;
  }

  /* 当前页面宽度 */
  function pageWidth() {
    return document.body.scrollWidth;
  }