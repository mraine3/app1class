package com.lessons.models.grid;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class GridGetRowsRequestDTO {

    @JsonProperty("startRow")
    private int startRow;

    @JsonProperty("endRow")
    private int endRow;

    // row group columns
    @JsonProperty("rowGroupCols")
    private List<ColumnVO> rowGroupCols;

    // value columns
    @JsonProperty("valueCols")
    private List<ColumnVO> valueCols;

    // pivot columns
    @JsonProperty("pivotCols")
    private List<ColumnVO> pivotCols;

    // true if pivot mode is one, otherwise false
    @JsonProperty("pivotMode")
    private boolean pivotMode;

    // what groups the user is viewing
    @JsonProperty("groupKeys")
    private List<String> groupKeys;

    // if filtering, what the filter model is
    @JsonProperty("filterModel")
    private Map<String, ColumnFilter> filterModel;

    // if sorting, what the sort model is
    @JsonProperty("sortModel")
    private List<SortModel> sortModel;

    @JsonProperty("rawSearchQuery")
    private String rawSearchQuery;

    @JsonProperty("searchAfterClause")
    private String searchAfterClause;


    // ------------------- Constructor, Getters, and Setters -----------------------------

    public GridGetRowsRequestDTO() {
        this.rowGroupCols = emptyList();
        this.valueCols = emptyList();
        this.pivotCols = emptyList();
        this.groupKeys = emptyList();
        this.filterModel = emptyMap();
        this.sortModel = emptyList();
    }


    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public List<ColumnVO> getRowGroupCols() {
        return rowGroupCols;
    }

    public void setRowGroupCols(List<ColumnVO> rowGroupCols) {
        this.rowGroupCols = rowGroupCols;
    }

    public List<ColumnVO> getValueCols() {
        return valueCols;
    }

    public void setValueCols(List<ColumnVO> valueCols) {
        this.valueCols = valueCols;
    }

    public List<ColumnVO> getPivotCols() {
        return pivotCols;
    }

    public void setPivotCols(List<ColumnVO> pivotCols) {
        this.pivotCols = pivotCols;
    }

    public boolean isPivotMode() {
        return pivotMode;
    }

    public void setPivotMode(boolean pivotMode) {
        this.pivotMode = pivotMode;
    }

    public List<String> getGroupKeys() {
        return groupKeys;
    }

    public void setGroupKeys(List<String> groupKeys) {
        this.groupKeys = groupKeys;
    }

    public Map<String, ColumnFilter> getFilterModel() {
        return filterModel;
    }

    public void setFilterModel(Map<String, ColumnFilter> filterModel) {
        this.filterModel = filterModel;
    }

    public List<SortModel> getSortModel() {
        return sortModel;
    }

    public void setSortModel(List<SortModel> sortModel) {
        this.sortModel = sortModel;
    }

    public String getSearchAfterClause() {
        return searchAfterClause;
    }

    public void setSearchAfterClause(String searchAfterClause) {
        this.searchAfterClause = searchAfterClause;
    }

    public String getRawSearchQuery() {
        return rawSearchQuery;
    }

    public void setRawSearchQuery(String rawSearchQuery) {
        this.rawSearchQuery = rawSearchQuery;
    }
}
