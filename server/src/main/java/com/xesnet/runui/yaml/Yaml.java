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

    private static Yaml instance;

    private final YamlContext yamlContext;

    private Yaml(Path configPath) {
        yamlContext = new YamlContext(configPath);
    }

    public static Yaml init(Path configPath) {
        instance = new Yaml(configPath);
        return instance;
    }

    public static Yaml getInstance() {
        return instance;
    }

    public Config readConfig() {
        return yamlContext.read(Yaml.CONFIG_FILE, Config.class);
    }

    public List<User> readUsers() {
        return yamlContext.read(Yaml.USERS_FILE, new TypeReference<List<User>>() {

        });
    }
}
