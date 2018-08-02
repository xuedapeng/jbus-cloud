

function moveTo(url) {
  window.location.href = url;
}

function moveToLogin() {
  moveTo("app/login.html");
}

function checkAuth() {

  		if (!localStorage.appId || !localStorage.appToken) {

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
