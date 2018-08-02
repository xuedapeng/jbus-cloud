

var app = new Vue({
  el: '#app',
  data: {
    deviceList:[]
  },
  methods:{

  }
});


loadData();
function loadData() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"device.list",
              "auth":[localStorage.appId, localStorage.appToken]};

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;
      makeDeviceList(result);

      layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function makeDeviceList(result, seq) {

  var dList =[];

  var seq = 0;
  for(i in result) {
    var record = result[i];
    seq++;
    var item={};
    item.seq = seq;
    item.deviceSn = record['deviceSn'];
    item.deviceName = record['deviceName'];
    item.crcMode = record['crcMode']==1?"CRC16_MODBUS":"";
    item.memo = record['memo']?record['memo']:"";
    dList[i] = item;
  }
  app.deviceList = dList;
  console.log(app.deviceList.length);

}
