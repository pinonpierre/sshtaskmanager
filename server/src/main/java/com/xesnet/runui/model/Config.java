package com.xesnet.runui.model;

/**
 * @author Pierre PINON
 */
public class Config {

    private String host;
    private Integer port = 8080;
    private Integer tokenTimeout = 1800; //seconds
    private Integer commandTimeout = 30; //seconds
    private Integer outputPollInterval = 100; //milliseconds
    private Integer numberOfThreads = 16;
    private Integer runCleanInterval = 300; //seconds
    private Integer runRetention = 1800; //seconds

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(Integer tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public Integer getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(Integer commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public Integer getOutputPollInterval() {
        return outputPollInterval;
    }

    public void setOutputPollInterval(Integer outputPollInterval) {
        this.outputPollInterval = outputPollInterval;
    }

    public Integer getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Integer getRunCleanInterval() {
        return runCleanInterval;
    }

    public void setRunCleanInterval(Integer runCleanInterval) {
        this.runCleanInterval = runCleanInterval;
    }

    public Integer getRunRetention() {
        return runRetention;
    }

    public void setRunRetention(Integer runRetention) {
        this.runRetention = runRetention;
    }
}
