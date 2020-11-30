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

    public String substituteByValues(String string) {
        return stringSubstitutor.replace(string);
    }

    public List<String> substituteByValues(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return strings;
        }

        return strings.stream().map(this::substituteByValues).collect(Collectors.toList());
    }

}
