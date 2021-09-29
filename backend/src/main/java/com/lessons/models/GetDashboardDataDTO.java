package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GetDashboardDataDTO {

    @JsonProperty("chartData1")
    private final List<Map<String, Object>> chartData1;

    @JsonProperty("chartData2")
    private final List<Map<String, Object>> chartData2;

    @JsonProperty("chartData3")
    private final List<Map<String, Object>> chartData3;


    // ---------- Constructor and Getters -------------------
    public GetDashboardDataDTO(List<Map<String, Object>> chartData1, List<Map<String, Object>> chartData2, List<Map<String, Object>> chartData3) {
        this.chartData1 = chartData1;
        this.chartData2 = chartData2;
        this.chartData3 = chartData3;
    }

    public List<Map<String, Object>> getChartData1() {
        return chartData1;
    }

    public List<Map<String, Object>> getChartData2() {
        return chartData2;
    }

    public List<Map<String, Object>> getChartData3() {
        return chartData3;
    }
}
