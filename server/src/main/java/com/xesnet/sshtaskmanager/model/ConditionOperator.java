package com.xesnet.sshtaskmanager.model;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * @author Pierre PINON
 */
public enum ConditionOperator {
    EQUAL("="),
    NOT_EQUAL("!="),
    STRICT_EQUAL("=="),
    NOT_STRICT_EQUAL("!=="),
    IN("in"),
    NOT_IN("!in"),
    GREATER(">"),
    LOWER("<"),
    GREATER_OR_EQUAL(">="),
    LOWER_OR_EQUAL("<="),
    REG("reg"),
    NOT_REG("!reg");

    private final String value;

    ConditionOperator(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
