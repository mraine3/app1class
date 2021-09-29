package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoCompleteMatchDTO {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("name")
    private final String name;


    // ------------------- Constructors & Getters ----------------------

    public AutoCompleteMatchDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
