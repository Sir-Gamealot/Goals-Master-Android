package com.goalsmaster.goalsmaster.events;

/**
 * Created by tudor on 5/8/2017.
 */

public class SwitchStateChangedEvent {
    public boolean state;
    public SwitchStateChangedEvent(boolean state) {
        this.state = state;
    }
}
