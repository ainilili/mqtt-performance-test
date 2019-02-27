## 简介
采用``golang``语言开发，项目地址[https://github.com/emitter-io/emitter](https://github.com/emitter-io/emitter)
## 部署
emitter可以使用docker非常轻便的部署：
```powershell
docker run -d --name emitter -p 8080:8080 --privileged --restart=unless-stopped emitter/server
```
直接通过上述指令部署会失败，emitter启动设计原理是通过每次生成的``license/secret``来进行使用中的验证，这样可以防止外来ip的恶意操作，我们可以通过运行一下指令来获取``license``和``secret``：
```powershell
docker run -it emitter/server
```
> 抄自作者原话：
>  - @problem Recovering secret key? 
>  - @answser you probably can just run ``docker run -it emitter/server``

不出意外，shell下会打印出以下几行文字：
```powershell
2019/01/27 23:50:49 [service] unable to find a license, make sure 'license' value is set in the config file or EMITTER_LICENSE environment variable
2019/01/27 23:50:49 [service] generated new license: bGKMGcG_6rqSMJ_AyymVZaphwUUt_Bm6AAAAAAAAAAI
2019/01/27 23:50:49 [service] generated new secret key: vF3pqMJ9XZf3OUiB2R28vL73ljddgqQj
```
保留后两行信息，然后在docker启动行增加``EMITTER_LICENSE``参数，value为license：
```powsershell
sudo docker run -d --name emitter -p 8080:8080 --privileged --restart=unless-stopped -e EMITTER_LICENSE=bGKMGcG_6rqSMJ_AyymVZaphwUUt_Bm6AAAAAAAAAAI emitter/server
```
执行完毕之后，查看镜像，STATUS为Up代表成功启动：
```powershell
[root@localhost /]# docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                                        NAMES
be1c13caf6df        emitter/server      "./emitter"         57 minutes ago      Up 57 minutes       4000/tcp, 8443/tcp, 0.0.0.0:8080->8080/tcp   emitter
```
接下来，我们需要申请操作所需要的key，确认启动成功之后我们通过浏览器访问``keygen``页面：
```
http://ip:port/keygen
```
界面会有三个值需要输入：
 - **Secret Key**：我们保存的secret，在申请key的时候需要用到它
 - **Target Channel**：发布订阅的通道名称
 - **Time-To-Live**：有效时间

下方还会有六个选项，表示生成的key有哪些操作权限：
 - **Read**：订阅
 - **Write**：发布
 - **Store**：写入持久化消息
 - **Load**：读取持久化消息
 - **Presence**：业务查询
 - **Extending**：扩展私有子通道

之后点击``Generate Key``按钮生成key，通过这个key我们就可以来使用``emitter``了！
## 使用
这是来自于java语言开发的demo，依赖于``mqtt-client``库，maven直接依赖即可：
```xml
<dependency>
  <groupId>org.fusesource.mqtt-client</groupId>
  <artifactId>mqtt-client</artifactId>
  <version>1.12</version>
</dependency>
```
demo代码：
```java
//keygen生成的key
String key = "I4Ypn8JoMSDECUxUm746DOxo_0bKHyXJ";
//通道
String channel = "nico/";
//组装topic
String topic = key + "/" + channel;

//连接emitter server
MQTT mqtt = new MQTT();
mqtt.setHost("api.emitter.io", 8080);
BlockingConnection connection = mqtt.blockingConnection();
connection.connect();

//订阅
connection.subscribe(new Topic[] {new Topic(topic, QoS.AT_LEAST_ONCE)});
//发布
connection.publish(topic, "hello world".getBytes(), QoS.AT_LEAST_ONCE, true);
//接受
Message msg = connection.receive();
// Print it out
System.out.println(msg.getPayloadBuffer()); //ascii: hello world
```
## 特性
### 通配
实际运用中，我们的channel地址是多变的，为了防止每一个channel都要申请一个相应的key，我们可以使用``/#/``为后缀来做通配，例如：
```
nico/#/
```
可以匹配一下channel
```
nico/hello
nico/hello/world
```
### 消息过滤
对于通配下的通道，可能有多个发布者发布消息，假设一个场景下，``publisher1``和``publisher2``同时向``nico/#/``通道发布消息，格式为：
```
nico/publisher1/message
nico/publisher2/message
```
如果订阅者想同时订阅两个发布者的消息，那么订阅的通道可以增加``+``：
```
nico/+/message
```
如果两个发布者发布通道为：
```
nico/message/publisher1
nico/message/publisher2
```
订阅通道可以改成：
```
nico/message/
```
详细文档地址：[https://emitter.io/develop/message-filtering/](https://emitter.io/develop/message-filtering/)
### 消息持久化
推送的消息可以设定失效时间，长期存储在channel中。
### 消息重读
订阅者可以消费存储在channel中的消息。
### 查看channel状态
支持实时查看通道的订阅相关的信息。
## 集群
配置A：
```
{
    "listen": ":8080",
    "license": "bGKMGcG_6rqSMJ_AyymVZaphwUUt_Bm6AAAAAAAAAAI",
    "cluster": {
        "name":"00:00:00:00:00:01",
        "listen": ":4000",
        "advertise": "127.0.0.1:4000"
    },
    "storage": {
        "provider": "ssd",
        "config":{
           "dir":"/data/emitter/1"
        }
    }
}

```
配置B：
```json
{
    "listen": ":8079",
    "license": "bGKMGcG_6rqSMJ_AyymVZaphwUUt_Bm6AAAAAAAAAAI",
    "cluster": {
        "name":"00:00:00:00:00:02",
        "listen": ":4001",
        "advertise": "127.0.0.1:4001",
        "seed":"127.0.0.1:4000"
    },
    "storage": {
        "provider": "ssd",
        "config":{
           "dir":"/data/emitter/2"
        }
    }
}
```
以上都是写磁盘模式。

启动：
```powershell
/root/go/bin/emitter --config emitter2.conf
```