package com.goalsmaster.goalsmaster.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.goalsmaster.goalsmaster.holders.BaseViewHolder;

/**
 * Created by tudor on 5/11/2017.
 */

public abstract class BaseAdapter<DataObject,ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private Class viewHolderClass;

    protected BaseAdapter(Class viewHolderClass) {
        this.viewHolderClass = viewHolderClass;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            return (RecyclerView.ViewHolder) viewHolderClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = getItem(position);
        Object state = getState(item);
        //if(item != null && state != null)
            ((BaseViewHolder)holder).bind(item, state);
    }

    protected abstract Object getItem(int position);
    protected abstract Object getState(Object item);
}
