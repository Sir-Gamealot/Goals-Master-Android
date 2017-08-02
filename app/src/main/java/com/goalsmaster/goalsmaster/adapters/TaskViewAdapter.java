package com.goalsmaster.goalsmaster.adapters;

import android.content.Context;
import android.util.Log;

import com.goalsmaster.goalsmaster.data.Task;
import com.goalsmaster.goalsmaster.data.Tasks;
import com.goalsmaster.goalsmaster.events.ToastMessage;
import com.goalsmaster.goalsmaster.holders.visualstate.BaseVisualState;
import com.goalsmaster.goalsmaster.holders.TaskViewHolder;
import com.goalsmaster.goalsmaster.holders.visualstate.TaskVisualState;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudor on 5/8/2017.
 */

public class TaskViewAdapter extends BaseFirebaseRecyclerAdapter<Task, TaskViewHolder> {

    private static final String TAG = TaskViewAdapter.class.getSimpleName();

    /**
     * @param rowLayoutResourceId
     * @param fromTimestamp
     * @param toTimestamp
     */
    public TaskViewAdapter(int rowLayoutResourceId, double fromTimestamp, double toTimestamp) {
        super(
                Task.class,
                rowLayoutResourceId,
                TaskViewHolder.class,
                Tasks.getFirebaseNodeRef()
                        .orderByChild("timestamp")
                        .startAt(fromTimestamp)
                        .endAt(toTimestamp)
        );
    }

    @Override
    public BaseVisualState createVisualState() {
        return new TaskVisualState();
    }

    @Override
    public synchronized void deleteObject(Object object) {
        assert (object != null) && (object instanceof DatabaseReference);
        DatabaseReference ref = (DatabaseReference) object;
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    EventBus.getDefault().post(new ToastMessage(databaseError.getMessage()));
                else
                    EventBus.getDefault().post(new ToastMessage("Deleted: " + databaseReference.toString()));
            }
        });
    }

    @Override
    public void deleteSelected() {
        List<WeakReference> selectedArray = getSelected();

        if(selectedArray.size() == 0) {
            EventBus.getDefault().post(new ToastMessage("No items selected."));
            return;
        }

        for(WeakReference<Task> weakReference: selectedArray) {
            Task task = weakReference.get();
            if(task != null)
                deleteObject(Tasks.getFirebaseItemNodeRef(task.getId()));
        }
    }

    /*@Override
    public Task getItem(int position) {
        return data.get(position);
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

//    @Override
//    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent, false);
//        return new TaskViewHolder(view);
//    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setDateFilters(Date dateFrom, Date dateTo) {
        this.filterDateFrom = dateFrom;
        this.filterDateTo = dateTo;
    }

    public List<Task> getList() {
        return data;
    }*/
}
