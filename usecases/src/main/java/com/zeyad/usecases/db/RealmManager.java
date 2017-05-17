package com.zeyad.usecases.db;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Completable;
import rx.Observable;

/**
 * {@link DataBaseManager} implementation.
 */
public class RealmManager implements DataBaseManager {

    private static final String REALM_OBJECT_INVALID = "RealmObject is invalid",
            JSON_INVALID = "JSONObject is invalid", NO_ID = "Could not find id!";
    private static Handler backgroundHandler;

    public RealmManager(Handler backgroundHandler) {
        RealmManager.backgroundHandler = backgroundHandler;
    }

    public RealmManager(Looper backgroundLooper) {
        if (backgroundHandler == null)
            backgroundHandler = new Handler(backgroundLooper);
    }

    RealmManager() {
        backgroundHandler = new Handler();
    }

    /**
     * Gets an {@link Observable} which will emit an Object.
     *
     * @param dataClass    Class type of the items to get.
     * @param idColumnName Name of the id field.
     * @param itemId       The user id to retrieve data.
     */
    @NonNull
    @Override
    public Observable<?> getById(@NonNull final String idColumnName, final int itemId, Class dataClass) {
        return Observable.defer(() -> {
            int finalItemId = itemId;
            if (finalItemId <= 0)
                finalItemId = Utils.getInstance().getMaxId(dataClass, idColumnName);
            Realm realm = Realm.getDefaultInstance();
            return realm.where(dataClass).equalTo(idColumnName, finalItemId).findAll().asObservable()
                    .filter(results -> ((RealmResults) results).isLoaded())
                    .map(o -> realm.copyFromRealm((RealmResults) o))
                    .doOnUnsubscribe(() -> closeRealm(realm));
        });
    }

