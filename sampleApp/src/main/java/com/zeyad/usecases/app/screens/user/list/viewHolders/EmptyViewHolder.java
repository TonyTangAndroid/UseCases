package com.zeyad.usecases.app.screens.user.list.viewHolders;

import android.view.View;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;


/** @author zeyad on 11/29/16. */
public class EmptyViewHolder extends GenericRecyclerViewAdapter.ViewHolder {

    public EmptyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {}

    @Override
    public void expand(boolean isExpanded) {}
}
