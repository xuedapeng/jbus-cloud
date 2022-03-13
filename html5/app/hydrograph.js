

var app = new Vue({
  el: '#app',
  data: {
    dataList:[],
    deviceId:getQueryString("deviceId"),
    sensorNo:getQueryString("sensorNo"),
    fromTime:getQueryString("fromTime"),
    toTime:getQueryString("toTime"),
    deviceSn:getQueryString("deviceSn"),
    deviceName:'',
    sensorName:'',
    fieldStyle:{},
    showTitle:getQueryString("showTitle")=='no'?'none':'block',
    field:getQueryString("field")?getQueryString("field"):'all',
    graphHeight:getQueryString("height")?getQueryString("height"):'300',
    qAppId : getQueryString("appId"),
    qAppToken : getQueryString("appToken")

  },
  methods:{
    query:function(){
      loadData();
    },
  }
});


loadData();
function loadData() {

  if (!app.qAppId || !app.qAppToken) {
    if(!checkAuth()) {
      return;
    }
  }

  // alert(app.deviceId);
  console.log("app.deviceId=" + app.deviceId );

  if (!app.toTime) {
    app.toTime = dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss");
  }

  if (!app.fromTime) {
    // 24小时前
    var  fromDate = new Date(Date.parse(new Date()) - 1000*60*60*24);
    app.fromTime = dateFormat(fromDate, "yyyy-MM-dd HH:mm:ss");
  }

  app.dataList=[];

  var param = {"method":"hydrograph.data.query",
              "auth":[app.qAppId?app.qAppId:getStorage("appId"), app.qAppToken?app.qAppToken:getStorage("appToken")],
              "data":{"deviceId":app.deviceId,
                      "deviceSn":app.deviceSn,
                      "sensorNo":app.sensorNo,
                      "fromTime":app.fromTime,
                      "toTime":app.toTime,
                      }
            };

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      app.deviceSn = response.deviceSn;
      app.deviceName = response.deviceName;
      app.sensorName = response.sensorName;
      app.fieldStyle = response.fieldStyle;

      var result = response.result;
      makeDataList(result);

      // layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function makeDataList(result) {

  var dList =[];
  for(k in app.fieldStyle) {
    var field = app.fieldStyle[k];
    var option = {
        id:k,
        title: {
            text: field['display'] + '(' + field['unit'] + ')'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            // data:[field['display']]
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                // saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: makeShortTime(result['time'])
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                // name:field['display'],
                type:'line',
                stack: field['unit'],
                data:result[k]
            }
        ]
    };

    dList.push(option);

  }
  app.dataList = dList;
  console.log(app.dataList.length);

  setTimeout("makeGraph()", 1000);
}

function makeGraph() {
  for (var i in app.dataList) {
    // 基于准备好的dom，初始化echarts实例
    var option = app.dataList[i];
    // alert(document.getElementById('graph_'+option.id));
    var myChart = echarts.init(document.getElementById('graph_'+option.id));

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);

  }

  setParentFrame();
}

function setParentFrame() {
  if (window.parent && window.parent.setGraphCount) {
    window.parent.setGraphCount(app.dataList.length);
  }
}

function makeShortTime(times) {
  var nt = new Date();
  var y = ""+nt.getFullYear();
  var m = ""+(nt.getMonth()+1);
  var d = ""+nt.getDate();

  if (m<10) {
    m = "0"+m
  }
  if (d<10) {
    d = "0"+d
  }

  console.log(y + "," + m + "," + d);
  
  for(var i in times) {
    var t = times[i];
    if (y==t.substr(0,4)) {
      times[i] = times[i].substr(5);

      if (m == t.substr(5,2)) {
        times[i] = times[i].substr(3);

        if (d == t.substr(8,2)) {
          times[i] = times[i].substr(3);
        }
      }
    } 
  }
  return times;
}
