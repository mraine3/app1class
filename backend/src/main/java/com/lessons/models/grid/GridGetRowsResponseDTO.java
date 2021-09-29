package com.lessons.models.grid;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GridGetRowsResponseDTO {

    @JsonProperty("data")
    private List<Map<String, Object>> data;

    @JsonProperty("lastRow")
    private Integer lastRow;

    @JsonProperty("totalMatches")
    private Integer totalMatches;

    @JsonProperty("secondaryColumnFields")
    private List<String> secondaryColumnFields;

    @JsonProperty("searchAfterClause")
    private String searchAfterClause;

    @JsonProperty("isValidQuery")
    private boolean isValidQuery = true;


    // --------------- Constructor, Getters, and Setters -------------------------------/

    public GridGetRowsResponseDTO(List<Map<String, Object>> data, Integer totalMatches, String aSearchAfterClause) {
        this.data = data;
        this.totalMatches = totalMatches;
        this.searchAfterClause = aSearchAfterClause;
    }


    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Integer getLastRow() {
        return lastRow;
    }

    public void setLastRow(Integer lastRow) {
        this.lastRow = lastRow;
    }

    public Integer getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(Integer totalMatches) {
        this.totalMatches = totalMatches;
    }

    public List<String> getSecondaryColumnFields() {
        return secondaryColumnFields;
    }

    public void setSecondaryColumnFields(List<String> secondaryColumnFields) {
        this.secondaryColumnFields = secondaryColumnFields;
    }

    public String getSearchAfterClause() {
        return searchAfterClause;
    }

    public void setSearchAfterClause(String searchAfterClause) {
        this.searchAfterClause = searchAfterClause;
    }

    public boolean getIsValidQuery() {
        return isValidQuery;
    }

    public void setIsValidQuery(boolean validQuery) {
        isValidQuery = validQuery;
    }
}
