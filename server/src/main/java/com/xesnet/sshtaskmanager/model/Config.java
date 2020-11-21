package com.xesnet.sshtaskmanager.model;

import java.util.logging.Level;


/**
 * @author Pierre PINON
 */
public class Config {

    private String host;
    private Integer port = 8080;
    private String logLevel = Level.WARNING.getName(); //OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
    private Integer tokenTimeout = 1800; //seconds
    private Integer runNumberOfThreads = 16;
    private Integer runStatusPollInterval = 100; //milliseconds
    private Integer runTimeout = 300; //seconds
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

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public Integer getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(Integer tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public Integer getRunNumberOfThreads() {
        return runNumberOfThreads;
    }

    public void setRunNumberOfThreads(Integer runNumberOfThreads) {
        this.runNumberOfThreads = runNumberOfThreads;
    }

    public Integer getRunStatusPollInterval() {
        return runStatusPollInterval;
    }

    public void setRunStatusPollInterval(Integer runStatusPollInterval) {
        this.runStatusPollInterval = runStatusPollInterval;
    }

    public Integer getRunTimeout() {
        return runTimeout;
    }

    public void setRunTimeout(Integer runTimeout) {
        this.runTimeout = runTimeout;
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
