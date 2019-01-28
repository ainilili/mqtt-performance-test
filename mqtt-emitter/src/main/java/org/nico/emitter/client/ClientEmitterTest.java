package org.nico.emitter.client;

import io.emitter.Emitter;
import io.emitter.mqtt.client.BlockingConnection;
import io.emitter.mqtt.client.Message;
import io.emitter.mqtt.client.Topic;

public class ClientEmitterTest {

    public static void main(String[] args) {
        String key = "I4Ypn8JoMSDECUxUm746DOxo_0bKHyXJ";
        String channel = "nico/";
        // Get an implementation of a blocking connection
        final BlockingConnection connection = Emitter.getDefault().blockingConnection();
        try {
            // Connect to emitter service
            connection.connect();

            // Subscribe to some channel
            connection.subscribe(new Topic(key, channel));

            // Publish a message
            connection.publish(key, channel, "hello, emitter!".getBytes());

            // Receive 10 messages
            for(int i=0; i < 10; ++i){
                // Receive a message and get the payload buffer
                Message msg = connection.receive();

                // Print it out
                System.out.println(msg.getPayloadBuffer());
            }

            // Disconnect ourselves
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
