package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPreferenceDTO {

    @JsonProperty("showBanner")
    private final boolean showBanner;

    // ------------------- Constructor & Getters -------------------

    public GetPreferenceDTO(boolean showBanner) {
        this.showBanner = showBanner;
    }

    public boolean isShowBanner() {
        return showBanner;
    }
}
