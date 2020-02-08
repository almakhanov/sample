package kz.dodix.sample.data.interseptors;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import kz.dodix.sample.BuildConfig;
import kz.dodix.sample.R;
import timber.log.Timber;

public class TLSSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory delegate;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        delegate = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(delegate.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if (socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1.2"});
        }
        return socket;
    }

    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            CertificateFactory cf;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                cf = CertificateFactory.getInstance("X.509");
            } else {
                cf = CertificateFactory.getInstance("X.509", "BC");
            }

            Certificate ca;

//            Class<R.raw> typeClass = R.raw.class;
//            Field field = typeClass.getDeclaredField(BuildConfig.CRT);
//            field.setAccessible(true);
//            Object value = field.get(typeClass);
//            int filePath = (Integer) value;

            try (InputStream inputStreamSSH = context.getResources().openRawResource(0)) {
                ca = cf.generateCertificate(inputStreamSSH);
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | NoSuchProviderException e) {
            Timber.e(e);
        }

        return sslSocketFactory;
    }
}