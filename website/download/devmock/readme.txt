
命令参数：
1）运行方式：console
2）模拟对象：d
3）透传服务地址：tc.moqbus.com
4）透传服务端口：2883
5）设备编号：在moqbus cloud 设备管理列表中查看
6）通讯密码：在moqbus cloud 设备管理列表中查看

例：
java -jar devmock.jar console d tc.moqbus.com 2883 F4D5B06E 56F6D05F52
java -jar devmock.jar console d tc.moqbus.com 2883 -seq REG:F4D5B06E,56F6D05F52
java -jar devmock.jar console d tc.moqbus.com 2883 -noreg