    /**
     * Gets an {@link Observable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    @Override
    public Observable<List> getAll(Class clazz) {
        return Observable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            return realm.where(clazz).findAll().asObservable()
                    .filter(results -> ((RealmResults) results).isLoaded())
                    .map(o -> realm.copyFromRealm((RealmResults) o))
                    .doOnUnsubscribe(() -> closeRealm(realm));
        });
    }

    /**
     * Takes a query to be executed and return a list of containing the result.
     *
     * @param queryFactory The query used to look for inside the DB.
     * @param <T>          the return type from the query
     * @return {@link List<T>} a result list that matches the given query.
     */
    @NonNull
    @Override
    public <T extends RealmModel> Observable<List<T>> getQuery(RealmQueryProvider<T> queryFactory) {
        return Observable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            return queryFactory.create(realm).findAll().asObservable()
                    .filter(RealmResults::isLoaded)
                    .map(realm::copyFromRealm)
                    .doOnUnsubscribe(() -> closeRealm(realm));
        });
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public <M extends RealmModel> Completable put(@Nullable M realmModel, @NonNull Class dataClass) {
        if (realmModel != null) {
            return Completable.defer(() -> {
                Realm realm = Realm.getDefaultInstance();
                try {
                    RealmModel result = executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmModel));
                    if (RealmObject.isValid(result)) {
                        return Completable.complete();
                    } else
                        return Completable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                } finally {
                    closeRealm(realm);
                }
            });
        }
        return Completable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public Completable put(@Nullable JSONObject jsonObject, @Nullable String idColumnName, @NonNull Class dataClass) {
        if (jsonObject != null) {
            return Completable.defer(() -> {
                try {
                    updateJsonObjectWithIdValue(jsonObject, idColumnName, dataClass);
                } catch (@NonNull JSONException | IllegalArgumentException e) {
                    return Completable.error(e);
                }
                Realm realm = Realm.getDefaultInstance();
                try {
                    RealmModel result = executeWriteOperationInRealm(realm, () -> realm.createOrUpdateObjectFromJson(dataClass, jsonObject));
                    if (RealmObject.isValid(result)) {
                        return Completable.complete();
                    } else
                        return Completable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                } finally {
                    closeRealm(realm);
                }
            });
        } else
            return Completable.error(new IllegalArgumentException(JSON_INVALID));
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    @Override
    public Completable putAll(@NonNull JSONArray jsonArray, String idColumnName, @NonNull Class dataClass) {
        return Completable.defer(() -> {
            try {
                updateJsonArrayWithIdValue(jsonArray, idColumnName, dataClass);
            } catch (@NonNull JSONException | IllegalArgumentException e) {
                return Completable.error(e);
            }
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.createOrUpdateAllFromJson(dataClass, jsonArray));
                return Completable.complete();
            } finally {
                closeRealm(realm);
            }
        });
    }

    @Override
    public <T extends RealmModel> Completable putAll(List<T> realmObjects, Class dataClass) {
        return Completable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmObjects));
                return Completable.complete();
            } finally {
                closeRealm(realm);
            }
        });
    }

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Completable evictAll(@NonNull Class clazz) {
        return Completable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.delete(clazz));
                return Completable.complete();
            } finally {
                closeRealm(realm);
            }
        });
    }

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    @Override
    public boolean evictById(@NonNull Class clazz, @NonNull String idFieldName, final long idFieldValue) {
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmModel toDelete = realm.where(clazz).equalTo(idFieldName, idFieldValue).findFirst();
            if (toDelete != null) {
//                executeWriteOperationInRealm(realm, () -> RealmObject.deleteFromRealm(toDelete));
                executeWriteOperationInRealm(realm, new Executor() {
                    @Override
                    public void run() {
                        RealmObject.deleteFromRealm(toDelete);
                    }
                });
                return !RealmObject.isValid(toDelete);
            } else return false;
        } finally {
            closeRealm(realm);
        }
    }

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Completable evictCollection(@NonNull String idFieldName, @NonNull List<Long> list,
                                       @NonNull Class dataClass) {
        return Completable.defer(() -> {
            boolean isDeleted = true;
            for (int i = 0, size = list.size(); i < size; i++)
                isDeleted = isDeleted && evictById(dataClass, idFieldName, list.get(i));
            return Completable.complete();
        });
    }

    private void closeRealm(Realm realm) {
        backgroundHandler.post(() -> {
            if (!realm.isClosed()) {
                realm.close();
                Log.d(RealmManager.class.getSimpleName(), "realm instance closed!");
            }
        });
    }

    private void executeWriteOperationInRealm(@NonNull Realm realm, @NonNull Executor executor) {
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        executor.run();
        realm.commitTransaction();
    }

    private <T> T executeWriteOperationInRealm(@NonNull Realm realm, @NonNull ExecuteAndReturn<T> executor) {
        T toReturnValue;
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        toReturnValue = executor.run();
        realm.commitTransaction();
        return toReturnValue;
    }

    @NonNull
    private JSONArray updateJsonArrayWithIdValue(@NonNull JSONArray jsonArray, @Nullable String idColumnName,
                                                 Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty())
            throw new IllegalArgumentException(NO_ID);
        for (int i = 0, length = jsonArray.length(); i < length; i++)
            if (jsonArray.opt(i) instanceof JSONObject)
                updateJsonObjectWithIdValue(jsonArray.optJSONObject(i), idColumnName, dataClass);
        return jsonArray;
    }

    @NonNull
    private JSONObject updateJsonObjectWithIdValue(@NonNull JSONObject jsonObject, @Nullable String idColumnName,
                                                   Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty())
            throw new IllegalArgumentException(NO_ID);
        if (jsonObject.optInt(idColumnName) == 0)
            jsonObject.put(idColumnName, Utils.getInstance().getNextId(dataClass, idColumnName));
        return jsonObject;
    }

    private interface Executor {
        void run();
    }

    private interface ExecuteAndReturn<T> {
        @NonNull
        T run();
    }
}