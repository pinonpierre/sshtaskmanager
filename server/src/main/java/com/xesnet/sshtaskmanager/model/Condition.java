package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public class Condition {

    private ConditionType type;
    private ConditionOperator operator;
    private String value;
    private Job then;

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Job getThen() {
        return then;
    }

    public void setThen(Job then) {
        this.then = then;
    }
}
