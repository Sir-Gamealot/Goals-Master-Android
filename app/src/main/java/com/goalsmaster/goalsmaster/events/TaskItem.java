package com.goalsmaster.goalsmaster.events;

import com.goalsmaster.goalsmaster.data.Task;

/**
 * Created by tudor on 5/8/2017.
 */

public class TaskItem {
    private Task task;
    public TaskItem(Task task) {
        this.task = new Task(task.getId(), task.getUserId(), task.getTitle(), task.getDescription(), task.getDate(), task.getDuration(), task.getLatitude(), task.getLongitude());
    }

    /**
     *
     * @return a Task object, its existance in the db is not guaranteed (could have been deleted, changed before this event was
     *              delivered to the subscribers.
     */
    public Task getTask() {
       return task;
    }
}
