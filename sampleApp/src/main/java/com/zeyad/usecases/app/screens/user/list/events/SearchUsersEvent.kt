package com.zeyad.usecases.app.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

/**
 * @author by ZIaDo on 4/20/17.
 */
@Parcelize
class SearchUsersEvent(private val query: String) : BaseEvent<String> {

    override fun getPayLoad(): String = query
}
