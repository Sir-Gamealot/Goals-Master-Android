package com.goalsmaster.goalsmaster.adapters;

import android.support.annotation.LayoutRes;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.goalsmaster.goalsmaster.data.Task;
import com.goalsmaster.goalsmaster.holders.BaseViewHolder;
import com.goalsmaster.goalsmaster.holders.visualstate.BaseVisualState;
import com.google.firebase.database.Query;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tudor on 7/30/2017.
 */

public abstract class BaseFirebaseRecyclerAdapter<T, VH extends BaseViewHolder> extends FirebaseRecyclerAdapter<T, VH> {

    private static final String KEY_EDIT_MODE = "EDIT_MODE";
    private static final String KEY_SELECTIONS = "SELECTIONS";
    private boolean editMode;
    private HashMap<T, BaseVisualState> dataState;
    private int[] oldSelections = new int[0];
    private Date filterDateFrom;
    private Date filterDateTo;

    public BaseFirebaseRecyclerAdapter(ObservableSnapshotArray<T> dataSnapshots, @LayoutRes int modelLayout, Class<VH> viewHolderClass) {
        super(dataSnapshots, modelLayout, viewHolderClass);
        init();
    }

    public BaseFirebaseRecyclerAdapter(SnapshotParser<T> parser, @LayoutRes int modelLayout, Class<VH> viewHolderClass, Query query) {
        super(parser, modelLayout, viewHolderClass, query);
        init();
    }

    public BaseFirebaseRecyclerAdapter(Class<T> modelClass, @LayoutRes int modelLayout, Class<VH> viewHolderClass, Query query) {
        super(modelClass, modelLayout, viewHolderClass, query);
        init();
    }

    @Override
    protected void populateViewHolder(VH viewHolder, T item, int position) {
        viewHolder.bind(item, getState(item));
    }

    private void init() {
        dataState = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, -1);
        filterDateFrom = calendar.getTime();
        calendar.roll(Calendar.MONTH, 2);
        filterDateTo = calendar.getTime();
    }

    public BaseVisualState getState(T t) {
        BaseVisualState baseVisualState = dataState.get(t);
        if(baseVisualState == null) {
            baseVisualState = createVisualState();
            dataState.put(t, baseVisualState); // TODO remove memory leak caused by never removing items from visualState array.
        }
        return baseVisualState;
    }

    public abstract BaseVisualState createVisualState();

    public boolean getEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        for(BaseVisualState state: dataState.values()) {
            state.setEdit(editMode);
        }
        notifyDataSetChanged();
    }

    public int selectAll(boolean state) {
        int k = 0;
        for(BaseVisualState visualState: dataState.values()) {
            visualState.select(state);
            k++;
        }
        return k;
    }

    public int getSelectedCount() {
        int k = 0;
        for(BaseVisualState visualState: dataState.values()) {
            if(visualState.isSelected())
                k++;
        }
        return k;
    }

    public List<WeakReference> getSelected() {
        ArrayList<WeakReference> list = new ArrayList<>();
        for(Object obj: dataState.keySet()) {
            list.add(new WeakReference(obj));
        }
        return list;
    }

    public abstract void deleteObject(Object object);

    public abstract void deleteSelected();
}
