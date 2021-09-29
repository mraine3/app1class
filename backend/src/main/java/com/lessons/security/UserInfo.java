package com.lessons.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class UserInfo implements UserDetails, Serializable{

    private String username;                 // The part of the Cn=.... from PKI client cert)
    private String usernameDN;                    // The entire DN string    (from the PKI client cert)
    private List<GrantedAuthority> grantedAuthorities;    // List of roles found in the database)
    private Integer id;                                   // Holds the user's ID in the database
    private Map<String, Boolean> accessMap;

    public String getPassword() {
        // Must implement this method in order to implement the UserDetails interface
        // NOTE:  There is no password as we are using PKI authentication
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }

    public boolean isAccountNonLocked() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }

    public boolean isCredentialsNonExpired() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }

    public boolean isEnabled() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }


    /**
     * @return an array of GrantedAuthority objects for this user
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Must implement this method in order to implement the UserDetails interface
        return this.grantedAuthorities;
    }

    /**
     * @return an array of Strings (for this user)
     */
    public List<String> getRoles() {
        return grantedAuthorities.stream().map(auth -> auth.toString()).collect(Collectors.toList());
    }

    public String getUsernameDN() {
        return this.usernameDN;
    }

    public List<GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(List<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    private static class GrantedAuthorityComparator implements Comparator<GrantedAuthority> {
        public int compare(GrantedAuthority object1, GrantedAuthority object2) {
            return object1.getAuthority().compareTo(object2.getAuthority());
        }
    }

    public Integer getId() {
        return id;
    }

    public Map<String, Boolean> getAccessMap() {
        return accessMap;
    }

    public String toString() {
        return ("Roles=" + StringUtils.join(this.grantedAuthorities, ",") +
                " UID=" + this.username +
                " DN=" + this.usernameDN);
    }

    public UserInfo withId(Integer id) {
        this.id = id;
        return this;
    }

    public UserInfo withUsernameUID(String usernameUID) {
        this.username = usernameUID;
        return this;
    }

    public UserInfo withUsernameDn(String usernameDN) {
        this.usernameDN = usernameDN;
        return this;
    }

    public UserInfo withGrantedAuthorities(List<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
        return this;
    }

    public UserInfo withAccessMap(Map<String, Boolean> aAccessMap) {
        this.accessMap = aAccessMap;
        return this;
    }
}
