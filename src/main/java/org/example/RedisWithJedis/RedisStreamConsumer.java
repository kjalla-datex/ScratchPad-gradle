package org.example.RedisWithJedis;

import io.valkey.JedisPooled;
import io.valkey.StreamEntryID;
import io.valkey.params.XReadParams;
import io.valkey.resps.StreamEntry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RedisStreamConsumer {
    private final JedisPooled jedis;
    private final String streamKey;
    private StreamEntryID lastId = new StreamEntryID(0);// Start from beginning, or use "$" for new only

    public RedisStreamConsumer(JedisPooled jedis, String streamKey) {
        this.jedis = jedis;
        this.streamKey = streamKey;
    }

    public void consume() {
        while (true) {
            Map<String, StreamEntryID> lastIdMap = Collections.singletonMap(streamKey, lastId);
            
            List<Map.Entry<String, List<StreamEntry>>> result =
                    jedis.xread(XReadParams.xReadParams().block(5000).count(1),
                            lastIdMap);
            
            if (result != null) {
                for (Map.Entry<String, List<StreamEntry>> stream : result) {
                    for (StreamEntry entry : stream.getValue()) {
                        System.out.println("Received: " + entry.getFields().get("message") + " (ID: " + entry.getID() + ")");
                        lastId = entry.getID(); // Update last seen ID
                    }
                }
            } else {
                System.out.println("No new messages...");
            }
        }
    }
}
