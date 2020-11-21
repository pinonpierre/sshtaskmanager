package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public class RunManagerConfig {

    private Integer numberOfThreads = 16;
    private Integer statusPollInterval = 100;
    private Integer timeout = 300;
    private Integer cleanInterval = 300;
    private Integer retention = 1800;

    public Integer getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Integer getStatusPollInterval() {
        return statusPollInterval;
    }

    public void setStatusPollInterval(Integer statusPollInterval) {
        this.statusPollInterval = statusPollInterval;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getCleanInterval() {
        return cleanInterval;
    }

    public void setCleanInterval(Integer cleanInterval) {
        this.cleanInterval = cleanInterval;
    }

    public Integer getRetention() {
        return retention;
    }

    public void setRetention(Integer retention) {
        this.retention = retention;
    }
}
