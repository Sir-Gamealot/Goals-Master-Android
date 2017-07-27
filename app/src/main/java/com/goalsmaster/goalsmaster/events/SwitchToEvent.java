package com.goalsmaster.goalsmaster.events;

import android.os.Bundle;

import com.goalsmaster.goalsmaster.other.FragmentTypes;

/**
 * Created by tudor on 5/8/2017.
 */

public class SwitchToEvent {
    public final FragmentTypes type;
    public SwitchToEvent(FragmentTypes type) {
        this.type = type;
    }
}
