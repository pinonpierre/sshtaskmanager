package com.xesnet.sshtaskmanager.model;

/**
 * @author Pierre PINON
 */
public enum RunState {
    INIT,
    CONNECT,
    SUBMIT,
    SUCCESS,
    FAILED,
    TIMEOUT
}