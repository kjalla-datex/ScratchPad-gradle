package org.example;

import io.valkey.CommandObjects;
import io.valkey.RedisProtocol;
import io.valkey.search.IndexOptions;
import io.valkey.search.Query;
import io.valkey.search.Schema;
import sun.reflect.FieldInfo;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.valkey.RedisProtocol.RESP2;
import static io.valkey.RedisProtocol.RESP3;

public class ValkeyTlsTester {

    // can be static or singleton, thread safety.
    private static io.valkey.JedisPool jedisPool;

    public static void main(String[] args) {
        try {
            String valkeyHost = "cpoc-01-dkr-001.dpw.io";
            int valkeyPort = 6381;
            System.out.println("Connecting to Valkey at " + valkeyHost + ":" + valkeyPort);
            io.valkey.JedisPoolConfig config = new io.valkey.JedisPoolConfig();
            // It is recommended that you set maxTotal = maxIdle = 2*minIdle for best performance
            config.setMaxTotal(32);
            config.setMaxIdle(32);
            config.setMinIdle(16);
            // jedisPool = new io.valkey.JedisPool(config, "localhost", 6381, true, new MySSLSocketFactory(), (SSLParameters)null, (HostnameVerifier)null);
            jedisPool = new io.valkey.JedisPool(config, valkeyHost, valkeyPort, true, new MySSLSocketFactory(), (SSLParameters)null, (HostnameVerifier)null);




            try (io.valkey.Jedis jedis = jedisPool.getResource()) {

                CommandObjects commandObjects = new CommandObjects();
                commandObjects.setProtocol(jedis.getConnection().getRedisProtocol());


                String keyPrefix = "esp:page:";
                String key = keyPrefix + "1";


                Map<String, String> map = new HashMap<>();
                map.put("cust", "datex");
                map.put("env", "non-prod");
                map.put("correlationId", UUID.randomUUID().toString());
                map.put("timestamp", "" + (System.currentTimeMillis() / 1000));
                map.put("embedding", "" + (System.currentTimeMillis() / 1000));
                map.put("embedding-z", "" + (System.currentTimeMillis() / 1000));



                // jedis.hdel(keyPrefix + "1", "embedding");
                jedis.hset(key, map);
                jedis.getConnection().executeCommand(commandObjects.hset(key, map));
//                jedis.zadd(key, (double)(System.currentTimeMillis() / 1000), "timestamp-zset");
//                jedis.del(key);
//                Object result = jedis.eval("hset post:1 title \"hello world\" body \"this is a cool document\"");
//                System.out.println("HSET result: " + result);
//                result = jedis.eval("hset post:2 title \"goodbye everybody\" body \"this is the best document\"");
                System.out.println(jedis.hgetAll(key));

                commandObjects.ftDropIndex(keyPrefix);

                Schema schema = new Schema();
                schema.addTagField("cust");
// Add a vector field (example: 128 dimensions, FLOAT32, FLAT index)

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("TYPE", "FLOAT32");
                attributes.put("DIM", 1);
                attributes.put("DISTANCE_METRIC", "L2"); // or "L2", "IP" as needed

//                schema.addVectorField("embedding", Schema.VectorField.VectorAlgo.HNSW, attributes);
//                schema.addVectorField("embedding", Schema.VectorField.VectorAlgo.HNSW, 128, Schema.VectorIndexType.FLAT);

//                try {
//                    Object result = jedis.getConnection().executeCommand(
//                            commandObjects.ftCreate(keyPrefix, IndexOptions.defaultOptions(), schema)
//                    );
//                    System.out.println("Index created: " + result);
//                }
//                catch (Throwable t) {
//                    System.err.println("Error creating index: " + t.getMessage());
//                }


//                Query query = new Query("@timestamp:[" + 0 + " " + (System.currentTimeMillis() / 1000) + "]");
//                Object searchResult = jedis.getConnection().executeCommand(commandObjects.ftSearch(keyPrefix, query));
//                System.out.println("Search result: " + searchResult);

            }

            // jedis.getConnection().executeCommand(commandObjects.ftSearch())




            jedisPool.close();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super();

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return new String[0];
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return new String[0];
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }

        @Override
        public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
            return null;
        }
    }
}
