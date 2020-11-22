package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public enum ProcessRunState {
    INIT,
    CONNECT,
    SUBMIT,
    SUCCESS,
    FAILED,
    TIMEOUT,
    NO_MULTIPLE_RUN;

    public boolean isDone() {
        return this == SUCCESS || this == FAILED || this == TIMEOUT || this == NO_MULTIPLE_RUN;
    }
}