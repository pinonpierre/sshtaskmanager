package com.xesnet.sshtaskmanager.model;

import java.util.List;


/**
 * @author Pierre PINON
 */
public class SshServers {

    private List<SshServer> servers;

    public List<SshServer> getServers() {
        return servers;
    }

    public void setServers(List<SshServer> servers) {
        this.servers = servers;
    }
}
