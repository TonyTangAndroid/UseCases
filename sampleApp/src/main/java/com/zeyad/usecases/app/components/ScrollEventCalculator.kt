package com.zeyad.usecases.app.components

import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent

object ScrollEventCalculator {

    /**
     * Determine if the scroll event at the end of the recycler view.
     *
     * @return true if at end of linear list recycler view, false otherwise.
     */
    fun isAtScrollEnd(recyclerViewScrollEvent: RecyclerViewScrollEvent): Boolean {
        val layoutManager = recyclerViewScrollEvent.view().layoutManager
        return if (layoutManager is LinearLayoutManager) {
            layoutManager.itemCount <= layoutManager.findLastVisibleItemPosition() + 2
        } else false
    }
}
