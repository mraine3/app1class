package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class UserInfoDTO {
    @JsonProperty("name")
    private final String name;

    @JsonProperty("pageRoutes")
    private final Map<String, Boolean> accessMap;

    // -------------- Constructor & Getters --------------------------------

    public UserInfoDTO(String name, Map<String, Boolean> accessMap) {
        this.name = name;
        this.accessMap = accessMap;
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getAccessMap() {
        return accessMap;
    }
}
