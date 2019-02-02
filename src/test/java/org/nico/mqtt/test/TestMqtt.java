package org.nico.mqtt.test;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.QoS;
import org.junit.Test;
import org.nico.mqtt.MqttTest;
import org.nico.mqtt.PahoTest;

public class TestMqtt {

    @Test
    public void testEmitter() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 8080, "OgeZTxRaxx7Bpr5wnFWKvL9Lf1S9gD2X/nico/hello/?ttl=0", QoS.EXACTLY_ONCE, "emitter", 2, 1000, 60 * 1);
        synchronized (this) {
            this.wait();
        }
    }
    
    @Test
    public void testActiveMQ() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 1883, "nico/hello/", QoS.EXACTLY_ONCE, "activemq",  50, 100, 60 * 10);
        synchronized (this) {
            this.wait();
        }
    }
    
    @Test
    public void testRabbitMQ() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 8666, "nico/hello/", QoS.EXACTLY_ONCE, "rabbitmq",  50, 100, 60 * 10);
        synchronized (this) {
            this.wait();
        }
    }
    
    @Test
    public void testEMQ() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 8081, "nico/hello/", QoS.EXACTLY_ONCE, "emqtt",  50, 100, 60 * 1);
        synchronized (this) {
            this.wait();
        }
    }
    
    @Test
    public void testMosca() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 8888, "nico/hello/", QoS.AT_LEAST_ONCE, "mosca",  50, 100, 60 * 10);
        synchronized (this) {
            this.wait();
        }
    }
    
    @Test
    public void testMosquitto() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 8999, "nico/hello/", QoS.EXACTLY_ONCE, "mosquitto",  10, 1000, 60 * 1);
        synchronized (this) {
            this.wait();
        }
    }
    
    @Test
    public void testMosquittoPaho() throws URISyntaxException, InterruptedException {
        PahoTest.test("tcp://nico1:8999", "nico/hello/", 2, "mosquitto",  30, 1, 60 * 1);
        synchronized (this) {
            this.wait();
        }
    }
    
}
