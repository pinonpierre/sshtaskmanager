package com.xesnet.sshtaskmanager.util;

import com.xesnet.sshtaskmanager.model.Variable;
import org.apache.commons.text.StringSubstitutor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Pierre PINON
 */
public class VariableResolver {

    private final StringSubstitutor stringSubstitutor;

    public VariableResolver(List<Variable> variables) {
        Map<String, String> variableMap = variables.stream().collect(Collectors.toMap(Variable::getName, Variable::getValue));

        stringSubstitutor = new StringSubstitutor(variableMap);
    }

    private String substituteValue(String value) {
        return stringSubstitutor.replace(value);
    }

    public List<String> substituteValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return values;
        }

        return values.stream().map(this::substituteValue).collect(Collectors.toList());
    }

    public List<Variable> substituteVariableValues(List<Variable> variables) {
        if (variables == null || variables.isEmpty()) {
            return variables;
        }
        return variables.stream().map(variable -> {
            Variable newVariable = new Variable();
            newVariable.setName(variable.getName());
            newVariable.setValue(substituteValue(variable.getValue()));
            return newVariable;
        }).collect(Collectors.toList());
    }
}
