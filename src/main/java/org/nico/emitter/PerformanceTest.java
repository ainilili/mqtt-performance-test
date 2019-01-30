package org.nico.emitter.test;


import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


public class PerformanceTest {

    static String key = "Zw4Ycw3Kl_sWsWomu-ONxlAlirjt67BE";
    static String channel = "nico/hello/";
    static String topic = key + "/" + channel;

    static ThreadPoolExecutor main = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());
    static ThreadPoolExecutor publisher = new ThreadPoolExecutor(100, 100, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());
    static ThreadPoolExecutor subscriber = new ThreadPoolExecutor(500, 500, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>());

    static boolean pflag = true;
    static boolean sflag = true;
    
    static AtomicInteger pushCount = new AtomicInteger();
    static AtomicInteger receiveCount = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("api.emitter.io", 8080);
        
        int publisherCount = 100;
        int subscriberCount = 1;
        
        Scanner in = new Scanner(System.in);
        String name = in.next();
        
        main.execute(() -> {
            for(int index = 0; index < publisherCount; index ++) {
                final int id = index;
                publisher.execute(() -> {
                    BlockingConnection connection = mqtt.blockingConnection();
                    try {
                        connection.connect();
                        while(pflag) {
                            sleep(5);
                            connection.publish(topic, (name + "-" + pushCount.incrementAndGet() + "-id-" + id).getBytes(), QoS.EXACTLY_ONCE, true);
                        }
                    }catch(Exception e) {
                        System.out.println("Publisher Error：" + e.getMessage());
                    }finally {
                        try {
//                            connection.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });


        main.execute(() -> {
            for(int index = 0; index < subscriberCount; index ++) {
                final int id = index;
                subscriber.execute(() -> {
                    BlockingConnection connection = mqtt.blockingConnection();
                    try {
                        connection.connect();
                        connection.subscribe(new Topic[] {new Topic(topic, QoS.EXACTLY_ONCE)});

                        while(sflag) {
                           
                            Message msg = connection.receive();
                            receiveCount.incrementAndGet();
                            System.out.println(pushCount.get() + " - " + receiveCount.get() + " - " + msg.getPayloadBuffer());
                            msg.ack();
//                            System.out.println("Subscriber " + id + "：" + msg.getPayloadBuffer());
                        }
                    }catch(Exception e) {
                        System.out.println("Subscriber Error：" + e.getMessage());
                    }finally {
                        try {
//                            connection.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        
        main.execute(() -> {
            sleep(50 * 1000);
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
