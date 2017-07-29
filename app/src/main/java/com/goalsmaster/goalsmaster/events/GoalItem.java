package com.goalsmaster.goalsmaster.events;

import com.goalsmaster.goalsmaster.data.Goal;

/**
 * Created by tudor on 5/8/2017.
 */

public class GoalItem {
    private Goal goal;
    public GoalItem(Goal goal) {
        this.goal = new Goal(goal.getId(), goal.getUserId(), goal.getTitle(),
                goal.getDescription(), goal.getDate(), goal.getPriority(), goal.getPhotoId());
    }

    /**
     *
     * @return a Goal object, its existance in the db is not guaranteed (could have been deleted, changed before this event was
     *              delivered to the subscribers.
     */
    public Goal getGoal() {
       return goal;
    }
}
