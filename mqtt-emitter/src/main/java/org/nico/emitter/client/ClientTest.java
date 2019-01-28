package org.nico.emitter.client;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


public class ClientTest {

    public static void main(String[] args) throws Exception {
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
        System.out.println(msg.getPayloadBuffer());
        
//        new Thread() {
//            @Override
//            public void run() {
//                Scanner in = new Scanner(System.in);
//                while(in.hasNext()) {
//                    try {
//                        connection.publish(topic, in.next().getBytes(), QoS.AT_LEAST_ONCE, true);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }   
//                }
//            }
//        }.start();
//        
//        for(int i=0; i < 10; ++i){
//            Message msg = connection.receive();
//            
//            // Print it out
//            System.out.println(msg.getPayloadBuffer());
//        }
        
        
    }
}
