## 安装
下载docker文件
```
docker load xxx
```
或者rpm安装
## 启动
```
docker run --rm -ti --name emq -p 8081:1883 -p 18083:18083 emqttd-docker-v2.3.11:latest
```
## 集群
配置三个结点集群

分别编辑配置文件``/etc/emqx/emqx.conf``，修改``node.name``名称：
```
A：node.name = emqx@n1.emqx.io
B：node.name = emqx@n2.emqx.io
C：node.name = emqx@n3.emqx.io
```
配置``/etc/hosts``
```
192.168.133.129 n1.emqx.io
192.168.133.75 n2.emqx.io
192.168.133.123 n3.emqx.io
```
分别启动三个结点，之后使用``emqx_ctl``创建集群：
```
B：emqx_ctl cluster join emqx@n1.emqx.io
C：emqx_ctl cluster join emqx@n1.emqx.io
```
## 写磁盘
进入目录``/etc/emqx/plugins``，修改``emqx_retainer.conf``
```
retainer.storage_type = disc
```