package org.example;


import io.valkey.CommandObject;
import io.valkey.CommandObjects;
import io.valkey.search.Query;
import io.valkey.search.SearchResult;

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

public class ValkeySearchTlsTester {

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


                //  FT.SEARCH my_index "@timestamp:[0 1721299200]=>[KNN 100000 @vector $query_vector]" PARAMS 2 query_vector "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00"


                float[] queryVector = new float[16]; // Example: 16 zeros, adjust size as needed

                String queryString = "@timestamp:[0 1721299200]=>[KNN 100000 @vector $query_vector]";
                Query query = new Query(queryString);
                query.addParam("query_vector", queryVector);

                commandObjects.setProtocol(jedis.getConnection().getRedisProtocol());

                CommandObject<SearchResult> commandObject = commandObjects.ftSearch("my_index", query);
                SearchResult result = jedis.getConnection().executeCommand(commandObject);
                System.out.println(result);

            }
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
