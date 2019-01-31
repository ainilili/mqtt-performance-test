package org.nico.mqtt;


import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttAck;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.fusesource.mqtt.codec.SUBACK;


public class PahoTest {

    static ThreadPoolExecutor main = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());
    static ThreadPoolExecutor publisher = new ThreadPoolExecutor(100, 100, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());
    static ThreadPoolExecutor subscriber = new ThreadPoolExecutor(500, 500, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());

    static boolean pflag = true;
    static boolean sflag = true;
    
    static AtomicInteger pushCount = new AtomicInteger();
    static AtomicInteger receiveCount = new AtomicInteger();

    public static void test(String broker, String topic, int qos, String name, int publisherCount, int subscriberCount, int seconds) throws URISyntaxException {
        test(broker, topic, qos, name, publisherCount, subscriberCount, seconds, 5);
    }
    
    public static void test(String broker, String topic, int qos, String name, int publisherCount, int subscriberCount, int seconds, int pubTime) throws URISyntaxException {
        main.execute(() -> {
            for(int index = 0; index < subscriberCount; index ++) {
                final int id = index;
                subscriber.execute(() -> {
                    try {
                        MemoryPersistence persistence = new MemoryPersistence();
                        MqttClient sampleClient = new MqttClient(broker, name + "sub" + id, persistence);
                        MqttConnectOptions connOpts = new MqttConnectOptions();
                        connOpts.setCleanSession(true);
                        connOpts.setUserName("admin");
                        connOpts.setPassword("admin".toCharArray());
                        sampleClient.connect(connOpts);
                        sampleClient.subscribe(topic, qos, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                receiveCount.incrementAndGet();
                                System.out.println(pushCount.get() + " - " + receiveCount.get() + " - " + new String(message.getPayload()));
                            }
                        });
                        sampleClient.disconnect();
                    }catch(Exception e) {
                        System.out.println("Subscriber Error：" + e.getMessage());
                    }finally {
                    }
                });
            }
        });
        
        sleep(5000);
        
        main.execute(() -> {
            for(int index = 0; index < publisherCount; index ++) {
                final int id = index;
                publisher.execute(() -> {
                    try {
                        MemoryPersistence persistence = new MemoryPersistence();
                        MqttClient sampleClient = new MqttClient(broker, name + "pub" + id, persistence);
                        MqttConnectOptions connOpts = new MqttConnectOptions();
                        connOpts.setCleanSession(true);
                        connOpts.setUserName("admin");
                        connOpts.setPassword("admin".toCharArray());
                        sampleClient.connect(connOpts);
                        while(pflag) {
                            sleep(pubTime);
                            MqttMessage msg = new MqttMessage((name + "-" + pushCount.incrementAndGet() + "-id-" + id).getBytes());
                            msg.setQos(qos);
                            sampleClient.publish(topic, msg);
                        }
                        sampleClient.disconnect();
                    }catch(Exception e) {
                        System.out.println("Publisher Error：" + e.getMessage());
                    }finally {
                    }

                });
            }
        });
        

        main.execute(() -> {
            sleep(seconds * 1000);
            pflag = false;
            System.out.println("发布者关闭...");
        });
    }
    

    static void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
