package com.goalsmaster.goalsmaster.events;

import com.goalsmaster.goalsmaster.other.FabMenu;

/**
 * Created by tudor on 5/8/2017.
 */

public class AllowedActionsChanged {
    public FabMenu[] allowed;

    public AllowedActionsChanged(FabMenu[] allowed) {
        this.allowed = allowed;
    }
}
