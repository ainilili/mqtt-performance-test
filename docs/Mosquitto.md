## 安装
安装导航页[https://mosquitto.org/download/](https://mosquitto.org/download/)

centos7安装需要到``/etc/yum.repos.d/``目录下，新建文件``CentOS-mosquitto.repo``文件，写入内容：
```
[home_oojah_mqtt]
name=mqtt (CentOS_CentOS-7)
type=rpm-md
baseurl=http://download.opensuse.org/repositories/home:/oojah:/mqtt/CentOS_CentOS-7/
gpgcheck=1
gpgkey=http://download.opensuse.org/repositories/home:/oojah:/mqtt/CentOS_CentOS-7/repodata/repomd.xml.key
enabled=1
```
之后使用``yum``下载：
```powershell
yum install mosquitto
```
## 启动
```powershell
mosquitto -p 8999 -c mosquitto.conf
```
## 桥接
选取三个服务器做三个结点桥接，分别为A、B、C，接下来修改``mosquitto.conf``配置：

A结点:
```
connection bridge2
address n2.emqx.io:1883
topic nico/hello/ both 2

connection bridge3
address n3.emqx.io:1883
topic nico/hello/ both 2
```

A作为主结点存在桥接另外两个结点，在启动A之前先启动B、C，再启动A即可

## 写磁盘
mosquitto默认是写内存的，可以切换为存储模式
```
autosave_interval 5 #内存写入磁盘间隔s
persistence true #开启存储模式
persistence_file mosquitto.db #存储文件
persistence_location /data/mosquitto/ #存储目录
```
