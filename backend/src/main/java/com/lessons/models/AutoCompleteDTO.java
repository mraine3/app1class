package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoCompleteDTO {

    @JsonProperty("index_name")
    private String indexName;

    @JsonProperty("returned_field")
    private String returnedField;

    @JsonProperty("searched_field")
    private String searchedField;

    @JsonProperty("raw_query")
    private String rawQuery;

    @JsonProperty("size")
    private int    size;


    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getReturnedField() {
        return returnedField;
    }

    public void setReturnedField(String returnedField) {
        this.returnedField = returnedField;
    }

    public String getSearchedField() {
        return searchedField;
    }

    public void setSearchedField(String searchedField) {
        this.searchedField = searchedField;
    }

    public String getRawQuery() {
        return rawQuery;
    }

    public void setRawQuery(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
