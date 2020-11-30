package com.xesnet.sshtaskmanager.model;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Pierre PINON
 */
public class ProcessRun {

    private String id;
    private String name;
    private String output;
    private Integer exitCode;
    private ProcessRunState state;
    private LocalDateTime localDateTime;
    private List<Variable> variables;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public ProcessRunState getState() {
        return state;
    }

    public void setState(ProcessRunState state) {
        this.state = state;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void updateLocalDateTime() {
        setLocalDateTime(LocalDateTime.now());
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public ProcessRun clone() {
        ProcessRun processRun = new ProcessRun();
        processRun.setId(id);
        processRun.setName(name);
        processRun.setOutput(output);
        processRun.setExitCode(exitCode);
        processRun.setState(state);
        processRun.setVariables(variables);

        processRun.updateLocalDateTime();

        return processRun;
    }
}
