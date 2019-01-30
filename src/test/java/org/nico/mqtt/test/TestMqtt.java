package org.nico.mqtt.test;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.QoS;
import org.junit.Test;
import org.nico.mqtt.MqttTest;

public class TestMqtt {

    @Test
    public void testEmitter() throws URISyntaxException, InterruptedException {
        MqttTest.test("nico1", 8080, "Gj-UHzLwA8V865h6P2AzJhn1-_QS8zuJ/nico/hello/", QoS.EXACTLY_ONCE, "emitter", 50, 100, 60 * 10);
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
        MqttTest.test("nico1", 8081, "nico/hello/", QoS.EXACTLY_ONCE, "emqtt",  50, 100, 60 * 10);
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
        MqttTest.test("nico1", 8999, "nico/hello/", QoS.EXACTLY_ONCE, "mosquitto",  10, 5, 60 * 1);
        synchronized (this) {
            this.wait();
        }
    }
}
