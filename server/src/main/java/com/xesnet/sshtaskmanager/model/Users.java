package com.xesnet.sshtaskmanager.model;

import java.util.Collections;
import java.util.List;


/**
 * @author Pierre PINON
 */
public class Users {

    private List<User> users;

    public Users() {
        User user = new User();
        user.setLogin("admin");
        user.setPassword("admin");
        user.setAdmin(true);

        users = Collections.singletonList(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
