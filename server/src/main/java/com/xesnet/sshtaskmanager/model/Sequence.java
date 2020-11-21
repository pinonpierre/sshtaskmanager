package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public class Sequence {
    private String name;
    private Job job;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
