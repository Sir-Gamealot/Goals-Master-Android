package com.goalsmaster.goalsmaster.holders;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.data.Task;
import com.goalsmaster.goalsmaster.events.SwitchStateChangedEvent;
import com.goalsmaster.goalsmaster.events.TaskFragmentEvent;
import com.goalsmaster.goalsmaster.events.TaskFragmentItemLongClick;
import com.goalsmaster.goalsmaster.holders.visualstate.TaskVisualState;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by tudor on 5/13/2017.
 */

public class TaskViewHolder extends BaseViewHolder {
    public Task task;
    public TaskVisualState state;

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

    public TaskViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Object item, Object state) {
        this.task = (Task) item;
        this.state = (TaskVisualState) state;
        //holder.id.setText(String.valueOf(holder.task.getId()));
        id.setText(sdf.format(task.getTimestamp()));

        title.setText((task.getTitle() != null ? task.getTitle() : "Unknown title"));
        description.setText((task.getDescription() != null ? task.getDescription() : "Unknown description"));
        location.setImageResource(R.drawable.ic_location);
        if(((TaskVisualState) state).switchShown == true) {
            if(onoff.getVisibility() != View.VISIBLE)
                onoff.setVisibility(View.VISIBLE);
        } else {
            if(onoff.getVisibility() != View.GONE)
                onoff.setVisibility(View.GONE);
        }
        onoff.setChecked(((TaskVisualState) state).onoff);
        int bgColor = R.drawable.bg_row_green;
        if (task.getDuration() > 8*3600)
            bgColor = R.drawable.bg_row_red;
        view.setBackgroundResource(bgColor);
    }

    @OnClick(R.id.row_main)
    public void onMainViewClick(View sender) {
        if (EventBus.getDefault().hasSubscriberForEvent(TaskFragmentEvent.class)) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            EventBus.getDefault().post(new TaskFragmentEvent(task));
        }
    }

    @OnLongClick(R.id.row_main)
    public boolean onMainViewLongClick(View sender) {
        if (EventBus.getDefault().hasSubscriberForEvent(TaskFragmentItemLongClick.class)) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            EventBus.getDefault().post(new TaskFragmentItemLongClick(task));
        }
        return true;
    }

    @OnClick(R.id.onoff)
    public void onSwitchClick(View sender) {
        state.onoff = onoff.isChecked();
        EventBus.getDefault().post(new SwitchStateChangedEvent(state.onoff));
    }
}
