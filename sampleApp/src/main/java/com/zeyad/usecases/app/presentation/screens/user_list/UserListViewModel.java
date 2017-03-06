package com.zeyad.usecases.app.presentation.screens.user_list;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListViewModel {

    Observable<UserListState> getState();

    void incrementPage();

    void getUsers();

    Observable search(String query);

    Observable deleteCollection(List<Long> selectedItemsIds);
}
