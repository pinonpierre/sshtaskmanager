package com.xesnet.sshtaskmanager.yaml;

import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.model.SshRuns;
import com.xesnet.sshtaskmanager.model.SshServers;
import com.xesnet.sshtaskmanager.model.Users;

import java.nio.file.Path;


/**
 * @author Pierre PINON
 */
public class Yaml {

    private static final String CONFIG_FILE = "config.yaml";
    private static final String USERS_FILE = "users.yaml";
    private static final String SERVERS_FILE = "servers.yaml";
    private static final String RUNS_FILES = "runs.yaml";

    private final YamlContext yamlContext;

    public Yaml(Path configPath) {
        yamlContext = new YamlContext(configPath);
    }

    public Config readConfig() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.CONFIG_FILE, Config.class, true);
    }

    public Users readUsers() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.USERS_FILE, Users.class, true);
    }

    public SshServers readServers() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.SERVERS_FILE, SshServers.class);
    }

    public SshRuns readRuns() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.RUNS_FILES, SshRuns.class);
    }
}
