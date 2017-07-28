package com.goalsmaster.goalsmaster.events;

import com.goalsmaster.goalsmaster.data.Goal;
import com.goalsmaster.goalsmaster.data.Task;

/**
 * Created by tudor on 5/8/2017.
 */

public class GoalFragmentEvent {
    public final Goal goal;

    public GoalFragmentEvent(Goal goal) {
        this.goal = goal;
    }
}
