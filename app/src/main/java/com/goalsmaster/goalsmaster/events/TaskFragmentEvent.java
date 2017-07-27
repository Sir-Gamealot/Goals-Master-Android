package com.goalsmaster.goalsmaster.events;

import com.goalsmaster.goalsmaster.data.Task;

/**
 * Created by tudor on 5/8/2017.
 */

public class TaskFragmentEvent {
    public final Task task;

    public TaskFragmentEvent(Task task) {
        this.task = task;
    }
}
