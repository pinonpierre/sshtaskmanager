package com.xesnet.sshtaskmanager.model;

import java.util.List;


/**
 * @author Pierre PINON
 */
public class Job {
    private String run;
    private List<Condition> conditions;

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
