package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public class User {

    private String login;
    private String password;
    private boolean admin = false;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
