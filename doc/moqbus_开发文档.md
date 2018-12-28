# Moqbus 开发文档

## 一、快速体验
1. 注册帐号

    * 访问moqbus cloud平台，[注册moqbus帐号][id_reg]。

[id_reg]: http://cloud.moqbus.com/app/reg.html
    
    * 注册成功后，使用用户名／密码 [登录moqbus平台][id_login]
    
[id_login]: http://cloud.moqbus.com/app/login.html
    
* 添加设备

    * 在“设备管理”中，点击“新增”，添加设备； 
    * 设备添加成功后，在设备列表页面，可以看到刚才添加的设备；
    * 在列表中，点击“设备编号”链接，可以查看改设备的通讯密码。
    
    
* 使用模拟器
    * 下载设备模拟器 [devmock.zip][id_download_devmock]；
    
[id_download_devmock]: http://moqbus.com/download/devmock.zip

    * 解压缩zip, 打开命令行，进入解压后的目录。
    
            cd [下载目录]/devmock
    
    * 运行模拟器
            
            java -jar devmock.jar console d tc.moqbus.com 2883 [设备编号] [通讯密码]
            
            INPUT DATA TO SERVER>

* 实时控制

    * 在moqbus cloud平台中打开“实时控制”页面，可以看到刚才添加的设备已经是在线状态（绿点表示在线）， 点击“连接”按钮，与设备建立连接；
    
    * 在“命令参数”输入框中输入以下命令，并按下“发送”按钮：
        
            01 03 00 00 00 02 C4 0B
      
      
    * 查看模拟器窗口，可以看到设备已经收到了指令：
    
            [2018-11-01 10:18:51.051] recieve: F4****6E, 01 03 00 00 00 02 c4 0b 

            INPUT DATA TO SERVER>
            
    * 在模拟器窗口中，设备应答数据（模拟）：
            
            INPUT DATA TO SERVER>01 03 04 02 92 ff 9b 5a 3d
            [2018-11-01 10:46:31.031] send data:01 03 04 02 92 ff 9b 5a 3d 
            
    * 查看“实时控制”页面，可以看到设备的应答数据（No2）：
    
            No.	Time	            Type	    Command/Data     
            2	11-01 10:46:31.309	received	01 03 04 02 92 FF 9B 5A 3D
            1	11-01 10:18:51.373	sent	    01 03 00 00 00 02 C4 0B

## 二、设备接入
1. 选择网关设备

    支持GPRS和注册包的DTU设备，都可以接入moqbus。
    
    推荐：
        
      * “有人” [USR-GPRS232-730][id_usr_dtu]
   [id_usr_dtu]:https://detail.tmall.com/item.htm?id=525239600301
   
      * “塔石” [TAS-GPRS-350][id_tastek_dtu]
   [id_tastek_dtu]:https://item.taobao.com/item.htm?id=577204480991

*  配置网关

     * TCP服务器设置：
     
             host: tc.moqbus.com
             port: 2883
         
     * 注册包设置：
         
         发送方式：连接时发送注册包；
         
         格式：
                 
                 REG:[设备编号],[通讯密码];
               
         例：
         
                 REG:F4D5B96E,56F6D85F52;
              
*  收发测试
        
     * 网关设备通电连网后，moqbus平台的“实时控制”页面，如果看到设备名称后面显示绿点，说明网关已成功接入moqbus平台。
     
     * 点击“连接”按钮，在“命令参数”输入框中输入指令，就可以向网关发送数据。
     
     * 将网关的RS232/485端口连接到PC的串口，可以通过串口调试助手等工具，查看指令数据并向平台发送应答数据。
     
     * 调试完成后，就可以将RS232/485端口连接到传感器，参考传感器的指令说明向传感器发送指令，并接收传感器的应答。

## 三、应用开发

1. 查看开发密钥

    登录moqbus平台后，点击右上角下拉菜单的“帐号”，查看令牌信息：
    
    * secretId， secretKey： 用于JSON-RPC调用
    * mqttUser， mqttPassword：用于mqtt服务器连接


