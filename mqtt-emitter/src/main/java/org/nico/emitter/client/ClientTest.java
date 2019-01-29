package org.nico.emitter.client;


import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


public class ClientTest {

    public static void main(String[] args) throws Exception {
        //keygen生成的key
        String key = "ytqiEY3DpPIfx9-vf-Z_5Od7IzxUQH7V";
        //通道
        String publishChannel = "nico/hello/";
        String subscribeChannel = "nico/hello/";
        //组装topic
        String publishTopic = key + "/" + publishChannel;
        String subscribeTopic = key + "/" + subscribeChannel;
        
        //连接emitter server
        MQTT mqtt = new MQTT();
        mqtt.setHost("api.emitter.io", 8080);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        
        //订阅
        connection.subscribe(new Topic[] {new Topic(subscribeTopic, QoS.EXACTLY_ONCE)});
        //发布
        
        System.out.println(publishTopic);
        connection.publish(publishTopic, "hello world7".getBytes(), QoS.EXACTLY_ONCE, true);
        connection.publish(publishTopic, "hello world8".getBytes(), QoS.EXACTLY_ONCE, true);
        //接受
        Message msg = connection.receive();
        // Print it out
        System.out.println(msg.getPayloadBuffer());
        
        connection.receive();
        System.out.println(msg.getPayloadBuffer());
        
        
    }
}
