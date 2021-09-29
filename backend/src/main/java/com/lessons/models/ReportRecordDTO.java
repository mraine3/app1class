package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportRecordDTO {

    @JsonProperty("id")    // The JsonProperty must match the ES mapping field name
    private final Integer id;             // Variable name can be anything

    @JsonProperty("description")
    private final String description;

    @JsonProperty("display_name")
    private final String displayName;

    @JsonProperty("priority")
    private final String priority;

    // ---------------------- Constructor & Getters -----------------------


    public ReportRecordDTO(Integer id, String description, String displayName, String priority) {
        this.id = id;
        this.description = description;
        this.displayName = displayName;
        this.priority = priority;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPriority() {
        return priority;
    }
}
