package com.zeyad.usecases.app.screens.user.list.events;

import com.zeyad.rxredux.core.redux.BaseEvent;

/**
 * @author by ZIaDo on 4/20/17.
 */
public class SearchUsersEvent implements BaseEvent<String> {

    private final String query;

    public SearchUsersEvent(String s) {
        query = s;
    }

    @Override
    public String getPayLoad() {
        return query;
    }
}
