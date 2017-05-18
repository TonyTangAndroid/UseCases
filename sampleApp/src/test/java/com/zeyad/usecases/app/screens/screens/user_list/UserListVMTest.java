package com.zeyad.usecases.app.screens.screens.user_list;

import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.screens.user_list.User;
import com.zeyad.usecases.app.screens.user_list.UserListVM;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/7/17.
 */
public class UserListVMTest {

    private IDataService mockDataUseCase;
    private List<User> userList;
    private UserListVM userListVM;

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataService.class);
        userListVM = new UserListVM(mockDataUseCase, mock(SuccessStateAccumulator.class));
    }

    // TODO: 3/31/17 Add value assertions!

    @Test
    public void returnUserListStateObservableWhenGetUserIsCalled() {
        User user = new User();
        user.setLogin("testUser");
        user.setId(1);
        userList = new ArrayList<>();
        userList.add(user);
        Observable<List<User>> observableUserRealm = Observable.just(userList);

        when(mockDataUseCase.<User>getListOffLineFirst(any()))
                .thenReturn(observableUserRealm);

        userListVM.getUsers(0);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).getListOffLineFirst(any(GetRequest.class));
    }

    @Test
    public void deleteCollection() throws Exception {
        Observable observableUserRealm = Observable.just(true);

        when(mockDataUseCase.deleteCollectionByIds(any(PostRequest.class)))
                .thenReturn(observableUserRealm);

        userListVM.deleteCollection(new ArrayList<>());

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).deleteCollectionByIds(any(PostRequest.class));
    }

    @Test
    public void search() throws Exception {
        User user = new User();
        user.setLogin("testUser");
        user.setId(1);
        userList = new ArrayList<>();
        userList.add(user);
        Observable<List<User>> listObservable = Observable.just(userList);
        Observable<User> userObservable = Observable.just(user);

        when(mockDataUseCase.<User>getObject(any(GetRequest.class))).thenReturn(userObservable);
        when(mockDataUseCase.<User>queryDisk(any(RealmQueryProvider.class)))
                .thenReturn(listObservable);

        userListVM.search("");

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(RealmQueryProvider.class));
    }
}
