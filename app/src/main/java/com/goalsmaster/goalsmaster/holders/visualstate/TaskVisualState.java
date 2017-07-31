package com.goalsmaster.goalsmaster.holders.visualstate;

import com.goalsmaster.goalsmaster.holders.visualstate.BaseVisualState;

/**
 * Created by tudor on 7/31/2017.
 */
public class TaskVisualState extends BaseVisualState {
    public boolean onoff;
    public boolean switchShown;

    public TaskVisualState() {
        onoff = false;
        switchShown = false;
    }

    @Override
    public void setEdit(boolean state) {
        onoff = false;
        switchShown = state;
    }

    @Override
    public void select(boolean state) {
        onoff = state;
    }

    @Override
    public boolean isSelected() {
        return onoff;
    }
}
