

var app = new Vue({
  el: '#app',
  data: {

  },
  methods:{

  }
});

var mapsource=1;// 1:chinaProvider;2:mapbox
var g_center=[31.34725,118.405515];
var myMap;

var mapParam = {"center":g_center,"divId":"mapId"};
if (mapsource==1) {
  loadChinaProvider(mapParam);
} else {
  loadMapbox(mapParam);
}

var marker = L.marker(g_center).addTo(myMap).bindPopup("I am a green leaf2.");
