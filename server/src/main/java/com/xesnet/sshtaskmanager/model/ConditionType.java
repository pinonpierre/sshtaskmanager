package com.xesnet.sshtaskmanager.model;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * @author Pierre PINON
 */
public enum ConditionType {
    EXIT_CODE("exitCode"),
    OUTPUT("output"),
    STATE("state");

    private final String value;

    ConditionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
