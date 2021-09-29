package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Tell Jackson to ignore the "took" and "items" fields
@JsonIgnoreProperties(value = { "took", "items" })
public class ErrorsDTO {
    private boolean errors;

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }
}
