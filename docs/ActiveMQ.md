## 简介
Apache 作品
## 安装
到官网下载最新版本：[http://activemq.apache.org/download.html](http://activemq.apache.org/download.html)

这里使用``ActiveMQ 5.15.8 Release``版本演示，下载Linux下的`apache-activemq-5.15.8-bin.tar.gz``安装包：
```powershell
wget http://mirrors.shu.edu.cn/apache//activemq/5.15.8/apache-activemq-5.15.8-bin.tar.gz
```
解压
```powershell
tar -zxf apache-activemq-5.15.8-bin.tar.gz
```
启动
```powershell
cd apache-activemq-5.15.8/bin
./activemq start
```
访问控制台：
```
http://192.168.133.129:8161
```
默认端口``8161``，默认账号密码：``admin/admin``
## 调优
使用activemq的mqtt协议过程中，存在丢包问题，这里做下简单的配置调整，首先是对tcp缓冲区的配置，需要修改``/etc/sysctl.conf``：
```
net.core.rmem_max = 8388608
net.core.wmem_max = 8388608
net.core.rmem_default = 655360
net.core.wmem_default = 655360
net.ipv4.tcp_rmem = 4096 655360 8388608 # Tcp接收缓冲区，分别是最小、默认、最大
net.ipv4.tcp_wmem = 4096 655360 8388608 # Tcp发送缓冲区，分别是最小、默认、最大
net.ipv4.tcp_mem = 8388608 8388608 8388608
```
接下来在activemq的``conf``目录下，修改``activemq.xml``配置：
```xml
<transportConnector name="mqtt"
uri="mqtt+nio://0.0.0.0:1883?maximumConnections=1000&
wireFormat.maxFrameSize=104857600&transport.ioBufferSize=1048576&
transport.socketBufferSize=4194304"/>
```
使用nio来提高网络通讯能力，提高socket和io缓冲区大小，增加处理能力。

