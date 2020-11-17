package com.xesnet.runui.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xesnet.runui.model.Config;
import com.xesnet.runui.model.User;

import java.nio.file.Path;
import java.util.List;


/**
 * @author Pierre PINON
 */
public class Yaml {

    private static final String CONFIG_FILE = "config.yaml";
    private static final String USERS_FILE = "users.yaml";

    private final YamlContext yamlContext;

    public Yaml(Path configPath) {
        yamlContext = new YamlContext(configPath);
    }

    public Config readConfig() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.CONFIG_FILE, Config.class);
    }

    public List<User> readUsers() throws YamlContext.YamlContextException {
        return yamlContext.read(Yaml.USERS_FILE, new TypeReference<List<User>>() {

        });
    }
}
