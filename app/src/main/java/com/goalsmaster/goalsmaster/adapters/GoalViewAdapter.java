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
import com.goalsmaster.goalsmaster.data.Goal;
import com.goalsmaster.goalsmaster.data.Goals;
import com.goalsmaster.goalsmaster.data.Task;
import com.goalsmaster.goalsmaster.holders.GoalViewHolder;
import com.goalsmaster.goalsmaster.holders.TaskViewHolder;
import com.goalsmaster.goalsmaster.rest.GoalApi;
import com.goalsmaster.goalsmaster.rest.RestApi;
import com.goalsmaster.goalsmaster.rest.TaskApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

//import com.goalsmaster.goalsmaster.activities.LoginActivity;

/**
 * Created by tudor on 5/8/2017.
 */

public class GoalViewAdapter extends BaseAdapter {

    private static final String KEY_EDIT_MODE = "EDIT_MODE";
    private static final String KEY_SELECTIONS = "SELECTIONS";
    private static final String TAG = GoalViewAdapter.class.getSimpleName();
    private final Context context;
    private List<Goal> data;
    private boolean editMode;
    private HashMap<Goal, GoalViewHolder.VisualState> dataState;
    private int[] oldSelections = new int[0];
    private Date filterDateFrom;
    private Date filterDateTo;

    public GoalViewAdapter(Context context) {
        super(GoalViewHolder.class);
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

    public List<Goal> getSelected() {
        List<Goal> res = new ArrayList<>();
        if(dataState == null)
            return res;
        for(Goal goal: dataState.keySet()) {
            if(dataState.get(goal).onoff == true)
                res.add(goal);
        }
        return res;
    }

    public int getItemPosition(Goal selected) {
        return data.indexOf(selected);
    }

    public void remove(int position) {
        Goal goal = data.remove(position);
        dataState.remove(goal);
        notifyItemRemoved(position);
    }

    public int getSelectedCount() {
        int k = 0;
        for(Goal goal: dataState.keySet()) {
            if(dataState.get(goal).onoff == true)
                k++;
        }
        return k;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_EDIT_MODE, editMode);
        ArrayList<Integer> selectionList = new ArrayList<>();
        List<Goal> list = getSelected();
        for(Goal t: list)
            selectionList.add((int) t.getId().hashCode());
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
        DatabaseReference goalsRef = Goals.getFirebaseNodeRef(context);
        goalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot goalsSnapshot) {
                if (goalsSnapshot.exists()) {
                    data.clear();
                    for (DataSnapshot issue : goalsSnapshot.getChildren()) {
                        GenericTypeIndicator<Goal> t = new GenericTypeIndicator<Goal>() {};
                        Goal goal = issue.getValue(t);
                        data.add(goal);
                    }
                    dataState = new HashMap<Goal, GoalViewHolder.VisualState>(data.size());
                    for (int i = 0; i < data.size(); i++) {
                        GoalViewHolder.VisualState visualState = new GoalViewHolder.VisualState();
                        Goal goal = data.get(i);
                        int val = Arrays.binarySearch(oldSelections, (int)goal.getId().hashCode());
                        if(val >= 0) {
                            visualState.onoff = true;
                        }
                        dataState.put(goal, visualState);
                    }
                    notifyItemRangeChanged(0, data.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                data.clear();
                dataState = new HashMap<Goal, GoalViewHolder.VisualState>(0);
                notifyDataSetChanged();
            }
        });
    }

    /*public void queryData() {
        GoalApi api = RestApi.getGoalApi(context);
        long id;
        // if(LoginActivity.isLoggedUserSuper())
        //  id = 0;
        // else
        //  id = LoginActivity.getLoggedInUserId();
        Call<List<Goal>> call = api.findGoalByDateRange(0, filterDateFrom, filterDateTo);
        call.enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if(response.body() != null) {
                    data.clear();
                    data.addAll(response.body());
                    //setCountedHoursPerDay(data);
                    dataState = new HashMap<Goal, GoalViewHolder.VisualState>(data.size());
                    for (int i = 0; i < data.size(); i++) {
                        GoalViewHolder.VisualState visualState = new GoalViewHolder.VisualState();
                        Goal goal = data.get(i);
                        int val = Arrays.binarySearch(oldSelections, (int)goal.getId().hashCode());
                        if(val >= 0) {
                            visualState.onoff = true;
                        }
                        dataState.put(goal, visualState);
                    }
                    notifyItemRangeChanged(0, data.size());
                } else {
                    data.clear();
                    dataState = new HashMap<Goal, GoalViewHolder.VisualState>(0);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                data.clear();
                dataState = new HashMap<Goal, GoalViewHolder.VisualState>(0);
                notifyDataSetChanged();
            }
        });
    }*/

    /*private void setCountedHoursPerDay(List<Goal> data) {
        if(data.size() == 0)
            return;

        HashMap<Date, Long> map = new HashMap<>();
        for(Goal t: data) {
            long duration = 0;
            if(map.keySet().contains(t.getDate()))
                duration = map.get(t.getDate());
            map.put(t.getDate(), t.getDuration() + duration);
        }

        for(Goal t: data) {
            t.setHoursPerDay(map.get(t.getDate()));
        }
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_goal, parent, false);
        return new GoalViewHolder(view);
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
        for(GoalViewHolder.VisualState state: dataState.values()) {
            state.onoff = false;
            state.switchShown = editMode;
        }
        notifyItemRangeChanged(0, data.size());
    }

    public int selectAll(boolean state) {
        int k = 0;
        for(GoalViewHolder.VisualState visualState: dataState.values()) {
            visualState.onoff = state;
            k++;
        }
        return k;
    }

    public void setDateFilters(Date dateFrom, Date dateTo) {
        this.filterDateFrom = dateFrom;
        this.filterDateTo = dateTo;
    }

    public List<Goal> getList() {
        return data;
    }
}
