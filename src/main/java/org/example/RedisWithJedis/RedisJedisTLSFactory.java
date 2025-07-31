package org.example.RedisWithJedis;

import io.valkey.DefaultJedisClientConfig;
import io.valkey.HostAndPort;
import io.valkey.JedisClientConfig;
import io.valkey.JedisPooled;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.X509Certificate;

public class RedisJedisTLSFactory {
    public static JedisPooled createTLSClient(String host, int port, String password) {
        try {
//            // Load client certificate and key (PKCS12 or JKS)
//            String keystorePath = "/Users/kjalla/IdeaProjects/redis-tls/client-keystore.p12";
//            String keystorePassword = "changeit";
//
//            // Load CA certificate (trust store)
//            String truststorePath = "/Users/kjalla/IdeaProjects/redis-tls/truststore.jks";
//            String truststorePassword = "changeit";
//
//            // Load KeyStore
//            KeyStore keyStore = KeyStore.getInstance("PKCS12");
//            keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
//
//            // Load TrustStore
//            KeyStore trustStore = KeyStore.getInstance("JKS");
//            trustStore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());
//
//            // Init KeyManager
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            kmf.init(keyStore, keystorePassword.toCharArray());
//
//            // Init TrustManager
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            tmf.init(trustStore);
//            // Init SSLContext
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("/Users/kjalla/IdeaProjects/redis-tls/truststore.jks"), "changeit".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            
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
////                .sslSocketFactory((SSLSocketFactory) MySSLSocketFactory.getDefault())
//                .sslParameters(null)
////                .sslParameters(new SSLParameters())
////                .hostnameVerifier((hostname, session) -> true)  // Disable hostname verification for testing
//                .hostnameVerifier(null)  // Disable hostname verification for testing
//                .password(password)
//                .build();
//
//        return new JedisPooled(new HostAndPort(host, port), config);
//    }


    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super();

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
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
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
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
