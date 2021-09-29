package com.lessons.security;

import com.lessons.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("com.lessons.security.MyAuthenticationManager")
public class MyAuthenticationManager implements AuthenticationManager {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationManager.class);

    private static final Pattern patExtractCN = Pattern.compile("cn=(.*?)(?:,|/|\\z)", Pattern.CASE_INSENSITIVE);
    private static final Pattern patMatchRole = Pattern.compile("ROLE:(.*?)(?:;|\\z)",
            Pattern.CASE_INSENSITIVE);

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Value("${use.hardcoded.authenticated.principal}")
    private boolean useHardcodedAuthenticatedPrincipal;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("authenticate() started.   authentication={}", authentication);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            // Users is already authenticated, so do nothing
            return  SecurityContextHolder.getContext().getAuthentication();
        }

        UserDetails userDetails;

        if (! useHardcodedAuthenticatedPrincipal) {
            // We are in production mode and we are getting information from headers
            // -- So get the roles from a real source -- e.g., ActiveDirectory, Database, or BDP headers
            userDetails = loadUserDetailsFromRealSource(authentication);
        }
        else {
            // Get the hard-coded bogus user details
            userDetails = loadUserDetailsForDevelopment(authentication);
        }

        // Return an AuthenticationToken object
        PreAuthenticatedAuthenticationToken preapproved = new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        preapproved.setAuthenticated(true);
        logger.debug("authenticate() finished.  preapproved={}", preapproved.toString());
        return preapproved;
    }

    private UserDetails loadUserDetailsFromRealSource(Authentication authentication) {
        logger.debug("loadUserDetailsFromRealSource() started authentication={}", authentication);
        String userDN;
        PreAuthenticatedAuthenticationToken token;

        if (authentication.getPrincipal() instanceof String) {
            userDN = authentication.getPrincipal().toString();
        } else if (authentication.getPrincipal() instanceof UserInfo) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            token = ( PreAuthenticatedAuthenticationToken ) authentication.getPrincipal();
            userDN = token.getName();
        }

        logger.debug("userDN={}", userDN);

        // Get the user's UID from the CN=<...>
        try {
            String userUID = getCnValueFromLongDnString(userDN);

            // Get the list of roles from the header
            List<GrantedAuthority> grantedRoleAuthorities = getAuthoritiesFromHeaderValue();

            if (grantedRoleAuthorities.size() > 0) {
                // This user has at least one role found in my authorization service
                // NOTE:  All granted authorities must start with the "ROLE_" prefix
                grantedRoleAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS"));
            }
            else {
                // This user has no roles so throw a runtime exception
                throw new RuntimeException("No roles were found for this user: " + userUID);
            }

            logger.info("{} successfully logged-in.", userUID);

            // User is about to login
            // -- This would be the place to add/update a database record indicating that the user logged-in
            Integer userId = this.userService.getOrAddUserRecordsToSystem(userUID);

            // Get the user's granted access map
            // NOTE:  This holds all authorized routes and UI controls (based on the user's granted roles)
            Map<String, Boolean> accessMap = userService.generateAccessMap(grantedRoleAuthorities);
            
            logger.debug("loadUserDetailsFromRealSource() about to return new UserInfo object");

            // We *MUST* set the database ID in the UserInfo object here
            return new UserInfo()
                    .withId(userId)
                    .withUsernameDn(userDN)
                    .withUsernameUID(userUID)
                    .withGrantedAuthorities(grantedRoleAuthorities)
                    .withAccessMap(accessMap);

        } catch (Exception e) {
            throw new UsernameNotFoundException("Exception raised in loadUserDetailsFromRealSource():  This user will definitely not login", e);
        }
    }

    /**
     * header(X-BDP-User) holds -AUTH:FOUO;AUTH:U;AUTH:USA;GROUP:BDPUSERS;NAME:bdptest_u_fouo;ROLE:ANALYTIC_RUNNER;ROLE:BDP_ADMIN;ROLE:CITE_USER;ROLE:DATA_ADMIN;ROLE:KIBANA_ADMIN;ROLE:LOGS;ROLE:METRICS;ROLE:OWF_ADMIN;ROLE:OWF_USER;ROLE:UNITY_ADMIN<---
     * Pull every ROLE:role name entry out of the header and insert it into a list of GrantedAuthority objects
     * @return List of GrantedAuthority objects
     */
    private List<GrantedAuthority> getAuthoritiesFromHeaderValue() {
        List<GrantedAuthority> grantedRoles = new ArrayList<>();

        // header(X-BDP-User) holds -AUTH:FOUO;AUTH:U;AUTH:USA;GROUP:BDPUSERS;NAME:bdptest_u_fouo;ROLE:ANALYTIC_RUNNER;ROLE:BDP_ADMIN;ROLE:CITE_USER;ROLE:DATA_ADMIN;ROLE:KIBANA_ADMIN;ROLE:LOGS;ROLE:METRICS;ROLE:OWF_ADMIN;ROLE:OWF_USER;ROLE:UNITY_ADMIN<---
        String xbdpUserHeaderValue = httpServletRequest.getHeader("X-BDP-User");
        logger.debug("In getAuthoritiesFromHeaderValue():  header-->{}<--", xbdpUserHeaderValue);
        if (StringUtils.isEmpty(xbdpUserHeaderValue)) {
            // The header is empty -- so return an empty list
            logger.warn("Warning in getAuthoritiesFromHeaderValue():  The X-BDP-User header had nothing in it.  This should never happen.");
            return grantedRoles;
        }

        // Pull every string that starts with ROLE: and add it to the list
        Matcher matcher = patMatchRole.matcher(xbdpUserHeaderValue);
        while (matcher.find()) {
            String roleName = matcher.group(1);
            logger.debug("Found a role:  roleName={}", roleName);
            String roleNameWithPrefix = "ROLE_" + roleName;
            grantedRoles.add(new SimpleGrantedAuthority(roleNameWithPrefix));
        }

        logger.debug("getAuthoritiesFromHeaderValue() returns -->{}<--", StringUtils.join(grantedRoles, ","));
        return grantedRoles;
    }

    public UserDetails loadUserDetailsForDevelopment(Authentication authentication) {
        String userUID = "my_test_user";
        String userDN = "3.2.12.144549.1.9.1=#161760312e646576,CN=my_test_user,OU=Hosts,O=ZZTop.Org,C=ZZ";

        if ((authentication != null) && (authentication.getPrincipal() != null)) {
            userDN = authentication.getPrincipal().toString();
            userUID = getCnValueFromLongDnString(userDN);
            if (userUID == null) {
                userUID = "my_test_user";
            }
        }

        // Create a list of granted authorities
        List<GrantedAuthority> grantedRoleAuthorities = new ArrayList<>();
        grantedRoleAuthorities.add(new SimpleGrantedAuthority("ROLE_READER"));
        grantedRoleAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS"));

        // User is about to login
        // -- This would be the place to add/update a database record indicating that the user logged-in
        Integer userId = 25;

        // Get the user's granted access map
        // NOTE:  This holds all authorized routes and UI controls (based on the user's granted roles)
        Map<String, Boolean> accessMap = userService.generateAccessMap(grantedRoleAuthorities);

        // Create a bogus UserInfo object
        // NOTE:  I am hard-coding the user's userid=25
        UserInfo anonymousUserInfo = new UserInfo()
                .withId(userId)
                .withUsernameUID(userUID)
                .withUsernameDn(userDN)
                .withGrantedAuthorities(grantedRoleAuthorities)
                .withAccessMap(accessMap);
        
        return anonymousUserInfo;
    }

    private static String getCnValueFromLongDnString(String userDN) {
        logger.debug("getCnValueFromLongDnString()  userDN={}", userDN);
        String cnValue = null;
        // Use the regular expression pattern to getByUserId the value part of "CN=value"
        Matcher matcher = patExtractCN.matcher(userDN);
        if (matcher.find()) {
            cnValue = matcher.group(1);
        }

        logger.debug("getCnValueFromLongDnString() returns -->{}<--", cnValue);
        return cnValue;
    }

}