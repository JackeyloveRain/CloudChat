## jar包运行方式

##### 第一步

连接校园网BUAA-WIFI

##### 第二步

打开mysql，建立数据库，导入cloudchat.sql文件

jar包内打包的代码设定了数据库信息为

> 数据库名称：cloudchat
>
> 用户名：root
>
> 密码：111111
>
> IP为localhost:3306

##### 第三步

Terminal运行CloudChatServer.jar（ 双击没有任何提示，看着很不爽，建议Terminal）

##### 第四步

双击运行CloudChatClient.jar





## 代码入口说明

服务器入口 src/main/java/server/server/MainServer.java

客户端入口 src/main/java/client/MainStart.java

