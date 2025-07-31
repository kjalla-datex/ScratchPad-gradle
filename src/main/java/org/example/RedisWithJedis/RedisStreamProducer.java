package org.example.RedisWithJedis;

import io.valkey.JedisPooled;
import io.valkey.StreamEntryID;

import java.util.HashMap;
import java.util.Map;

public class RedisStreamProducer {
    private final JedisPooled jedis;
    private final String streamKey;

    public RedisStreamProducer(JedisPooled jedis, String streamKey) {
        this.jedis = jedis;
        this.streamKey = streamKey;
    }

    public void publish(String message) {
        Map<String, String> data = new HashMap<>();
        data.put("message", message);
        StreamEntryID id = jedis.xadd(streamKey, StreamEntryID.NEW_ENTRY, data);
        System.out.println("Message published with ID: " + id);
    }
}