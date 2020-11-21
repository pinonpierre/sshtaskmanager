package com.xesnet.sshtaskmanager.model;

import java.time.LocalDateTime;


/**
 * @author Pierre PINON
 */
public class Run {

    private String id;
    private String name;
    private String output;
    private Integer exitCode;
    private RunState state;
    private LocalDateTime localDateTime;

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

    public RunState getState() {
        return state;
    }

    public void setState(RunState state) {
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

    @Override
    public Run clone() {
        Run runInfo = new Run();
        runInfo.setId(id);
        runInfo.setName(name);
        runInfo.setOutput(output);
        runInfo.setExitCode(exitCode);
        runInfo.setState(state);

        runInfo.updateLocalDateTime();

        return runInfo;
    }
}
