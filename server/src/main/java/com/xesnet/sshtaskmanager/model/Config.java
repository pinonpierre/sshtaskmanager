package com.xesnet.sshtaskmanager.model;

import java.util.logging.Level;


/**
 * @author Pierre PINON
 */
public class Config {

    private String host;
    private Integer port = 8080;
    private String logLevel = Level.WARNING.getName();
    private Integer tokenTimeout = 1800;
    private RunManagerConfig runManager = new RunManagerConfig();

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

    public RunManagerConfig getRunManager() {
        return runManager;
    }

    public void setRunManager(RunManagerConfig runManager) {
        this.runManager = runManager;
    }
}
