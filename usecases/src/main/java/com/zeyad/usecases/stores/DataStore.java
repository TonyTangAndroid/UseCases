package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.RealmQueryProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    Gson gson = Config.getGson();

    @NonNull
    <M> Flowable<List<M>> dynamicGetList(String url, String idColumnName, Class requestType,
                                         boolean persist, boolean shouldCache);

    /**
     * Get an {@link Flowable} which will emit a Object by its id.
     */
    @NonNull
    <M> Flowable<M> dynamicGetObject(String url, String idColumnName, Object itemId, Class itemIdType,
                                     Class requestType, boolean persist, boolean shouldCache);

    /**
     * Search disk with a RealmQuery which returns an {@link Flowable} that will emit a list of
     * Object.
     */
    @NonNull
    <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory);

    /**
     * Patch a JSONObject which returns an {@link Flowable} that will emit a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPatchObject(String url, String idColumnName, Class itemIdType,
                                       @NonNull JSONObject jsonObject, Class requestType,
                                       Class responseType, boolean persist, boolean cache);

    /**
     * Post a JSONObject which returns an {@link Flowable} that will emit a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPostObject(String url, String idColumnName, Class itemIdType,
                                      JSONObject keyValuePairs, Class requestType, Class responseType,
                                      boolean persist, boolean cache);

    /**
     * Post a HashMap<String, Object> which returns an {@link Flowable} that will emit a list of
     * Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPostList(String url, String idColumnName, Class itemIdType,
                                    JSONArray jsonArray, Class requestType, Class responseType,
                                    boolean persist, boolean cache);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Flowable} that
     * will emit a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPutObject(String url, String idColumnName, Class itemIdType,
                                     JSONObject keyValuePairs, Class requestType, Class responseType,
                                     boolean persist, boolean cache);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Flowable} that
     * will emit a list of Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPutList(String url, String idColumnName, Class itemIdType,
                                   JSONArray jsonArray, Class requestType, Class responseType,
                                   boolean persist, boolean cache);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link Flowable} that will emit
     * a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicDeleteCollection(String url, String idColumnName, Class itemIdType,
                                            JSONArray jsonArray, Class requestType, Class responseType,
                                            boolean persist, boolean cache);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link Completable}
     * that will emit a list of Object.
     */
    @NonNull
    Single<Boolean> dynamicDeleteAll(Class requestType);

    @NonNull
    Flowable<File> dynamicDownloadFile(String url, File file);

    @NonNull
    <M> Flowable<M> dynamicUploadFile(String url, @NonNull HashMap<String, File> keyFileMap,
                                      @Nullable HashMap<String, Object> parameters, @NonNull Class responseType);
}
