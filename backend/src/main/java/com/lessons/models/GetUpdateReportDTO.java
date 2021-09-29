package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetUpdateReportDTO {
    private final Integer id;

    private final Integer priority;

    @JsonProperty("report_name")
    private final String reportName;

    public GetUpdateReportDTO(Integer id, Integer priority, String reportName) {
        this.id = id;
        this.priority = priority;
        this.reportName = reportName;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getReportName() {
        return reportName;
    }
}
