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
    TIMEOUT
}