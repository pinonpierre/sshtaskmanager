package com.xesnet.sshtaskmanager.model;

import java.util.List;


/**
 * @author Pierre PINON
 */
public class SshRun {

    private String name;
    private String server;
    private List<String> commands;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
