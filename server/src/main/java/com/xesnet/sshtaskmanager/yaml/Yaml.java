package com.xesnet.sshtaskmanager.yaml;

import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.model.Sequences;
import com.xesnet.sshtaskmanager.model.Processes;
import com.xesnet.sshtaskmanager.model.Servers;
import com.xesnet.sshtaskmanager.model.Users;

import java.nio.file.Path;


/**
 * @author Pierre PINON
 */
public class Yaml {

    private static final String CONFIG_FILE = "config.yaml";
    private static final String USERS_FILE = "users.yaml";
    private static final String SERVERS_FILE = "servers.yaml";
    private static final String PROCESSES_FILES = "processes.yaml";
    private static final String SEQUENCES_FILES = "sequences.yaml";

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

    public Servers readServers() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.SERVERS_FILE, Servers.class);
    }

    public Processes readProcesses() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.PROCESSES_FILES, Processes.class);
    }

    public Sequences readSequences() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.SEQUENCES_FILES, Sequences.class);
    }
}
