
var DEFAULT_SCRIPTS = {
  "scriptText":  
      "function decodeDat(bytes) { \n \t return null; \n}",
  "resultSchema":  
      formatJson(JSON.stringify(
          {"1":{
              "type":"metric",
              "field":{
                      "wd":{"display":"温度", "format":"#.0", "unit":"℃"}, 
                      "sd":{"display":"湿度", "format":"#.0", "unit":"%"}
              }
            }
          }
      )),
  "sampleCases":
      formatJson(JSON.stringify(
        [{
        "input":"01 03 00 00 00 02 c4 0b",
          "output":{"sno":1,"data":{"wd":23.5,"sd":28}}

        }]
      ))
}

var app = new Vue({
  el: '#app',
  data: {
    deviceId:getQueryString("deviceId"),
    codeMirror:{
      "scriptText":null,
      "resultSchema":null,
      "sampleCases":null,
      "debugInfo":null
    },

    cmValues:{
      "scriptText":DEFAULT_SCRIPTS["scriptText"],
      "resultSchema":DEFAULT_SCRIPTS["resultSchema"],
      "sampleCases":DEFAULT_SCRIPTS["sampleCases"],
      "debugInfo":""
    },
    tabIndex: 0,

    cmWidth : $( window ).width() - 80,
    cmHeight : $( window ).height() - 90,
    showBtn:true,
    isOverlay:false,
    decodeStatus:'',
  },
  computed: {
    runEnable: function () {
      return this.tabIndex < 3 ;
    },
    fmtEnable: function () {
      return this.tabIndex == 1 || this.tabIndex == 2;
    },
    clearEnable: function () {
      return this.tabIndex == 3;
    },
    saveEnable:function () {
      return this.tabIndex < 3 ;
    },
    startEnable:function () {
      return this.tabIndex == 0 && this.decodeStatus=='stop';
    },
    stopEnable:function () {
      return this.tabIndex == 0&& this.decodeStatus=='start';
    },
  },
  methods:{
    onTabScript:function(){
      app.tabIndex = 0;
      
    },
    onTabSchema:function(){
      app.tabIndex = 1;
      if (app.codeMirror.resultSchema == null) {
        makeCodeMirror("resultSchema", null, null);
      }
    },
    onTabSample:function(){

      app.tabIndex = 2;
      if (app.codeMirror.sampleCases == null) {
        makeCodeMirror("sampleCases", null, null);
      }
    },
    onTabDebug:function(){
      app.tabIndex = 3;
      if (app.codeMirror.debugInfo == null) {
        makeCodeMirror("debugInfo", null, null);
      }
      setVal2Doc();
    },
    run:function() {
      if (doRun()) {
        layer.msg("测试用例运行通过！", {icon:1,time:3000});
      }
    },
    format:function() {
      doFormat();
    },
    clear:function() {

      setDoc2Val();
      app.cmValues["debugInfo"] = "";
      setVal2Doc();

    },
    save:function() {
      if (!doRun()) {
        return;
      }
      doSave();
    },
    start:function() {
      // todo:
    },
    stop:function() {
      // todo:
    },
    doNothing:function(){

    }
  },
});

window.onload = function() {
  makeTab();
  makeCodeMirror("scriptText", null, null);
  loadData();
};

$(window).resize(function() {
    app.cmWidth = $( window ).width() - 80;
    app.cmHeight = $( window ).height() - 90;

    for(i in app.codeMirror) {
      if (app.codeMirror[i]) {
        app.codeMirror[i].setSize(app.cmWidth,app.cmHeight);
      }
    }

    if (app.isOverlay) {
      showOverlay();
    }
});
  

function loadData(silent) {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"codec.datDecode.get",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"deviceId":app.deviceId}
              };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        showOverlay();
        layer.msg(response.msg,{icon:2,time:30000});
        return;
      } else if(response.status == 11) {

        layer.msg("解码器尚未设置！", {icon:1,time:1000});
        return;
      }

      var result = response.result;

      setCmValues(result);
      app.decodeStatus = (result.status == 1?'start':'stop');
      if (!silent) {
        layer.msg("解码器查询成功！", {icon:1,time:1000});
      }

    },
    function(status, ex) {
      showOverlay();
      layer.msg("网络不给力！"+status, {icon:2,time:30000});
    }
    
    );

}

function makeTab() {

  $("#tab_demo2").Huitab({
		tabEvent:"mousemove",
		index:0
	});
}



