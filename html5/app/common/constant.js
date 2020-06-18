var G_IS_HTTPS = 'https:' == document.location.protocol ? true : false;

var G_APP_UUID = "jbus-cloud-B7952D";

var G_RPC_URL = "http://localhost:8080/jbus-cloud/api/";

// var G_RPC_URL = ((G_IS_HTTPS?"https":"http") + "://cloud.moqbus.com/open/api/");

var G_MQTT_HOST = "cloud.moqbus.com";
var G_MQTT_PORT = (G_IS_HTTPS?"443":"80");

// var G_TC_WS_URL = "ws://localhost:2885";
var G_TC_WS_URL = (G_IS_HTTPS?"wss://cloud.moqbus.com:443/tc":"ws://cloud.moqbus.com/tc");

var G_LABEL_APP_TITLE = "Moqbus Cloud";
var G_LABEL_APP_VER = "v0.1";
