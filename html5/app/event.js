

var app = new Vue({
  el: '#app',
  data: {
    eventList:[],
    deviceKey:'',
    onlyLast:true,
  },
  methods:{
    query:function(){
      loadData();
    },
    showAll:function(deviceSn){
      app.deviceKey  = deviceSn;
      app.onlyLast = false;
      loadData();
    }
  }
});


loadData();
function loadData() {

  if (!checkAuth()) {
    return;
  }

  app.eventList=[];

  var param = {"method":"monitor.event.query",
              "auth":[getStorage("appId"), getStorage("appToken")],
              "data":{"deviceKey":app.deviceKey,
                      "onlyLast":app.onlyLast?"yes":"no"
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
      makeEventList(result);

      layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function makeEventList(result, seq) {

  var eList =[];

  var seq = 0;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.deviceId = record['deviceId'];
    item.deviceSn = record['deviceSn'];
    item.deviceName = record['deviceName'];
    item.event = record['event'];
    item.memo = record['memo'];
    item.time = record['time'];
    eList[i] = item;
  }
  app.eventList = eList;
  console.log(app.eventList.length);

}
