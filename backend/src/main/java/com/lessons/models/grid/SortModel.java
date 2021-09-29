package com.lessons.models.grid;

import java.io.Serializable;

public class SortModel implements Serializable {

    private String colId;
    private String sort;

    public SortModel() {}

    public SortModel(String colId, String sort) {
        this.colId = colId;
        this.sort = sort;
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }


}
