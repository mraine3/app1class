package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetUpdateReportDTO {
    private Integer id;

    private Integer priority;

    @JsonProperty("report_name")
    private String reportName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
