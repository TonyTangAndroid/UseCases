package com.zeyad.usecases.app.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

/**
 * @author by ZIaDo on 4/19/17.
 */
@Parcelize
class GetPaginatedUsersEvent(private val lastId: Long) : BaseEvent<Long> {
    override fun getPayLoad(): Long = lastId
}
