package com.lessons.models.grid;

public class TextColumnFilter extends ColumnFilter {
    private String type;
    private String filter;
    private String filterTo;

    public TextColumnFilter() {}

    public TextColumnFilter(String type, String filter, String filterTo) {
        this.type = type;
        this.filter = filter;
        this.filterTo = filterTo;
    }

    public String getFilterType() {
        return filterType;
    }

    public String getType() {
        return type;
    }

    public String getFilter() {
        return filter;
    }

    public String getFilterTo() {
        return filterTo;
    }
}
