package com.goalsmaster.goalsmaster.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.activities.BaseActivity;
import com.goalsmaster.goalsmaster.data.Task;
import com.goalsmaster.goalsmaster.data.Tasks;
import com.goalsmaster.goalsmaster.holders.TaskViewHolder;
import com.goalsmaster.goalsmaster.rest.GoalApi;
import com.goalsmaster.goalsmaster.rest.RestApi;
import com.goalsmaster.goalsmaster.rest.TaskApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tudor on 5/8/2017.
 */

public class TaskViewAdapter extends BaseAdapter {

    private static final String KEY_EDIT_MODE = "EDIT_MODE";
    private static final String KEY_SELECTIONS = "SELECTIONS";
    private static final String TAG = TaskViewAdapter.class.getSimpleName();
    private final Context context;
    private List<Task> data;
    private boolean editMode;
    private HashMap<Task, TaskViewHolder.VisualState> dataState;
    private int[] oldSelections = new int[0];
    private Date filterDateFrom;
    private Date filterDateTo;

    public TaskViewAdapter(Context context) {
        super(TaskViewHolder.class);
        this.context = context;
        data = new ArrayList<>();
        filterDateFrom = new Date(116,11,31,0,0,0);
        filterDateTo = new Date(117,11,31,0,0,0);
        queryData();
    }

    @Override
    protected Object getItem(int position) {
        return data.get(position);
    }

    @Override
    protected Object getState(Object item) {
        return dataState.get(item);
    }

    public List<Task> getSelected() {
        List<Task> res = new ArrayList<>();
        if(dataState == null)
            return res;
        for(Task task: dataState.keySet()) {
            if(dataState.get(task).onoff == true)
                res.add(task);
        }
        return res;
    }

    public int getItemPosition(Task selected) {
        return data.indexOf(selected);
    }

    public void remove(int position) {
        Task task = data.remove(position);
        dataState.remove(task);
        notifyItemRemoved(position);
    }

    public int getSelectedCount() {
        int k = 0;
        for(Task task: dataState.keySet()) {
            if(dataState.get(task).onoff == true)
                k++;
        }
        return k;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_EDIT_MODE, editMode);
        ArrayList<Integer> selectionList = new ArrayList<>();
        List<Task> list = getSelected();
        for(Task t: list)
            selectionList.add(t.getId().hashCode());
        outState.putIntegerArrayList(KEY_SELECTIONS, selectionList);
    }

    public void restoreState(Bundle savedInstanceState) {
        editMode = savedInstanceState.getBoolean(KEY_EDIT_MODE, false);
        ArrayList<Integer> list = savedInstanceState.getIntegerArrayList(KEY_SELECTIONS);
        oldSelections = new int[list.size()];
        for(int i=0; i<list.size(); i++) {
            oldSelections[i] = list.get(i);
        }
        Arrays.sort(oldSelections);
    }


    public void queryData() {
        DatabaseReference tasksRef = Tasks.getFirebaseNodeRef(context);
        tasksRef.orderByChild("timestamp")
                .startAt(filterDateFrom.getTime())
                .endAt(filterDateTo.getTime())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot tasksSnapshot) {
                    if (tasksSnapshot.exists()) {
                        data.clear();
                        for (DataSnapshot issue : tasksSnapshot.getChildren()) {
                            GenericTypeIndicator<Task> t = new GenericTypeIndicator<Task>() {};
                            Task task = issue.getValue(t);
                            data.add(task);
                        }
                        dataState = new HashMap<Task, TaskViewHolder.VisualState>(data.size());
                        for (int i = 0; i < data.size(); i++) {
                            TaskViewHolder.VisualState visualState = new TaskViewHolder.VisualState();
                            Task task = data.get(i);
                            int val = Arrays.binarySearch(oldSelections, (int)task.getId().hashCode());
                            if(val >= 0) {
                                visualState.onoff = true;
                            }
                            dataState.put(task, visualState);
                        }
                        notifyItemRangeChanged(0, data.size());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    data.clear();
                    dataState = new HashMap<Task, TaskViewHolder.VisualState>(0);
                    notifyDataSetChanged();
                }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean getEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        for(TaskViewHolder.VisualState state: dataState.values()) {
            state.onoff = false;
            state.switchShown = editMode;
        }
        notifyItemRangeChanged(0, data.size());
    }

    public int selectAll(boolean state) {
        int k = 0;
        for(TaskViewHolder.VisualState visualState: dataState.values()) {
            visualState.onoff = state;
            k++;
        }
        return k;
    }

    public void setDateFilters(Date dateFrom, Date dateTo) {
        this.filterDateFrom = dateFrom;
        this.filterDateTo = dateTo;
    }

    public List<Task> getList() {
        return data;
    }
}
