package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Builds an SSLContext with custom KeyStore and TrustStore, to work with a client cert signed by a truststore.jks file
 */
public enum SSLContextFactory
{
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(SSLContextFactory.class);

    /**
     * Utility class private constructor
     */
    private SSLContextFactory() {
    }


    /***********************************************************************************
     * makeContext()
     * Creates an SSLContext using the passed-in clientp12 File and truststoreJks File objects
     *
     * @return An initialized SSLContext
     * @throws Exception
     ***********************************************************************************/
    public static SSLContext makeContext(File clientCertificateP12File, String clientCertificatePassword,
                                         File trustJksFile, String trustPassword) throws Exception
    {
        logger.debug("makeContext() started.  aClientCertP12File={}   aTrustJksFile={}",
                clientCertificateP12File.toString(), trustJksFile.toString());

        // Generate a keystore from the client p12 file
        final KeyStore keyStore = loadPKCS12KeyStore(clientCertificateP12File, clientCertificatePassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, clientCertificatePassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        // Generate a truststore from the passed-in aTrustJksFile object
        final KeyStore trustStore = loadJksKeyStore(trustJksFile, trustPassword);

        TrustManager[] trustManagers = {new SSLCustomTrustManager(trustStore) };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        logger.debug("makeContext() finished.");
        return sslContext;
    }


    /***********************************************************************************
     * loadPEMTrustStore()
     * Produces a KeyStore from a String containing a PEM certificate (typically, the server's CA certificate)
     ***********************************************************************************/
    private static KeyStore loadPEMTrustStore(String certificateString) throws Exception
    {
        byte[] certificateBytes = loadPemCertificate(new ByteArrayInputStream(certificateString.getBytes()));
        ByteArrayInputStream derInputStream = new ByteArrayInputStream(certificateBytes);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate)
                certificateFactory.generateCertificate(derInputStream);
        String alias = certificate.getSubjectX500Principal().getName();

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, certificate);

        return trustStore;
    }


    /***********************************************************************************
     * Produces a KeyStore from a PKCS12 (.p12) certificate file, typically the client certificate
     * @param certificateP12File A File object referring to the client p12 certificate
     * @param clientCertPassword Password for the certificate
     * @return A KeyStore containing the certificate from the certificateFile
     ***********************************************************************************/
    private static KeyStore loadPKCS12KeyStore(File certificateP12File, String clientCertPassword) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(certificateP12File)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(fileInputStream, clientCertPassword.toCharArray());
            return keyStore;
        }
    }

    /***********************************************************************************
     * loadJksKeyStore()
     * @param jksCertificateFile
     * @param trustPassword
     * @return
     ***********************************************************************************/
    private static KeyStore loadJksKeyStore(File jksCertificateFile, String trustPassword) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(jksCertificateFile)) {
            KeyStore keyStore =  KeyStore.getInstance("JKS");
            keyStore.load(fileInputStream, trustPassword.toCharArray());
            return keyStore;
        }
    }


    /***********************************************************************************
     * loadPemCertificate()
     * Reads and decodes a base-64 encoded DER certificate (a .pem certificate), typically the server's CA cert.
     * @param certificateStream an InputStream from which to read the cert
     * @return a byte[] containing the decoded certificate
     ***********************************************************************************/
    private static byte[] loadPemCertificate(InputStream certificateStream) throws IOException
    {
        byte[] certificateBytes;
        BufferedReader bufferedReader = null;

        try {
            StringBuilder builder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(certificateStream));

            String line = bufferedReader.readLine();
            while(line != null) {
                if(!line.startsWith("--")){
                    builder.append(line);
                }
                line = bufferedReader.readLine();
            }

            String pem = builder.toString();
            certificateBytes = DatatypeConverter.parseBase64Binary(pem);

        }
        finally
        {
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }
        }

        return certificateBytes;
    }
}