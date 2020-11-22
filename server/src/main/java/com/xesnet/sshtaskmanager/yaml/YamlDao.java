package com.xesnet.sshtaskmanager.yaml;

import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.model.Processes;
import com.xesnet.sshtaskmanager.model.Sequences;
import com.xesnet.sshtaskmanager.model.Servers;
import com.xesnet.sshtaskmanager.model.Users;

import java.nio.file.Path;


/**
 * @author Pierre PINON
 */
public class YamlDao {

    private final Path configPath;
    private YamlContext yamlContext;

    public YamlDao(Path configPath) {
        this.configPath = configPath;
    }

    public void init() throws YamlContext.YamlContextException {
        yamlContext = new YamlContext(configPath);

        for (YamlFile yamlFile : YamlFile.values()) {
            yamlContext.read(yamlFile.getFile(), yamlFile.getClazz(), yamlFile.isCreateIfNotExist());
        }
    }

    public Config readConfig() throws YamlContext.YamlContextException {
        return yamlContext.read(YamlFile.CONFIG.getFile(), Config.class);
    }

    public Users readUsers() throws YamlContext.YamlContextException {
        return yamlContext.read(YamlFile.USERS.getFile(), Users.class);
    }

    public Servers readServers() throws YamlContext.YamlContextException {
        return yamlContext.read(YamlFile.SERVERS.getFile(), Servers.class);
    }

    public Processes readProcesses() throws YamlContext.YamlContextException {
        return yamlContext.read(YamlFile.PROCESSES.getFile(), Processes.class);
    }

    public Sequences readSequences() throws YamlContext.YamlContextException {
        return yamlContext.read(YamlFile.SEQUENCES.getFile(), Sequences.class);
    }
}
