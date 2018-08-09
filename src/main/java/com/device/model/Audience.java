package com.device.model;

public enum Audience {

    UNKNOWN("unknown"),
    WEB("web"),
    MOBILE("mobile"),
    TABLET("tablet");

    private final String value;

    Audience(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
