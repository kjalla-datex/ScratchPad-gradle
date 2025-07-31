package org.example.RedisWithJedis;

import io.valkey.JedisPooled;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        String host = "your-valkey-host"; // example: cpoc-01-dkr-001.dpw.io
        String host = "localhost"; // example: cpoc-01-dkr-001.dpw.io
        int port = 6381;
        String password = ""; // optional
        String stream = "demo-stream";

        JedisPooled jedis = RedisJedisTLSFactory.createTLSClient(host, port, password);

        RedisStreamProducer producer = new RedisStreamProducer(jedis, stream);
        RedisStreamConsumer consumer = new RedisStreamConsumer(jedis, stream);

        // Run consumer in a thread
        new Thread(consumer::consume).start();

        // Publish messages
        for (int i = 0; i < 5; i++) {
            producer.publish("Hello Redis Stream TLS - " + i);
            Thread.sleep(1000);
        }
    }
}

