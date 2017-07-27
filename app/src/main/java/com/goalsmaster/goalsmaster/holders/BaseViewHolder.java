package com.goalsmaster.goalsmaster.holders;

import android.view.View;
/**
 * Created by tudor on 5/13/2017.
 */

public abstract class BaseViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(Object item, Object state);
}