function makeCodeMirror(cmId, w, h) {
    var cmEl = document.getElementById(cmId);
    var cm = CodeMirror(cmEl, 
      { 
          value: "",
          mode:(cmId=="scriptText"||cmId=="debugInfo")?"javascript":"application/json",
          theme:"blackboard",
          styleActiveLine: true,
          matchBrackets: true,
          //快捷键

      });

      var width = w;
      var height = h;
      if (!width) {
        width = app.cmWidth;
      }
      if (!height) {
        height = app.cmHeight;
      }
      cm.setSize(width,height);

      app.codeMirror[cmId] = cm;
      
      setTimeout("setCmOption()", 100);
      // setTimeout("setVal2Doc()", 100);
      setVal2Doc();


  }

  function setCmOption() {
    for(i in app.codeMirror) {
      if (app.codeMirror[i]) {
        app.codeMirror[i].setOption("lineNumbers", true);
        if (i == 'debugInfo') {
          app.codeMirror[i].setOption("readOnly", true);
        }

      }
    }
  }

  function setVal2Doc() {
    setTimeout("setVal2DocImpl()", 10);
  }

  function setVal2DocImpl() {
    for(i in app.codeMirror) {
      if (app.codeMirror[i]) {
        app.codeMirror[i].getDoc().setValue(app.cmValues[i]);
      }
    }
  }

  function setDoc2Val() {

    for(i in app.codeMirror) {
      if (app.codeMirror[i]) {
        app.cmValues[i] = app.codeMirror[i].getDoc().getValue();
      }
    }
  }

  function setCmValues(result) {
    for(i in app.cmValues) {
      if (result[i]) {
        app.cmValues[i] = result[i];
      } else {
        app.cmValues[i] = "";
      }
    }

    setVal2Doc();

  }

  function myFormatJson(str, tab, errorHandle) {
    try {
      return formatJson(str);
    } catch(err) {
      errorHandle(tab, err);
      return str;
    }
  }

  function errorHandle(tab, err) {

    layer.msg(err.name,{icon:2,time:3000});

    setDoc2Val();
    app.cmValues["debugInfo"] = formatError(tab, err);
    setVal2Doc();
  }

  function formatError(tab, err) {
    var t = dateFormat(Date(), "yyyy-MM-dd HH:mm:ss") + " (" + tab + ")\n";
    var b = err.name + "\n" + (err.message?(err.message + "\n"):"") + "\n";
    return t + b + app.cmValues["debugInfo"];

  } 

  function doFormat() {

      setDoc2Val();
      if (app.tabIndex == 1) {
        var v = app.cmValues["resultSchema"];
        if (v && v.length > 0) {
          app.cmValues["resultSchema"] = myFormatJson(v, "返回值模式", errorHandle);
        }
      }
      
      if (app.tabIndex == 2) {
        v = app.cmValues["sampleCases"];
        if (v && v.length > 0) {
          app.cmValues["sampleCases"] = myFormatJson(v, "测试用例", errorHandle);
        }
      }
      setVal2Doc();
  }

  function doRun() {
    setDoc2Val();
    // checkResultSchema
    var err = DatDecodeUtil.checkResultSchema(app.cmValues["resultSchema"]);
    if (err != null) {
      errorHandle("返回值模式", err);
      return false;
    }

    // checkSampleCases
    var err = DatDecodeUtil.checkSampleCases(app.cmValues["sampleCases"], app.cmValues["resultSchema"]);
    if (err != null) {
      errorHandle("测试用例", err);
      return false;
    }

    // run
    var err = DatDecodeUtil.runSampleCases(app.cmValues["sampleCases"], app.cmValues["scriptText"]);
    if (err != null) {
      errorHandle("运行结果", err);
      return false;
    } 
    return true;

  }

  function doSave() {

    if (!checkAuth()) {
      return;
    }
    setDoc2Val();
    var param = {"method":"codec.datDecode.save",
                "auth":[getStorage("appId"), getStorage("appToken")],
                "data":{"deviceId":app.deviceId,
                        "scriptText": app.cmValues["scriptText"],
                        "resultSchema": app.cmValues["resultSchema"],
                        "sampleCases": app.cmValues["sampleCases"]
                      }
                };

    ajaxPost(G_RPC_URL, param,
      function(response){

        if (response.status < 0) {
          layer.msg(response.msg,{icon:2,time:1000});
          errorHandle("保存操作", 
                {name:response.msg, message:response.exception});
          return;
        }

        layer.msg(response.msg,{icon:1,time:1000});

        loadData(true);

      }
    
    );
  }

    /* 显示遮罩层 */
  function showOverlay() {
    app.isOverlay = true;
    $("#overlay").height(pageHeight());
    $("#overlay").width(pageWidth());

    // fadeTo第一个参数为速度，第二个为透明度
    // 多重方式控制透明度，保证兼容性，但也带来修改麻烦的问题
    $("#overlay").fadeTo(200, 0.5);
  }

  /* 隐藏覆盖层 */
  function hideOverlay() {
    $("#overlay").fadeOut(200);
  }

  /* 当前页面高度 */
  function pageHeight() {
    return document.body.scrollHeight;
  }

  /* 当前页面宽度 */
  function pageWidth() {
    return document.body.scrollWidth;
  }