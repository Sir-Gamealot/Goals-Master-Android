package com.goalsmaster.goalsmaster.events;

/**
 * Created by tudor on 7/29/2017.
 */

public class LogOffRequest {
    public boolean smartLockState;

    public LogOffRequest() {
        smartLockState = true;
    }

    public LogOffRequest(boolean smartLockState) {
        this.smartLockState = smartLockState;
    }
}
