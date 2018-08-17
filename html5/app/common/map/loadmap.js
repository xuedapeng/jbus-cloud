
function loadMapbox(param){ // {"center":g_center,"divId":"mapId"}
  myMap = L.map(param.divId).setView(param.center, 10);
  L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
  		maxZoom: 18,
  		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
  			'<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
  			'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
  		id: 'mapbox.streets'
  	}).addTo(myMap);

}


function loadChinaProvider(param){ // {"center":g_center,"divId":"mapId"}
    var zMax = 18;
    var zMin = 4;
    /**
     * 智图地图内容
     */
    var normalm1 = L.tileLayer.chinaProvider('Geoq.Normal.Map', {
        maxZoom: zMax,
        minZoom: zMin
    });
    var normalm2 = L.tileLayer.chinaProvider('Geoq.Normal.Color', {
        maxZoom: zMax,
        minZoom: zMin
    });
    var normalm3 = L.tileLayer.chinaProvider('Geoq.Normal.PurplishBlue', {
        maxZoom: zMax,
        minZoom: zMin
    });
    var normalm4 = L.tileLayer.chinaProvider('Geoq.Normal.Gray', {
        maxZoom: zMax,
        minZoom: zMin
    });
    var normalm5 = L.tileLayer.chinaProvider('Geoq.Normal.Warm', {
        maxZoom: zMax,
        minZoom: zMin
    });
    var normalm6 = L.tileLayer.chinaProvider('Geoq.Normal.Cold', {
        maxZoom: zMax,
        minZoom: zMin
    });
    /**
     * 天地图内容
     */
    var normalm = L.tileLayer.chinaProvider('TianDiTu.Normal.Map', {
          maxZoom: zMax,
          minZoom: zMin
        }),
        normala = L.tileLayer.chinaProvider('TianDiTu.Normal.Annotion', {
              maxZoom: zMax,
              minZoom: zMin
        }),
        imgm = L.tileLayer.chinaProvider('TianDiTu.Satellite.Map', {
              maxZoom: zMax,
              minZoom: zMin
        }),
        imga = L.tileLayer.chinaProvider('TianDiTu.Satellite.Annotion', {
              maxZoom: zMax,
              minZoom: zMin
        });

    var normal = L.layerGroup([normalm, normala]),
        image = L.layerGroup([imgm, imga]);
    /**
     * 谷歌
     */
    var normalMap = L.tileLayer.chinaProvider('Google.Normal.Map', {
            maxZoom: zMax,
            minZoom: zMin
        }),
        satelliteMap = L.tileLayer.chinaProvider('Google.Satellite.Map', {
              maxZoom: zMax,
              minZoom: zMin
        });
    /**
     * 高德地图
     */
    var Gaode = L.tileLayer.chinaProvider('GaoDe.Normal.Map', {
          maxZoom: zMax,
          minZoom: zMin
    });
    var Gaodimgem = L.tileLayer.chinaProvider('GaoDe.Satellite.Map', {
          maxZoom: zMax,
          minZoom: zMin
    });
    var Gaodimga = L.tileLayer.chinaProvider('GaoDe.Satellite.Annotion', {
          maxZoom: zMax,
          minZoom: zMin
    });
    var Gaodimage = L.layerGroup([Gaodimgem, Gaodimga]);



    var baseLayers = {
        // "智图地图": normalm1,
        // "智图多彩": normalm2,
        // "智图午夜蓝": normalm3,
        // "智图灰色": normalm4,
        // "智图暖色": normalm5,
        // "智图冷色": normalm6,
        // "天地图": normal,
        // "天地图影像": image,
        // "谷歌地图": normalMap,
        // "谷歌影像": satelliteMap,
        "高德地图": Gaode,
        "高德影像": Gaodimage,

    }

    myMap = L.map(param.divId, {
        center: param.center,
        zoom: 10,
        layers: [Gaode],
        zoomControl: false
    });

    L.control.layers(baseLayers, null).addTo(myMap);
    L.control.zoom({
        zoomInTitle: '放大',
        zoomOutTitle: '缩小'
    }).addTo(myMap);
  }
