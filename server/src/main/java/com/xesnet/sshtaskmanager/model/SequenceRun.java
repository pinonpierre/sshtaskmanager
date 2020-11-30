package com.xesnet.sshtaskmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pierre PINON
 */
public class SequenceRun {

    private String id;
    private String name;
    private String output;
    private Integer exitCode;
    private SequenceRunState state;
    private List<String> jobs;
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

    public SequenceRunState getState() {
        return state;
    }

    public void setState(SequenceRunState state) {
        this.state = state;
    }

    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobSteps) {
        this.jobs = jobSteps;
    }

    public void addJob(String job) {
        if (jobs == null) {
            jobs = new ArrayList<>();
        }
        jobs.add(job);
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
    public SequenceRun clone() {
        SequenceRun sequenceRun = new SequenceRun();
        sequenceRun.setId(id);
        sequenceRun.setName(name);
        sequenceRun.setOutput(output);
        sequenceRun.setExitCode(exitCode);
        sequenceRun.setState(state);
        sequenceRun.setJobs(jobs == null ? null: new ArrayList<>(jobs));
        sequenceRun.setVariables(variables == null ? null: new ArrayList<>(variables));

        sequenceRun.updateLocalDateTime();

        return sequenceRun;
    }
}
