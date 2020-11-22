package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public enum SequenceRunState {
    INIT,
    SUBMIT,
    SUCCESS,
    FAILED,
    NO_MULTIPLE_RUN;

    public boolean isDone() {
        return this == SUCCESS || this == FAILED || this == NO_MULTIPLE_RUN;
    }
}