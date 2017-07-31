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

import java.util.ArrayList;

/**
 * Created by tudor on 5/8/2017.
 */

public class TaskViewAdapter extends BaseFirebaseRecyclerAdapter<Task, TaskViewHolder> {

    private static final String TAG = TaskViewAdapter.class.getSimpleName();
    //private final Context context;

    public TaskViewAdapter(Context context, int rowLayoutResourceId) {
        super(
                Task.class,
                rowLayoutResourceId,
                TaskViewHolder.class,
                /*FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://goalsmaster-e6340.firebaseio.com/Tasks/Zy90DT3yY5ZBOlx62PjiTIT8Oig2")
                        .orderByKey()*/
                Tasks.getFirebaseNodeRef()
                        .orderByChild("timestamp")
                        .startAt(Double.MIN_VALUE)
                        .endAt(Double.MAX_VALUE)
        );

            /*Query query = Tasks.getFirebaseNodeRef(getContext())
                    .orderByChild("timestamp")
                    .startAt(Double.MIN_VALUE)
                    .endAt(Double.MAX_VALUE);*/

        //this.context = context;
    }

    @Override
    public BaseVisualState createVisualState() {
        return new TaskVisualState();
    }

    @Override
    public void deleteObject(Object object) {
        /*for(int i=0; i<getItemCount(); i++) {
            // TODO change the object from VisualState fetching method, it is inefficient for tons of rows
            Task source = (Task)object;
            Task target = (Task)getItem(i);
            Log.d(TAG, "Target <> Source" + String.valueOf(target) + " <> " + String.valueOf(source) );
            if(source.equals(target)) {
                getRef(i).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError != null)
                            EventBus.getDefault().post(new ToastMessage(databaseError.getMessage()));
                        else
                            EventBus.getDefault().post(new ToastMessage("Deleted: " + databaseReference.toString()));
                    }
                });
                break;
            }
        }*/
    }

    @Override
    public void deleteSelected() {

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


    public void queryData() {
//        DatabaseReference tasksRef = Tasks.getFirebaseNodeRef(context);
//        tasksRef.orderByChild("timestamp")
//                .startAt(filterDateFrom.getTime())
//                .endAt(filterDateTo.getTime())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot tasksSnapshot) {
//                    if (tasksSnapshot.exists()) {
//                        data.clear();
//                        for (DataSnapshot issue : tasksSnapshot.getChildren()) {
//                            GenericTypeIndicator<Task> t = new GenericTypeIndicator<Task>() {};
//                            Task task = issue.getValue(t);
//                            data.add(task);
//                        }
//                        dataState = new HashMap<Task, TaskViewHolder.TaskVisualState>(data.size());
//                        for (int i = 0; i < data.size(); i++) {
//                            TaskViewHolder.TaskVisualState visualState = new TaskViewHolder.TaskVisualState();
//                            Task task = data.get(i);
//                            int val = Arrays.binarySearch(oldSelections, (int)task.getId().hashCode());
//                            if(val >= 0) {
//                                visualState.onoff = true;
//                            }
//                            dataState.put(task, visualState);
//                        }
//                        notifyItemRangeChanged(0, data.size());
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    data.clear();
//                    dataState = new HashMap<Task, TaskViewHolder.TaskVisualState>(0);
//                    notifyDataSetChanged();
//                }
//        });
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
