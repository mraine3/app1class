package com.lessons.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;




@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)         // Needed for @PreAuthorize to work
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {


    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);




    /**
     * The authorization mode being
     * Possible values:  pki    (if user is sending PKI certificate directly to the spring boot webapp)
     *                   header (if a proxy is sending the PKI certificate info as headers to the spring boot webapp)
     *                   null   (if not using ssl)
     */
    @Value("${ssl.security.mode:}")
    private String sslSecurityMode;


    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;


    @Resource
    private MyAuthenticationManager myAuthenticationManager;


    @Resource
    private MyRequestHeaderAuthFilter requestHeaderAuthFilter;


    @Resource
    private SubjectX509PrincipalExtractor subjectX509PrincipalExtractor;


    /**
     * Global configuration to set the authorization listener.
     * @param authenticationManagerBuilder holds the authenticationManagerBuilder
     * @throws Exception if something bad happens
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        logger.debug("configureGlobal() started");
        super.configure(authenticationManagerBuilder);


        logger.debug("configureGlobal() finished");
    }


    @PostConstruct
    public void init() {
        if (sslEnabled) {
            // Running in SSL mode so verify that sslSecurityMode is set to a valid value


            if (sslSecurityMode == null) {
                throw new RuntimeException("Critical Error in SpringSecurityConfig:  server.ssl.enabled=TRUE but ssl.security.mode is not set.  The ssl.security.mode should be either 'header' or 'pki'");
            }
            else if (!(sslSecurityMode.equalsIgnoreCase("header")) && (!(sslSecurityMode.equalsIgnoreCase("pki")))) {
                throw new RuntimeException("Critical Error in SpringSecurityConfig:  server.ssl.enabled=TRUE but ssl.security.mode holds an invalid value.  The ssl.security.mode should be either 'header' or 'pki'");
            }
        }
    }


    /**
     * Configure Spring Security
     *
     * If ssl is enabled AND ssl.security.mode=='pki'
     *    then use the x509 filter to getByUserId the DN from the x509 certificate
     *
     * If ssl is enabled AND ssl.security.mode is anything else
     *    Then use the requestHeaderAuthFilter to pull roles from the header
     *
     * @param aHttpSecurity an HttpSecurity object with Spring Security Configuration
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity aHttpSecurity) throws Exception {
        logger.debug("configure() started.");


        if (sslEnabled) {
            // Running in https mode
            javax.servlet.Filter filter;


            if (sslSecurityMode.equalsIgnoreCase("pki")) {
                // Running in "prod" mode with sslSecurityMode="pki" so, get the CN information from the user's x509 pki cert
                logger.debug("In configure()  filter will be the x509 filter");
                filter = x509Filter();
            }
            else {
                // Running in "prod" mode with sslSecurityMode="header" so, get the CN information from a header
                logger.debug("In configure()  filter will be MyRequestHeaderAuthFilter");
                filter = requestHeaderAuthFilter;
            }


            aHttpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                    .authorizeRequests()    // Filters out any URLs that are ignored.  This should be before any authorization filters
                    .antMatchers("/resources/**", "/app1/resources/**", "/error").permitAll()
                    .antMatchers("/**").access("hasRole('ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS')")   // All users must have the grantedAuthority called ROLE_UserFoundInLdap to view all pages
                    .and()
                    .requiresChannel().antMatchers("/**").requiresSecure()    // Redirect http to https
                    .and()
                    .addFilter(filter)                                 // Pull the DN from the user's X509 certificate or header
                    .headers().frameOptions().disable()               // By default X-Frame-Options is set to denied.  Disable frameoptions to let this webapp work in OWF
                    .and()
                    .anonymous().disable();
        }
        else {
            // Running in http mode
            aHttpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                    .authorizeRequests()     // Filters out any URLs that are ignored.  This should be before any authorization filters
                    .antMatchers("/resources/**", "/app1/resources/**", "/app1/error", "/error").permitAll()
                    .antMatchers("/**").access("hasRole('ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS')")   // All users must have the grantedAuthority called ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS to view all pages
                    .anyRequest().authenticated()
                    .and()
                    .requiresChannel().antMatchers("/**").requiresInsecure()
                    .and()
                    .addFilter(requestHeaderAuthFilter)
                    .headers().frameOptions().disable()                       // By default X-Frame-Options is set to denied.
                    .and()
                    .anonymous().disable();
        }


        // Disable CSRF Checks (because the BDP cannot handle it)
        aHttpSecurity.csrf().disable();
    }






    /**
     * Configures the X509AuthenticationFilter for extracting information from the Cert
     * @return X509AuthenticationFilter object
     */
    @Bean
    public X509AuthenticationFilter x509Filter() {
        // Setup a filter that extracts the principal from the cert
        X509AuthenticationFilter x509Filter = new X509AuthenticationFilter();
        x509Filter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
        x509Filter.setAuthenticationManager(myAuthenticationManager);
        x509Filter.setPrincipalExtractor(subjectX509PrincipalExtractor);
        return x509Filter;
    }
}


