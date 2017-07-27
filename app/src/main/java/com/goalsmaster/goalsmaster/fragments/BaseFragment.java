package com.goalsmaster.goalsmaster.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.goalsmaster.goalsmaster.events.AllowedActionsChanged;
import com.goalsmaster.goalsmaster.other.FabMenu;
import com.goalsmaster.goalsmaster.other.FragmentTypes;

import org.greenrobot.eventbus.EventBus;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by tudor on 5/8/2017.
 */

public abstract class BaseFragment extends Fragment {

    public static final String TAG = BaseFragment.class.getSimpleName();

    // Keys for retrieving data from temporary bundle
    public static final String S_ITEM_ID = "S_ITEM_ID"; // The id of the item (row)
    public static final String S_ITEM = "S_ITEM";
    protected FabMenu[] allowedOperations;
    private Bundle bundle;

    public BaseFragment() {
        super();
    }

    public abstract FragmentTypes getFragmentType();

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new AllowedActionsChanged(allowedOperations));
        Crouton.cancelAllCroutons();
    }

    @Override
    public void onPause() {
        Crouton.cancelAllCroutons();
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void setExtraData(Bundle bundle) {
        this.bundle = bundle;
    }
}
