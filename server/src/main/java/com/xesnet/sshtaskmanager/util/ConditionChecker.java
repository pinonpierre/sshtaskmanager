package com.xesnet.sshtaskmanager.util;

import com.xesnet.sshtaskmanager.model.Condition;
import com.xesnet.sshtaskmanager.model.ProcessRun;

import java.util.Arrays;


/**
 * @author Pierre PINON
 */
public class ConditionChecker {

    private ConditionChecker() {
    }

    public static boolean check(ProcessRun processRun, Condition condition) {
        String conditionValue = condition.getValue();

        String value;
        switch (condition.getType()) {
            case EXIT_CODE -> value = processRun.getExitCode() == null ? null : processRun.getExitCode().toString();
            case OUTPUT -> value = processRun.getOutput();
            case STATE -> value = processRun.getState() == null ? null : processRun.getState().name();
            default -> throw new IllegalStateException("Unexpected value: " + condition.getType());
        }

        boolean result;
        switch (condition.getOperator()) {
            case EQUAL -> result = isEquals(value, conditionValue);
            case NOT_EQUAL -> result = !isEquals(value, conditionValue);
            case STRICT_EQUAL -> result = isStrictEquals(value, conditionValue);
            case NOT_STRICT_EQUAL -> result = !isStrictEquals(value, conditionValue);
            case IN -> result = isIn(value, conditionValue);
            case NOT_IN -> result = !isIn(value, conditionValue);
            case GREATER -> result = isGreater(value, conditionValue, false);
            case LOWER -> result = !isGreater(value, conditionValue, false);
            case GREATER_OR_EQUAL -> result = isGreater(value, conditionValue, true);
            case LOWER_OR_EQUAL -> result = !isGreater(value, conditionValue, true);
            case REG -> result = isReg(value, conditionValue);
            case NOT_REG -> result = !isReg(value, conditionValue);
            default -> throw new IllegalStateException("Unexpected value: " + condition.getType());
        }

        return result;
    }

    public static boolean isEquals(String value, String conditionValue) {
        if (value == null && conditionValue == null) {
            return true;
        } else if ((value != null && conditionValue == null) || (value == null && conditionValue != null)) {
            return false;
        }

        try {
            int integer = Integer.parseInt(value);
            int conditionInteger = Integer.parseInt(conditionValue);

            return integer == conditionInteger;

        } catch (NumberFormatException e) {
            return isStrictEquals(value, conditionValue);
        }
    }

    public static boolean isStrictEquals(String value, String conditionValue) {
        return (value == null && conditionValue == null) || (value != null && value.equals(conditionValue));
    }

    public static boolean isIn(String value, String conditionValue) {
        if (value == null || conditionValue == null) {
            return false;
        }
        String[] conditionValues = conditionValue.split(";");

        return Arrays.asList(conditionValues).contains(value);
    }

    public static boolean isGreater(String value, String conditionValue, boolean orEqual) {
        if (value == null || conditionValue == null) {
            return false;
        }

        try {
            int integer = Integer.parseInt(value);
            int conditionInteger = Integer.parseInt(conditionValue);

            return orEqual ? integer >= conditionInteger : integer > conditionInteger;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isReg(String value, String conditionValue) {
        if (value == null || conditionValue == null) {
            return false;
        }

        return value.matches(conditionValue);
    }
}