*  获取设备列表

    url： 
            
            http://cloud.moqbus.com/open/api/
            
    param：
           
            {
                "method":"device.list",
                "auth":["#secretId#", "#secretKey#"]
            }
    
    以上#secretId#替换为你自己的secretId, 其它同样。
    
    
*  获取设备在线状态 
 
    url： 
            
            http://cloud.moqbus.com/open/api/
            
    param：
           
            {
                "method":"realtime.device.online.query",
                "auth":[#secretId#, #secretKey#],
                "data":{
                        "deviceIds":["#设备编号1#", "#设备编号2#"]
                       }
            }
    

*  连接mqtt
    * js：
        
        首先导入paho-mqtt js客户端：
        
            <script src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.min.js"><script>
            
        js代码：
        
               // 创建对象
               var mqttClient = new Paho.MQTT.Client("mqtt.moqbus.com", 8083);
           
               // 收到消息时的回调函数
               mqttClient.onMessageArrived = function(message){
                   console.log(message);
               };
           
               // 连接到服务器
               mqttClient.connect({
                 onSuccess:function(){
                     console.log("连接成功");
                 },
                 userName:#mqttUser#,
                 password:#mqttPassword#
               });
           
     * java：
         
         在maven工程中导入paho mqtt项目：
             
              <dependency>
  	            <groupId>org.eclipse.paho</groupId>
  	            <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
  	            <version>1.1.1</version>
              </dependency>           
        
        java代码：
        
            MemoryPersistence persistence = new MemoryPersistence(); 
            MqttClient mqttClient = new MqttClient(_mqttBroker, GuidHelper.genUUID(), persistence);  
            mqttClient.setCallback(new MqttCallback(){

			    @Override
			    public void connectionLost(Throwable cause) {			  			    
			    }

			    @Override
			    public void deliveryComplete(IMqttDeliveryToken token) {				
			    }

			    @Override
			    public void messageArrived(String topic, MqttMessage message) throws Exception {
				
				    // 处理接收到的数据
		    		
			    }
        	
            });  
            
            MqttConnectOptions options = new MqttConnectOptions(); 
		    options.setUserName(#mqttUser#);  
		    options.setPassword(#mqttPassword#.toCharArray());  
        
            mqttClient.connect(options);  

*  订阅设备状态
        
      * js
           
           var topic= "TC/STS/#设备编号#";
           mqttClient.subscribe(topic);
           
           
      * java
      
          String topic = "TC/STS/#设备编号#";
          mqttClient.subscribe(topic);
          
          
      设备在线状态改变时，mqttClient会接受到如下消息：
          
          {
              "onlineCount":"0",
              "tcpClient":"60.***.19.***:58524",
              "time":"2018-11-01 14:34:50",                                    
              "sessionId":"3df087****394ecf900e7ac4fcf0511e",
              "event":"off",
              "deviceSn":"F4****6E"
          }

* 接收设备上报数据
        
     * js
         
          // 收到消息时的回调函数
          mqttClient.onMessageArrived = function(message){
             // 在此处理设备上报的数据，如更新画面图表的数据源等
          };
          
      
     * java
      
           @Override
		   public void messageArrived(String topic, MqttMessage message) throws Exception {
				// 在此处理设备上报的数据，如保存到数据库等
		    } 

    

* 向设备发送指令
    
    * js
        
            var message = new Paho.MQTT.Message([0x01,0x03,0x00,0x00,0x00,0x02,0xc4,0x0b]);
            message.destinationName = "TC/CMD/#设备编号#";
            mqttClient.send(message);

    * java
    
            String topic = "TC/CMD/#设备编号#";
            byte[] data = [0x01,0x03,0x00,0x00,0x00,0x02,0xc4,0x0b];
            
            MqttMessage mm = new MqttMessage(data);
            mqttClient.publish(topic, mm);
            
            
            

## And more?

 * 开发相关问题，可加入QQ群交流：941036927
 
 <script src="qs.js"></script>