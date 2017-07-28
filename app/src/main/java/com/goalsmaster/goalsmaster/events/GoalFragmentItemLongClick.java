package com.goalsmaster.goalsmaster.events;

import com.goalsmaster.goalsmaster.data.Goal;

/**
 * Created by tudor on 5/8/2017.
 */

public class GoalFragmentItemLongClick {
    public final Goal goal;
    public GoalFragmentItemLongClick(Goal goal) {
        this.goal = goal;
    }
}
