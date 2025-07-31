package org.example.RedisWithJedis;

import io.valkey.DefaultJedisClientConfig;
import io.valkey.HostAndPort;
import io.valkey.JedisClientConfig;
import io.valkey.JedisPooled;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

public class RedisJedisTLSFactory {
    public static JedisPooled createTLSClient(String host, int port, String password) {
        try {
            // Load client certificate and key (PKCS12 or JKS)
            String keystorePath = "/Users/kjalla/IdeaProjects/redis-tls/client-keystore.p12";
            String keystorePassword = "changeit";

            // Load CA certificate (trust store)
            String truststorePath = "/Users/kjalla/IdeaProjects/redis-tls/truststore.jks";
            String truststorePassword = "changeit";

            // Load KeyStore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

            // Load TrustStore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());

            // Init KeyManager
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keystorePassword.toCharArray());

            // Init TrustManager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Init SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            JedisClientConfig config = DefaultJedisClientConfig.builder()
                    .ssl(true)
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .sslParameters(new SSLParameters())
                    .hostnameVerifier((hostname, session) -> true) // for testing, disable for prod
                    .password(password)
                    .build();

            return new JedisPooled(new HostAndPort(host, port), config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create TLS Redis client", e);
        }
    }
    

//    public static JedisPooled createTLSClient(String host, int port, String password) {
//        JedisClientConfig config = DefaultJedisClientConfig.builder()
//                .ssl(true)
//                .sslSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault())
//                .sslParameters(new SSLParameters())
//                .hostnameVerifier((hostname, session) -> true)  // Disable hostname verification for testing
//                .password(password)
//                .build();
//
//        return new JedisPooled(new HostAndPort(host, port), config);
//    }
    
}
