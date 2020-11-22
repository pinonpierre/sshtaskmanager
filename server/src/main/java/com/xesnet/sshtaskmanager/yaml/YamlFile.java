package com.xesnet.sshtaskmanager.yaml;

import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.model.Processes;
import com.xesnet.sshtaskmanager.model.Sequences;
import com.xesnet.sshtaskmanager.model.Servers;
import com.xesnet.sshtaskmanager.model.Users;


/**
 * @author Pierre PINON
 */
public enum YamlFile {
    CONFIG("config.yaml", Config.class, true),
    USERS("users.yaml", Users.class, true),
    SERVERS("servers.yaml", Servers.class),
    PROCESSES("processes.yaml", Processes.class),
    SEQUENCES("sequences.yaml", Sequences.class);

    private final String file;
    private final Class<?> clazz;
    private final boolean createIfNotExist;

    YamlFile(String file, Class<?> clazz) {
        this(file, clazz, false);
    }

    YamlFile(String file, Class<?> clazz, boolean createIfNotExist) {
        this.file = file;
        this.clazz = clazz;
        this.createIfNotExist = createIfNotExist;
    }

    public String getFile() {
        return file;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean isCreateIfNotExist() {
        return createIfNotExist;
    }
}
