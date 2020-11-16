package com.xesnet.runui.model;

/**
 * @author Pierre PINON
 */
public class Config {

    private String path = "/";
    private Integer port = 8080;
    private Integer tokenTimeout = 300;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
