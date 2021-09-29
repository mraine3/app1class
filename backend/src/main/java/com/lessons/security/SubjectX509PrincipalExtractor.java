package com.lessons.security;

import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;
import org.springframework.stereotype.Component;
import java.security.cert.X509Certificate;


/**
 * Extractor for the principal from the certificate.
 */
@Component("com.lessons.security.SubjectX509PrincipalExtractor")
public class SubjectX509PrincipalExtractor implements X509PrincipalExtractor
{

    public Object extractPrincipal(X509Certificate clientCertificate)
    {
        // Get the Distinguished Name (DN) from the cert
        return clientCertificate.getSubjectX500Principal().getName();
    }
}



