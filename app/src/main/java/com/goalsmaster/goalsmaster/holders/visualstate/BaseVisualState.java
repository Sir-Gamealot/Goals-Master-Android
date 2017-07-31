package com.goalsmaster.goalsmaster.holders.visualstate;

/**
 * Created by tudor on 7/31/2017.
 */

public abstract class BaseVisualState {
    public abstract void setEdit(boolean state);
    public abstract void select(boolean state);
    public abstract boolean isSelected();
}
