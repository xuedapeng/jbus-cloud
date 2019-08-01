import * as echarts from '../ec-canvas/echarts';

const app = getApp();

var ecOptionInit = {
  title: {
    text: '　　正在加载...',
    left: 'left'
  },
  // color: ["#37A2DA", "#67E0E3", "#9FE6B8"],
  legend: {
    data: [],
    top: 30,
    left: 'center',
    backgroundColor: 'white',
    z: 100
  },
  grid: {
    containLabel: true
  },
  
  tooltip: {
    show: true,
    trigger: 'axis', 
    snap:true,
    position: function (pos, params, dom, rect, size) {
      // 鼠标在左侧时 tooltip 显示到右侧，鼠标在右侧时 tooltip 显示到左侧。
      var obj = { top: pos[1]-150 };
      // obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 5;
      obj['left'] = (pos[0] - ([0, 100][+(pos[0] > size.viewSize[0] / 2)]));
      return obj;
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [
      // '周一', '周二', '周三', '周四', '周五', '周六', '周日'
      ],
    // show: false
  },
  yAxis: {
    x: 'center',
    type: 'value',
    splitLine: {
      lineStyle: {
        type: 'dashed'
      }
    }
    // show: false
  },
  series: [
  //   {
  //   type: 'line',
  //   smooth: true,
  //   data: [18, 36, 65, 30, 78, 40, 33]
  // }, {
  //   type: 'line',
  //   smooth: true,
  //   data: [12, 50, 51, 35, 70, 30, 20]
  // }
  ]
};

var ecOption = ecOptionInit
var chart;
function initChart(canvas, width, height) {
  
  chart = echarts.init(canvas, null, {
    width: width,
    height: height
  });
  canvas.setChart(chart);


  chart.setOption(ecOption);
  return chart;
}

Page({
  
  data: {
    ec: {
      onInit: initChart 
    },
    deviceId:'',
    sensorNo: '',

  },
  onLoad(q) {
    this.data.deviceId = q.deviceId;
    this.data.sensorNo = q.sno; 
    this.loadData();
  },

  onUnload() {
    this.clearGraph();
  },

  loadData() {

    var page = this;

    var toTime = dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"); 
    var fromDate = new Date(Date.parse(new Date()) - 1000 * 60 * 60 * 24);
    var fromTime = dateFormat(fromDate, "yyyy-MM-dd HH:mm:ss");
    
    wx.request({
      url: app.globalData.url,
      data: {
        "method": "hydrograph.data.query",
        "auth": app.globalData.auth(),
        "data": { deviceId: page.data.deviceId + '', sensorNo: page.data.sensorNo + '', fromTime: fromTime, toTime: toTime}
      },
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success(res) {
        console.log(res.data);
        page.refreshGraph(res.data);
      }
    })
  },

  clearGraph() {
    console.log("clearGraph");
    ecOption.title.text = '　　正在加载...';
    ecOption.xAxis.data = [];
    ecOption.series = [];
    ecOption.legend.data = [];

    chart.setOption(ecOption);
  },

  refreshGraph(data) {

    var deviceName = data.deviceName;
    var deviceSn = data.deviceSn;
    var sensorName = data.sensorName;
    var sensorNo = data.sensorNo;
    ecOption.title.text = "　　" + deviceName + '(' + deviceSn + ')' + '　' + sensorName + '(' + sensorNo + ')';

    ecOption.xAxis.data = data.result.time;
    for (var i in ecOption.xAxis.data) {
      ecOption.xAxis.data[i] = ecOption.xAxis.data[i].substring(11);
    }

    ecOption.series=[];
    ecOption.legend.data = [];
    for(let k in data.fieldStyle) {
      var sData = data.result[k];
      var name = data.fieldStyle[k].display + '(' + data.fieldStyle[k].unit + ')'
      ecOption.legend.data.push(name);
      ecOption.series.push({
        name: name,
        type: 'line',
        smooth: true,
        data: sData
      });
    }

    chart.setOption(ecOption);
  }




});


function dateFormat(time, format) {
  var t = new Date(time);
  var tf = function (i) { return (i < 10 ? '0' : '') + i };
  return format.replace(/yyyy|MM|dd|HH|mm|ss|ms/g, function (a) {
    switch (a) {
      case 'yyyy':
        return tf(t.getFullYear());
        break;
      case 'MM':
        return tf(t.getMonth() + 1);
        break;
      case 'mm':
        return tf(t.getMinutes());
        break;
      case 'dd':
        return tf(t.getDate());
        break;
      case 'HH':
        return tf(t.getHours());
        break;
      case 'ss':
        return tf(t.getSeconds());
        break;
      case 'ms':
        return tf(t.getMilliseconds());
        break;
    }
  })
}