package com.goalsmaster.goalsmaster.events;

/**
 * Created by tudor on 5/7/2017.
 */

public class ToastMessage {
    public String message;
    public Type type;

    public ToastMessage(String message) {
        this.message = message;
        this.type = Type.INFO;
    }

    public ToastMessage(String message, Type type) {
        this.message = message;
        this.type = type;
    }

    public enum Type {
        INFO,
        ALERT
    }
}
