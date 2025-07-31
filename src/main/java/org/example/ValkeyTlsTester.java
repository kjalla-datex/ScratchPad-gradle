package org.example;

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

public class ValkeyTlsTester {

    // can be static or singleton, thread safety.
    private static io.valkey.JedisPool jedisPool;

    public static void main(String[] args) {
        try {
            String valkeyHost = "cpoc-01-dkr-001.dpw.io";
            int valkeyPort = 6381;
            System.out.println("Connecting to Valkey at " + valkeyHost + ":" + valkeyPort);
            io.valkey.JedisPoolConfig config = new io.valkey.JedisPoolConfig();
            config.setMaxTotal(32);
            config.setMaxIdle(32);
            config.setMinIdle(16);
//            io.valkey.JedisPool jedisPool = new io.valkey.JedisPool(config, valkeyHost, valkeyPort, 5000, 5000, "eskimming-sensor-page-nonprod", "Datex123",0,null, true, new MySSLSocketFactory(), (SSLParameters)null, (HostnameVerifier)null);
            jedisPool = new io.valkey.JedisPool(config, valkeyHost, valkeyPort, true, new MySSLSocketFactory(), (SSLParameters)null, (HostnameVerifier)null);
            try (io.valkey.Jedis jedis = jedisPool.getResource()) {
                String test = jedis.ping();
                System.out.println("Valkey connection check: " + test);
            } catch (Exception e) {
                e.printStackTrace();
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