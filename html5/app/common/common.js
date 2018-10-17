

function moveTo(url) {
  window.location.href = url;
}

function moveToLogin() {
  moveTo("app/login.html");
}

function getStorage(key) {
  return localStorage[G_APP_UUID + "-" + key];
}

function setStorage(key, value) {
  localStorage[G_APP_UUID + "-" + key] = value;
}

function clearStorage() {
  localStorage.clear();
}

function checkAuth() {

  		if (!getStorage("appId") || !getStorage("appToken")) {

          layer.msg("登录信息已失效，请重新登录！",{icon:0,time:2000});
          return false;
  		}

      return true;
}

// Post
function ajaxPost(url, param, callback){

    var data = param;

    var dataString = JSON.stringify(data);

    // サーバにsubmit
    $.ajax({

          contentType: "application/json; charset=utf-8",
          type    : "POST",
          url     : url,
          data    : dataString,
          dataType: "json",
          timeout : 12000,
          success : function(response, ex){
            callback(response);
          },

          error   : function(request, status, ex){
            var err = "ajaxPost error!\t(status:"+status+", exception:" + ex + ")";
            layer.msg("网络不给力！"+status, {icon:1,time:1000});
            console.log(err);
          }
    });

  };

  function guid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
  }

  function dateFormat(time, format){
      var t = new Date(time);
      var tf = function(i){return (i < 10 ? '0' : '') + i};
      return format.replace(/yyyy|MM|dd|HH|mm|ss|ms/g, function(a){
          switch(a){
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


/* Convert a byte to string */
function byte2hexStr(byte)
{
  var hexByteMap = "0123456789ABCDEF";
  var str = "";
  str += hexByteMap.charAt(byte >> 4);
  str += hexByteMap.charAt(byte & 0x0f);
  return str;
}


/* Convert byte arry to HEX string */
function byteArray2hexStr(byteArray)
{
  var str = "";
  for (var i=0; i<(byteArray.length-1); i++)
  {
    str += byte2hexStr(byteArray[i]);
    str += " ";
  }
  str += byte2hexStr(byteArray[i]);
  return str;
}


function hexStringToBytes(hexString) {
    	if (hexString.length==0) {
            return [];
        }
        hexString = hexString.toUpperCase().replaceAll(" ", "");
        var length = hexString.length / 2;
        hexChars = hexString.split('');
        var d = new Uint8Array(length);
        for (var i = 0; i < length; i++) {
            var pos = i * 2;
            d[i] =  (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
}

function charToByte(c) {
    return "0123456789ABCDEF".indexOf(c);
}


function bytesToBase64( bytes ) {
    var binary = '';
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode( bytes[ i ] );
    }
    return window.btoa( binary );
}

String.prototype.replaceAll = function(s1,s2){
  return this.replace(new RegExp(s1,"gm"),s2);
}


function getQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  decodeURI(r[2]); return null;
}

function isNumber(obj) {
    return !isNaN(obj);
}
