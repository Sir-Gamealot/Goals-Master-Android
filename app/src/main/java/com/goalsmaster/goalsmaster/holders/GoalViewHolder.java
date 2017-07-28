package com.goalsmaster.goalsmaster.holders;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.data.Goal;
import com.goalsmaster.goalsmaster.events.SwitchStateChangedEvent;
import com.goalsmaster.goalsmaster.events.GoalFragmentEvent;
import com.goalsmaster.goalsmaster.events.GoalFragmentItemLongClick;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by tudor on 5/13/2017.
 */

public class GoalViewHolder extends BaseViewHolder {
    public Goal goal;
    public VisualState state;

    @BindView(R.id.row_main)
    public View view;
    @BindView(R.id.onoff)
    public SwitchCompat onoff;
    @BindView(R.id.id)
    public TextView id;
    @BindView(R.id.title)
    public TextView title;
    @BindView(R.id.description)
    public TextView description;
    @BindView(R.id.location)
    public ImageView location;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM d,\nyyyy");

    public static class VisualState {
        public boolean onoff;
        public boolean switchShown;

        public VisualState() {
            onoff = false;
            switchShown = false;
        }
    }

    public GoalViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Object item, Object state) {
        this.goal = (Goal) item;
        this.state = (VisualState) state;
        //holder.id.setText(String.valueOf(holder.goal.getId()));
        id.setText(sdf.format(goal.getDate()));

        title.setText((goal.getTitle() != null ? goal.getTitle() : "Unknown title"));
        description.setText((goal.getDescription() != null ? goal.getDescription() : "Unknown description"));
        location.setImageResource(R.drawable.ic_location);
        if(((VisualState) state).switchShown == true) {
            if(onoff.getVisibility() != View.VISIBLE)
                onoff.setVisibility(View.VISIBLE);
        } else {
            if(onoff.getVisibility() != View.GONE)
                onoff.setVisibility(View.GONE);
        }
        onoff.setChecked(((VisualState) state).onoff);
        int bgColor = R.drawable.bg_row_red;
        view.setBackgroundResource(bgColor);
    }

    @OnClick(R.id.row_main)
    public void onMainViewClick(View sender) {
        if (EventBus.getDefault().hasSubscriberForEvent(GoalFragmentEvent.class)) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            EventBus.getDefault().post(new GoalFragmentEvent(goal));
        }
    }

    @OnLongClick(R.id.row_main)
    public boolean onMainViewLongClick(View sender) {
        if (EventBus.getDefault().hasSubscriberForEvent(GoalFragmentItemLongClick.class)) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            EventBus.getDefault().post(new GoalFragmentItemLongClick(goal));
        }
        return true;
    }

    @OnClick(R.id.onoff)
    public void onSwitchClick(View sender) {
        state.onoff = onoff.isChecked();
        EventBus.getDefault().post(new SwitchStateChangedEvent(state.onoff));
    }
}
