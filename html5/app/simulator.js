

window.onload = function() {
  init();
}

var app = new Vue({
  el: '#app',
  data: {
    dataMessage:"",
    deviceSn:"",
    devicePwd:"",
    wsClient:null,
    messageList:[],
  },
  methods:{
    connect:function(){

      if(app.wsClient) {
        app.disconnect();
      } else {
        connectWs();
      }
    },

    disconnect:function(){

      if (app.wsClient) {
        app.wsClient.close();
      }
    },
    
    send:function(){

      if (app.wsClient) {
        sendMessage();
      } else {
        layer.msg("连接已断开！", {icon:1,time:1000});
      }
    },
    
    clearList:function() {
      app.messageList = [];
      g_seq = 1;
    }
  }
});

function init() {
  
}

function connectWs() {
  app.deviceSn = app.deviceSn.trim();
  app.devicePwd = app.devicePwd.trim();
  if(app.deviceSn.length == 0 || app.devicePwd.length == 0) {
    
    layer.msg("请输入设备编号和通讯密码！", {icon:1,time:1000});
    return;
  }

  var target = G_TC_WS_URL;
  var ws = null;
  var reginfo = stringToByte("REG:" + app.deviceSn + "," + app.devicePwd + ";");
  // reginfo = hexStringToBytes("52 45 47 3A 42 4A 57 34 30 31 2C 61 3B");
  reginfo = hexStringToBytes(byteArray2hexStr(reginfo));
    	
  if ('WebSocket' in window) {
      ws = new WebSocket(target);
  } else if ('MozWebSocket' in window) {
      ws = new MozWebSocket(target);
  } else {
      alert('WebSocket is not supported by this browser.');
  }

  ws.onopen = function(obj){  
      console.info('ws open') ;
      console.info(obj) ;
      console.info('send:' + reginfo);
      ws.send(reginfo);
  } ;
  
  ws.onclose = function (obj) {
      console.info('ws close') ;
      console.info(obj) ;

      app.wsClient = null;
      app.dataMessage = "";
  } ;
  ws.onmessage = function(evt){
    console.log("recieve message");
    console.log(evt.data);
    var reader = new FileReader();
    reader.readAsArrayBuffer(evt.data);
    reader.onload = function(rdevt){  
      if(rdevt.target.readyState == FileReader.DONE){  
        var arr = new Uint8Array(rdevt.target.result);
        var hexStr = byteArray2hexStr(arr);
        addMessage("", hexStr , "received");
        console.log("ws recieve:" + hexStr);
        
      }
    }

  } ;

  ws.onerror = function(evt){
    console.log(evt);
    console.log("WebSocketError!");
    app.wsClient = null;
    // setTimeout("connectWs()", 1000);
    // console.log("ws reconnect...");
  };

  app.wsClient = ws;
}


var g_seq = 1;
var g_list_size = 500;
function addMessage(parsed, source, type) {

  var item = {};
  item.seq = g_seq++;
  item.time = dateFormat(new Date(),"MM-dd HH:mm:ss.ms");
  item.type = type;
  item.parsedData =  parsed;
  item.sourceData = source;
  item.image = [];
  app.messageList.push(item);
  if (app.messageList.length > g_list_size) {
    app.messageList.splice(0, app.messageList.length-g_list_size);
  }

  var currentIdx = app.messageList.length-1;
}

function sendMessage() {
  app.dataMessage = app.dataMessage.trim();
  if (app.dataMessage.length==0) {
    layer.msg("请输入设备上报数据！", {icon:1,time:1000});
    return;
  }

  var bytes = hexStringToBytes(app.dataMessage);
  app.wsClient.send(bytes);
  addMessage("", byteArray2hexStr(bytes), "sent");

}
