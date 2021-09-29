package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobStatusDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("state")
    private Integer state;

    @JsonProperty("user_message")
    private String userMessage;

    @JsonProperty("progress_as_percent")
    private Integer progressAsPercent;

    /**
     * Spring-JDBC's BeanPropertyRowMapper needs the default constructor for the JobService
     * to get a list of jobStatusDTO objects
     */
    public JobStatusDTO() {

    }


    public JobStatusDTO(Integer aId, Integer aState, Integer aProgressAsPercent, String aUserMessage) {
        this.id = aId;
        this.state = aState;
        this.progressAsPercent = aProgressAsPercent;
        this.userMessage = aUserMessage;
    }


    public Integer getId() {
        return id;
    }

    public Integer getState() {
        return state;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public Integer getProgressAsPercent() {
        return progressAsPercent;
    }

}

