package com.xesnet.sshtaskmanager;

import com.xesnet.sshtaskmanager.model.Process;
import com.xesnet.sshtaskmanager.model.Sequence;
import com.xesnet.sshtaskmanager.model.Server;
import com.xesnet.sshtaskmanager.model.User;
import com.xesnet.sshtaskmanager.yaml.YamlDao;
import com.xesnet.sshtaskmanager.yaml.YamlContext;


/**
 * @author Pierre PINON
 */
public class Backend {

    private final YamlDao yamlDao;

    public Backend(YamlDao yamlDao) {
        this.yamlDao = yamlDao;
    }

    public Process getProcess(String name) throws YamlContext.YamlContextException {
        return yamlDao.readProcesses().getProcesses().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public User getUser(String login, String password) throws YamlContext.YamlContextException {
        return yamlDao.readUsers().getUsers().stream()
                .filter(u -> u.getLogin().equals(login) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public Sequence getSequence(String name) throws YamlContext.YamlContextException {
        return yamlDao.readSequences().getSequences().stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Server getServer(String name) throws YamlContext.YamlContextException {
        return yamlDao.readServers().getServers().stream()
                .filter(ss -> ss.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
