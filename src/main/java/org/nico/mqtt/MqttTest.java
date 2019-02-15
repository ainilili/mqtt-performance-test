package org.nico.mqtt;


import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


public class MqttTest {

    static ThreadPoolExecutor main = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());
    static ThreadPoolExecutor publisher = new ThreadPoolExecutor(100, 100, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());
    static ThreadPoolExecutor subscriber = new ThreadPoolExecutor(3000, 3000, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());

    static boolean pflag = true;
    static boolean sflag = true;
    
    static AtomicInteger pushCount = new AtomicInteger();
    static AtomicInteger receiveCount = new AtomicInteger();

    public static void test(String host, int port, String topic, QoS qos, String name, int publisherCount, int subscriberCount, int seconds) throws URISyntaxException {
        test(host, port, topic, qos, name, publisherCount, subscriberCount, seconds, 5);
    }
    
    public static void test(String host, int port, String topic, QoS qos, String name, int publisherCount, int subscriberCount, int seconds, int pubTime) throws URISyntaxException {
        MQTT mqtt = new MQTT();
        mqtt.setHost(host, port);
        mqtt.setUserName("admin");
        mqtt.setPassword("admin");
        
        CountDownLatch cdl = new CountDownLatch(subscriberCount);
        main.execute(() -> {
            for(int index = 0; index < subscriberCount; index ++) {
                final int id = index;
                subscriber.execute(() -> {
                    MQTT mqtt1 = new MQTT();
                    try {
                        if(name.equals("emitter")) {
                            mqtt1.setHost(host, port - (id % 4));
                        }else {
                            mqtt1.setHost(host, port);
                        }
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                    mqtt1.setUserName("admin");
                    mqtt1.setPassword("admin");
                    
                    BlockingConnection connection = mqtt1.blockingConnection();
                    try {
                        connection.connect();
                        cdl.countDown();
                        connection.subscribe(new Topic[] {new Topic(topic, qos)});
                        while(sflag) {
                            Message msg = connection.receive();
                            receiveCount.incrementAndGet();
                            System.out.println(pushCount.get() + " - " + receiveCount.get() + " - " + msg.getPayloadBuffer());
                            msg.ack();
                        }
                    }catch(Exception e) {
                        System.out.println("Subscriber Error：" + e.getMessage());
                    }finally {
                        try {
                            connection.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        
        try {
            cdl.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        main.execute(() -> {
            for(int index = 0; index < publisherCount; index ++) {
                final int id = index;
                publisher.execute(() -> {
                    BlockingConnection connection = mqtt.blockingConnection();
                    try {
                        connection.connect();
                        while(pflag) {
                            sleep(pubTime);
                            connection.publish(topic, (name + "-" + pushCount.incrementAndGet() + "-id-" + id).getBytes(), qos, true);
                        }
                    }catch(Exception e) {
                        System.out.println("Publisher Error：" + e.getMessage());
                    }finally {
                        try {
                            connection.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
