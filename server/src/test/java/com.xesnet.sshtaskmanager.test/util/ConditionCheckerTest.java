
package com.xesnet.sshtaskmanager.test.util;

import com.xesnet.sshtaskmanager.model.Condition;
import com.xesnet.sshtaskmanager.model.ConditionOperator;
import com.xesnet.sshtaskmanager.model.ConditionType;
import com.xesnet.sshtaskmanager.model.ProcessRun;
import com.xesnet.sshtaskmanager.model.ProcessRunState;
import com.xesnet.sshtaskmanager.util.ConditionChecker;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConditionCheckerTest {

    @Test
    public void test01OutputEqual() {
        testOutput(ConditionOperator.EQUAL, null, null, true);
        testOutput(ConditionOperator.EQUAL, null, "", false);
        testOutput(ConditionOperator.EQUAL, "", null, false);
        testOutput(ConditionOperator.EQUAL, "", "", true);
        testOutput(ConditionOperator.EQUAL, "test", "test", true);
        testOutput(ConditionOperator.EQUAL, "15", "15", true);
        testOutput(ConditionOperator.EQUAL, "15", "015", true);
        testOutput(ConditionOperator.EQUAL, "015", "15", true);
        testOutput(ConditionOperator.EQUAL, "test", "15", false);
        testOutput(ConditionOperator.EQUAL, "15", "test", false);

        testOutput(ConditionOperator.NOT_EQUAL, null, null, false);
        testOutput(ConditionOperator.NOT_EQUAL, null, "", true);
        testOutput(ConditionOperator.NOT_EQUAL, "", null, true);
        testOutput(ConditionOperator.NOT_EQUAL, "", "", false);
        testOutput(ConditionOperator.NOT_EQUAL, "test", "test", false);
        testOutput(ConditionOperator.NOT_EQUAL, "15", "15", false);
        testOutput(ConditionOperator.NOT_EQUAL, "15", "015", false);
        testOutput(ConditionOperator.NOT_EQUAL, "015", "15", false);
        testOutput(ConditionOperator.NOT_EQUAL, "test", "15", true);
        testOutput(ConditionOperator.NOT_EQUAL, "15", "test", true);
    }

    @Test
    public void test02OutputStrictEqual() {
        testOutput(ConditionOperator.STRICT_EQUAL, null, null, true);
        testOutput(ConditionOperator.STRICT_EQUAL, null, "", false);
        testOutput(ConditionOperator.STRICT_EQUAL, "", null, false);
        testOutput(ConditionOperator.STRICT_EQUAL, "", "", true);
        testOutput(ConditionOperator.STRICT_EQUAL, "test", "test", true);
        testOutput(ConditionOperator.STRICT_EQUAL, "15", "15", true);
        testOutput(ConditionOperator.STRICT_EQUAL, "15", "015", false);
        testOutput(ConditionOperator.STRICT_EQUAL, "015", "15", false);
        testOutput(ConditionOperator.STRICT_EQUAL, "test", "15", false);
        testOutput(ConditionOperator.STRICT_EQUAL, "15", "test", false);

        testOutput(ConditionOperator.NOT_STRICT_EQUAL, null, null, false);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, null, "", true);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "", null, true);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "", "", false);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "test", "test", false);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "15", "15", false);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "15", "015", true);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "015", "15", true);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "test", "15", true);
        testOutput(ConditionOperator.NOT_STRICT_EQUAL, "15", "test", true);
    }

    @Test
    public void test03OutputIn() {
        testOutput(ConditionOperator.IN, null, null, false);
        testOutput(ConditionOperator.IN, null, "", false);
        testOutput(ConditionOperator.IN, "", null, false);
        testOutput(ConditionOperator.IN, "", "", true);
        testOutput(ConditionOperator.IN, "test", "test", true);
        testOutput(ConditionOperator.IN, "15", "15", true);
        testOutput(ConditionOperator.IN, "15", "015", false);
        testOutput(ConditionOperator.IN, "015", "15", false);
        testOutput(ConditionOperator.IN, "test", "15", false);
        testOutput(ConditionOperator.IN, "15", "test", false);
        testOutput(ConditionOperator.IN, "test", "one;two;three", false);
        testOutput(ConditionOperator.IN, "one", "one;two;three", true);
        testOutput(ConditionOperator.IN, "two", "one;two;three", true);
        testOutput(ConditionOperator.IN, "three", "one;two;three", true);

        testOutput(ConditionOperator.NOT_IN, null, null, true);
        testOutput(ConditionOperator.NOT_IN, null, "", true);
        testOutput(ConditionOperator.NOT_IN, "", null, true);
        testOutput(ConditionOperator.NOT_IN, "", "", false);
        testOutput(ConditionOperator.NOT_IN, "test", "test", false);
        testOutput(ConditionOperator.NOT_IN, "15", "15", false);
        testOutput(ConditionOperator.NOT_IN, "15", "015", true);
        testOutput(ConditionOperator.NOT_IN, "015", "15", true);
        testOutput(ConditionOperator.NOT_IN, "test", "15", true);
        testOutput(ConditionOperator.NOT_IN, "15", "test", true);
        testOutput(ConditionOperator.NOT_IN, "test", "one;two;three", true);
        testOutput(ConditionOperator.NOT_IN, "one", "one;two;three", false);
        testOutput(ConditionOperator.NOT_IN, "two", "one;two;three", false);
        testOutput(ConditionOperator.NOT_IN, "three", "one;two;three", false);
    }

    @Test
    public void test04OutputGreaterLower() {
        testOutput(ConditionOperator.GREATER, null, null, false);
        testOutput(ConditionOperator.GREATER, null, "", false);
        testOutput(ConditionOperator.GREATER, "", null, false);
        testOutput(ConditionOperator.GREATER, "", "", false);
        testOutput(ConditionOperator.GREATER, "test", "test", false);
        testOutput(ConditionOperator.GREATER, "test", "value", false);
        testOutput(ConditionOperator.GREATER, "0", "test", false);
        testOutput(ConditionOperator.GREATER, "test", "0", false);
        testOutput(ConditionOperator.GREATER, "1", "2", false);
        testOutput(ConditionOperator.GREATER, "2", "1", true);
        testOutput(ConditionOperator.GREATER, "01", "2", false);
        testOutput(ConditionOperator.GREATER, "1", "02", false);
        testOutput(ConditionOperator.GREATER, "02", "1", true);
        testOutput(ConditionOperator.GREATER, "2", "01", true);
        testOutput(ConditionOperator.GREATER, "1", "01", false);
        testOutput(ConditionOperator.GREATER, "01", "1", false);

        testOutput(ConditionOperator.LOWER, null, null, true);
        testOutput(ConditionOperator.LOWER, null, "", true);
        testOutput(ConditionOperator.LOWER, "", null, true);
        testOutput(ConditionOperator.LOWER, "", "", true);
        testOutput(ConditionOperator.LOWER, "test", "test", true);
        testOutput(ConditionOperator.LOWER, "test", "value", true);
        testOutput(ConditionOperator.LOWER, "0", "test", true);
        testOutput(ConditionOperator.LOWER, "test", "0", true);
        testOutput(ConditionOperator.LOWER, "1", "2", true);
        testOutput(ConditionOperator.LOWER, "2", "1", false);
        testOutput(ConditionOperator.LOWER, "01", "2", true);
        testOutput(ConditionOperator.LOWER, "1", "02", true);
        testOutput(ConditionOperator.LOWER, "02", "1", false);
        testOutput(ConditionOperator.LOWER, "2", "01", false);
        testOutput(ConditionOperator.LOWER, "1", "01", true);
        testOutput(ConditionOperator.LOWER, "01", "1", true);
    }

    @Test
    public void test05OutputGreaterLowerOrEqual() {
        testOutput(ConditionOperator.GREATER_OR_EQUAL, null, null, false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, null, "", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "", null, false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "", "", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "test", "test", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "test", "value", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "0", "test", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "test", "0", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "1", "2", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "2", "1", true);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "01", "2", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "1", "02", false);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "02", "1", true);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "2", "01", true);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "1", "01", true);
        testOutput(ConditionOperator.GREATER_OR_EQUAL, "01", "1", true);

        testOutput(ConditionOperator.LOWER_OR_EQUAL, null, null, true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, null, "", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "", null, true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "", "", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "test", "test", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "test", "value", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "0", "test", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "test", "0", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "1", "2", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "2", "1", false);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "01", "2", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "1", "02", true);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "02", "1", false);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "2", "01", false);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "1", "01", false);
        testOutput(ConditionOperator.LOWER_OR_EQUAL, "01", "1", false);
    }

    @Test
    public void test06OutputReg() {
        testOutput(ConditionOperator.REG, null, null, false);
        testOutput(ConditionOperator.REG, null, "", false);
        testOutput(ConditionOperator.REG, "", null, false);
        testOutput(ConditionOperator.REG, "", "", true);
        testOutput(ConditionOperator.REG, "", ".*", true);
        testOutput(ConditionOperator.REG, "abc", ".*", true);
        testOutput(ConditionOperator.REG, "abc", ".b.", true);
        testOutput(ConditionOperator.REG, "cba", ".b.", true);
        testOutput(ConditionOperator.REG, "adc", ".b.", false);
        testOutput(ConditionOperator.REG, "abc", "ab", false);
        testOutput(ConditionOperator.REG, "ab", "abc", false);

        testOutput(ConditionOperator.NOT_REG, null, null, true);
        testOutput(ConditionOperator.NOT_REG, null, "", true);
        testOutput(ConditionOperator.NOT_REG, "", null, true);
        testOutput(ConditionOperator.NOT_REG, "", "", false);
        testOutput(ConditionOperator.NOT_REG, "", ".*", false);
        testOutput(ConditionOperator.NOT_REG, "abc", ".*", false);
        testOutput(ConditionOperator.NOT_REG, "abc", ".b.", false);
        testOutput(ConditionOperator.NOT_REG, "cba", ".b.", false);
        testOutput(ConditionOperator.NOT_REG, "adc", ".b.", true);
        testOutput(ConditionOperator.NOT_REG, "abc", "ab", true);
        testOutput(ConditionOperator.NOT_REG, "ab", "abc", true);
    }

    @Test
    public void test07ExitCodeEqual() {
        testExitCode(ConditionOperator.EQUAL, null, null, true);
        testExitCode(ConditionOperator.EQUAL, null, "", false);
        testExitCode(ConditionOperator.EQUAL, 15, "15", true);
        testExitCode(ConditionOperator.EQUAL, 15, "015", true);
        testExitCode(ConditionOperator.EQUAL, 15, "test", false);

        testExitCode(ConditionOperator.NOT_EQUAL, null, null, false);
        testExitCode(ConditionOperator.NOT_EQUAL, null, "", true);
        testExitCode(ConditionOperator.NOT_EQUAL, 15, "15", false);
        testExitCode(ConditionOperator.NOT_EQUAL, 15, "015", false);
        testExitCode(ConditionOperator.NOT_EQUAL, 15, "test", true);
    }

    @Test
    public void test08ExitCodeStrictEqual() {
        testExitCode(ConditionOperator.STRICT_EQUAL, null, null, true);
        testExitCode(ConditionOperator.STRICT_EQUAL, null, "", false);
        testExitCode(ConditionOperator.STRICT_EQUAL, 15, "15", true);
        testExitCode(ConditionOperator.STRICT_EQUAL, 15, "015", false);
        testExitCode(ConditionOperator.STRICT_EQUAL, 15, "test", false);

        testExitCode(ConditionOperator.NOT_STRICT_EQUAL, null, null, false);
        testExitCode(ConditionOperator.NOT_STRICT_EQUAL, null, "", true);
        testExitCode(ConditionOperator.NOT_STRICT_EQUAL, 15, "15", false);
        testExitCode(ConditionOperator.NOT_STRICT_EQUAL, 15, "015", true);
        testExitCode(ConditionOperator.NOT_STRICT_EQUAL, 15, "test", true);
    }

    @Test
    public void test09ExitCodeIn() {
        testExitCode(ConditionOperator.IN, null, null, false);
        testExitCode(ConditionOperator.IN, null, "", false);
        testExitCode(ConditionOperator.IN, 15, "15", true);
        testExitCode(ConditionOperator.IN, 15, "015", false);
        testExitCode(ConditionOperator.IN, 15, "test", false);
        testExitCode(ConditionOperator.IN, 4, "1;2;3", false);
        testExitCode(ConditionOperator.IN, 1, "1;2;3", true);
        testExitCode(ConditionOperator.IN, 2, "1;2;3", true);
        testExitCode(ConditionOperator.IN, 3, "1;2;3", true);

        testExitCode(ConditionOperator.NOT_IN, null, null, true);
        testExitCode(ConditionOperator.NOT_IN, null, "", true);
        testExitCode(ConditionOperator.NOT_IN, 15, "15", false);
        testExitCode(ConditionOperator.NOT_IN, 15, "015", true);
        testExitCode(ConditionOperator.NOT_IN, 15, "test", true);
        testExitCode(ConditionOperator.NOT_IN, 4, "1;2;3", true);
        testExitCode(ConditionOperator.NOT_IN, 1, "1;2;3", false);
        testExitCode(ConditionOperator.NOT_IN, 2, "1;2;3", false);
        testExitCode(ConditionOperator.NOT_IN, 3, "1;2;3", false);
    }

    @Test
    public void test10ExitCodeGreaterLower() {
        testExitCode(ConditionOperator.GREATER, null, null, false);
        testExitCode(ConditionOperator.GREATER, null, "", false);
        testExitCode(ConditionOperator.GREATER, 0, "test", false);
        testExitCode(ConditionOperator.GREATER, 1, "2", false);
        testExitCode(ConditionOperator.GREATER, 2, "1", true);
        testExitCode(ConditionOperator.GREATER, 1, "02", false);
        testExitCode(ConditionOperator.GREATER, 2, "01", true);
        testExitCode(ConditionOperator.GREATER, 1, "01", false);

        testExitCode(ConditionOperator.LOWER, null, null, true);
        testExitCode(ConditionOperator.LOWER, null, "", true);
        testExitCode(ConditionOperator.LOWER, 0, "test", true);
        testExitCode(ConditionOperator.LOWER, 1, "2", true);
        testExitCode(ConditionOperator.LOWER, 2, "1", false);
        testExitCode(ConditionOperator.LOWER, 1, "02", true);
        testExitCode(ConditionOperator.LOWER, 2, "01", false);
        testExitCode(ConditionOperator.LOWER, 1, "01", true);
    }

    @Test
    public void test11ExitCodeGreaterLowerOrEqual() {
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, null, null, false);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, null, "", false);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, 0, "test", false);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, 1, "2", false);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, 2, "1", true);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, 1, "02", false);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, 2, "01", true);
        testExitCode(ConditionOperator.GREATER_OR_EQUAL, 1, "01", true);

        testExitCode(ConditionOperator.LOWER_OR_EQUAL, null, null, true);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, null, "", true);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, 0, "test", true);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, 1, "2", true);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, 2, "1", false);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, 1, "02", true);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, 2, "01", false);
        testExitCode(ConditionOperator.LOWER_OR_EQUAL, 1, "01", false);
    }

    @Test
    public void test12ExitCodeReg() {
        testExitCode(ConditionOperator.REG, null, null, false);
        testExitCode(ConditionOperator.REG, null, "", false);
        testExitCode(ConditionOperator.REG, 123, ".*", true);
        testExitCode(ConditionOperator.REG, 123, ".2.", true);
        testExitCode(ConditionOperator.REG, 321, ".2.", true);
        testExitCode(ConditionOperator.REG, 143, ".2.", false);
        testExitCode(ConditionOperator.REG, 123, "12", false);
        testExitCode(ConditionOperator.REG, 12, "123", false);

        testExitCode(ConditionOperator.NOT_REG, null, null, true);
        testExitCode(ConditionOperator.NOT_REG, null, "", true);
        testExitCode(ConditionOperator.NOT_REG, 123, ".*", false);
        testExitCode(ConditionOperator.NOT_REG, 123, ".2.", false);
        testExitCode(ConditionOperator.NOT_REG, 321, ".2.", false);
        testExitCode(ConditionOperator.NOT_REG, 143, ".2.", true);
        testExitCode(ConditionOperator.NOT_REG, 123, "12", true);
        testExitCode(ConditionOperator.NOT_REG, 12, "123", true);
    }

    @Test
    public void test13ProcessRunStateEqual() {
        testState(ConditionOperator.EQUAL, null, null, true);
        testState(ConditionOperator.EQUAL, null, "", false);
        testState(ConditionOperator.EQUAL, ProcessRunState.CONNECT, "CONNECT", true);
        testState(ConditionOperator.EQUAL, ProcessRunState.CONNECT, "INIT", false);

        testState(ConditionOperator.NOT_EQUAL, null, null, false);
        testState(ConditionOperator.NOT_EQUAL, null, "", true);
        testState(ConditionOperator.NOT_EQUAL, ProcessRunState.CONNECT, "CONNECT", false);
        testState(ConditionOperator.NOT_EQUAL, ProcessRunState.CONNECT, "INIT", true);
    }

    @Test
    public void test14ProcessRunStateStrictEqual() {
        testState(ConditionOperator.STRICT_EQUAL, null, null, true);
        testState(ConditionOperator.STRICT_EQUAL, null, "", false);
        testState(ConditionOperator.STRICT_EQUAL, ProcessRunState.CONNECT, "CONNECT", true);
        testState(ConditionOperator.STRICT_EQUAL, ProcessRunState.CONNECT, "INIT", false);

        testState(ConditionOperator.NOT_STRICT_EQUAL, null, null, false);
        testState(ConditionOperator.NOT_STRICT_EQUAL, null, "", true);
        testState(ConditionOperator.NOT_STRICT_EQUAL, ProcessRunState.CONNECT, "CONNECT", false);
        testState(ConditionOperator.NOT_STRICT_EQUAL, ProcessRunState.CONNECT, "INIT", true);
    }

    @Test
    public void test15ProcessRunStateIn() {
        testState(ConditionOperator.IN, null, null, false);
        testState(ConditionOperator.IN, null, "", false);
        testState(ConditionOperator.IN, ProcessRunState.CONNECT, "CONNECT", true);
        testState(ConditionOperator.IN, ProcessRunState.CONNECT, "INIT", false);
        testState(ConditionOperator.IN, ProcessRunState.SUBMIT, "SUCCESS;FAILED;TIMEOUT", false);
        testState(ConditionOperator.IN, ProcessRunState.SUCCESS, "SUCCESS;FAILED;TIMEOUT", true);
        testState(ConditionOperator.IN, ProcessRunState.FAILED, "SUCCESS;FAILED;TIMEOUT", true);
        testState(ConditionOperator.IN, ProcessRunState.TIMEOUT, "SUCCESS;FAILED;TIMEOUT", true);

        testState(ConditionOperator.NOT_IN, null, null, true);
        testState(ConditionOperator.NOT_IN, null, "", true);
        testState(ConditionOperator.NOT_IN, ProcessRunState.CONNECT, "CONNECT", false);
        testState(ConditionOperator.NOT_IN, ProcessRunState.CONNECT, "INIT", true);
        testState(ConditionOperator.NOT_IN, ProcessRunState.SUBMIT, "SUCCESS;FAILED;TIMEOUT", true);
        testState(ConditionOperator.NOT_IN, ProcessRunState.SUCCESS, "SUCCESS;FAILED;TIMEOUT", false);
        testState(ConditionOperator.NOT_IN, ProcessRunState.FAILED, "SUCCESS;FAILED;TIMEOUT", false);
        testState(ConditionOperator.NOT_IN, ProcessRunState.TIMEOUT, "SUCCESS;FAILED;TIMEOUT", false);
    }

    private void testOutput(ConditionOperator operator, String value, String conditionValue, boolean expected) {
        ProcessRun processRun = new ProcessRun();
        processRun.setOutput(value);

        test(ConditionType.OUTPUT, operator, processRun, conditionValue, expected);
    }

    private void testExitCode(ConditionOperator operator, Integer value, String conditionValue, boolean expected) {
        ProcessRun processRun = new ProcessRun();
        processRun.setExitCode(value);

        test(ConditionType.EXIT_CODE, operator, processRun, conditionValue, expected);
    }

    private void testState(ConditionOperator operator, ProcessRunState value, String conditionValue, boolean expected) {
        ProcessRun processRun = new ProcessRun();
        processRun.setState(value);

        test(ConditionType.STATE, operator, processRun, conditionValue, expected);
    }

    private void test(ConditionType type, ConditionOperator operator, ProcessRun processRun, String conditionValue, boolean expected) {
        Condition condition = new Condition();
        condition.setType(type);
        condition.setOperator(operator);
        condition.setValue(conditionValue);

        Assert.assertEquals(expected, ConditionChecker.check(processRun, condition));
    }
}
