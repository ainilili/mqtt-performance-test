## 启动
```powershell
docker run -d --name rabbit -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 8666:1883 -p 15672:15672 docker.io/rabbitmq
```
## 安装mqtt插件
文档：[http://www.rabbitmq.com/mqtt.html](http://www.rabbitmq.com/mqtt.html)
```powershell
docker exec -i -t rabbit rabbitmq-plugins enable rabbitmq_mqtt
```
