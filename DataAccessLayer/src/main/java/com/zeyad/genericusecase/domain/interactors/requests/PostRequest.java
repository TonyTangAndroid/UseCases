package com.zeyad.genericusecase.domain.interactors.requests;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class PostRequest {
    public static final String POST = "post", DELETE = "delete", PUT = "put";
    private String mUrl, mIdColumnName, mMethod;
    private Subscriber mSubscriber;
    private Class mDataClass, mPresentationClass;
    private boolean mPersist;
    private JSONObject mJsonObject;
    private JSONArray mJsonArray;
    private HashMap<String, Object> mKeyValuePairs;
    private ContentValues mContentValue;
    private ContentValues[] mContentValues;

    public PostRequest(@NonNull PostRequestBuilder postRequestBuilder) {
        mUrl = postRequestBuilder.mUrl;
        mDataClass = postRequestBuilder.mDataClass;
        mPresentationClass = postRequestBuilder.mPresentationClass;
        mPersist = postRequestBuilder.mPersist;
        mSubscriber = postRequestBuilder.mSubscriber;
        mKeyValuePairs = postRequestBuilder.mKeyValuePairs;
        mJsonObject = postRequestBuilder.mJsonObject;
        mJsonArray = postRequestBuilder.mJsonArray;
        mIdColumnName = postRequestBuilder.mIdColumnName;
        mMethod = postRequestBuilder.mMethod;
        mContentValue = postRequestBuilder.mContentValue;
        mContentValues = postRequestBuilder.mContentValues;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, ContentValues[] contentValues,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mContentValues = contentValues;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, ContentValues contentValue,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mContentValue = contentValue;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, JSONObject keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mJsonObject = keyValuePairs;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, JSONArray keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mJsonArray = keyValuePairs;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, HashMap<String, Object> keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mKeyValuePairs = keyValuePairs;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    @Nullable
    public JSONObject getObjectBundle() {
        if (mJsonObject != null)
            return mJsonObject;
        else if (mKeyValuePairs != null)
            return new JSONObject(mKeyValuePairs);
        else return null;
    }

    public String getUrl() {
        return mUrl;
    }

    public Subscriber getSubscriber() {
        return mSubscriber;
    }

    public Class getDataClass() {
        return mDataClass;
    }

    public Class getPresentationClass() {
        return mPresentationClass;
    }

    public boolean isPersist() {
        return mPersist;
    }

    public JSONArray getJsonArray() {
        return mJsonArray;
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    public HashMap<String, Object> getKeyValuePairs() {
        return mKeyValuePairs;
    }

    public String getIdColumnName() {
        return mIdColumnName;
    }

    public String getMethod() {
        return mMethod;
    }

    public ContentValues[] getContentValues() {
        return mContentValues;
    }

    public ContentValues getContentValue() {
        return mContentValue;
    }

    public static class PostRequestBuilder {

        ContentValues mContentValue;
        ContentValues[] mContentValues;
        JSONArray mJsonArray;
        JSONObject mJsonObject;
        HashMap<String, Object> mKeyValuePairs;
        String mUrl, mIdColumnName, mMethod;
        Subscriber mSubscriber;
        Class mDataClass, mPresentationClass;
        boolean mPersist;

        public PostRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public PostRequestBuilder contentValue(ContentValues contentValues) {
            mContentValue = contentValues;
            return this;
        }

        @NonNull
        public PostRequestBuilder contentValues(ContentValues[] contentValues) {
            mContentValues = contentValues;
            return this;
        }

        @NonNull
        public PostRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public PostRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public PostRequestBuilder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
        }

        @NonNull
        public PostRequestBuilder idColumnName(String idColumnName) {
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public PostRequestBuilder jsonObject(JSONObject jsonObject) {
            mJsonObject = jsonObject;
            return this;
        }

        @NonNull
        public PostRequestBuilder method(String method) {
            mMethod = method;
            return this;
        }

        @NonNull
        public PostRequestBuilder jsonArray(JSONArray jsonArray) {
            mJsonArray = jsonArray;
            return this;
        }

        @NonNull
        public PostRequestBuilder hashMap(HashMap<String, Object> bundle) {
            mKeyValuePairs = bundle;
            return this;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }
    }
}
