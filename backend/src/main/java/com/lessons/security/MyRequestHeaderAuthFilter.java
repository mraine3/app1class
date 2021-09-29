package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Component("com.lessons.security.RequestHeaderAuthFilter")
public class MyRequestHeaderAuthFilter extends RequestHeaderAuthenticationFilter
{
    private static final Logger logger = LoggerFactory.getLogger(MyRequestHeaderAuthFilter.class);

    @Resource
    private MyAuthenticationManager myAuthenticationManager;

    @Value("${use.hardcoded.authenticated.principal}")
    private boolean useHardcodedAuthenticatedPrincipal;

    @PostConstruct
    public void init() {
        this.setAuthenticationManager(myAuthenticationManager);
    }


    /**
     * This is called when a request is made to get the pre-authenticated principal
     * @param request holds the request object
     * @return the userDN found in the certificate
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request)
    {
        logger.debug("getPreAuthenticatedPrincipal() called");

        // Get the principal from the header
        String userDnFromHeader = (String) request.getHeader("SSL_CLIENT_S_DN");
        logger.debug("userDnFromHeader from header -->{}<---", userDnFromHeader);

        if (userDnFromHeader == null) {

            if (useHardcodedAuthenticatedPrincipal) {
                // No header was found, but I am in dev mode or "local prod" mode.  So, set a hard-coded user name
                logger.debug("No header was found, so husing hard-dcoded header 'Bogus_user'");
                userDnFromHeader = "Bogus_user";
            }
        }

        // If this method returns null, then the user will see a 403 Forbidden Message
        logger.debug("getPreAuthenticatedPrincipal() returns -->{}<--", userDnFromHeader);
        return userDnFromHeader;
    }

}

