package org.nico.emqtt;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class EMQTest {

    public static void main(String[] args) throws Exception {
        
        String channel = "nico/hello3/";
        String topic = channel;
        
        MQTT mqtt = new MQTT();
        mqtt.setHost("api.emitter.io", 8081);
        
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        
        //订阅
        connection.subscribe(new Topic[] {new Topic(topic, QoS.EXACTLY_ONCE)});
        //发布
        
        connection.publish(topic, "hello world1".getBytes(), QoS.AT_LEAST_ONCE, true);
        connection.publish(topic, "hello world2".getBytes(), QoS.AT_LEAST_ONCE, true);
        //接受
        Message msg = connection.receive();
        // Print it out
        System.out.println(msg.getPayloadBuffer());
        msg.ack();
        
        msg = connection.receive();
        System.out.println(msg.getPayloadBuffer());
        msg.ack();
        
        msg = connection.receive();
        System.out.println(msg.getPayloadBuffer());
        msg.ack();
        
        connection.disconnect();
    }
}
