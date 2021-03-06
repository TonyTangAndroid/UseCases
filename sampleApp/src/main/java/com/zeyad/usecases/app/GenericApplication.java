package com.zeyad.usecases.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.rollbar.android.Rollbar;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.usecases.api.DataServiceConfig;
import com.zeyad.usecases.api.DataServiceFactory;
import com.zeyad.usecases.network.ProgressInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.zeyad.usecases.app.utils.Constants.URLS.API_BASE_URL;

/**
 * @author by ZIaDo on 9/24/16.
 */
public class GenericApplication extends Application {
    private static final int TIME_OUT = 15;
    private Disposable disposable;
    private RefWatcher refwatcher;

    @TargetApi(value = 24)
    private static boolean checkAppSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                byte[] signatureBytes = signature.toByteArray();
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signatureBytes);
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("REMOVE_ME", "Include this string as a value for SIGNATURE:" + currentSignature);
                //compare signatures
                if (java.security.CryptoPrimitive.SIGNATURE.toString().equals(currentSignature)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("GenericApplication", "checkAppSignature", e);
            //assumes an issue in checking signature., but we let the caller decide on what to do.
        }
        return false;
    }

    private static boolean verifyInstaller(Context context) {
        final String installer =
                context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && installer.startsWith("com.android.vending");
    }

    private static boolean checkEmulator() {
        try {
            boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
            boolean emu = getSystemProperty("ro.kernel.qemu").length() > 0;
            boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
            if (emu || goldfish || sdk) {
                return true;
            }
        } catch (Exception ignored) {
            Log.e("GenericApplication", "checkEmulator", ignored);
        }
        return false;
    }

    private static String getSystemProperty(String name)
            throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException,
            IllegalAccessException {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class})
                .invoke(systemPropertyClazz, name);
    }

    private static boolean checkDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        initializeStrictMode();
        refwatcher = LeakCanary.install(this);
        disposable = Completable.fromAction(() -> {
            if (!checkAppTampering(this)) {
                throw new IllegalAccessException("App might be tampered with!");
            }
            //            initializeFlowUp();
            Rollbar.init(this, "c8c8b4cb1d4f4650a77ae1558865ca87", BuildConfig.DEBUG ? "debug" : "production");
        }).subscribeOn(Schedulers.io())
                .subscribe(() -> {
                }, Throwable::printStackTrace);
        initializeRealm();
        DataServiceFactory.init(new DataServiceConfig.Builder(this)
                .baseUrl(getApiBaseUrl())
                .okHttpBuilder(getOkHttpBuilder())
                .withCache(3, TimeUnit.MINUTES)
                .withRealm()
                .build());
    }

    @NonNull
    OkHttpClient.Builder getOkHttpBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new ProgressInterceptor((bytesRead, contentLength, done)
                        -> RxEventBusFactory.getInstance().send(null)) {
                    @Override
                    public boolean isFileIO(Response originalResponse) {
                        return false;
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                        .setLevel(BuildConfig.DEBUG ?
                                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .certificatePinner(new CertificatePinner.Builder()
                        .add(API_BASE_URL,
                                "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw")
                        .add(API_BASE_URL,
                                "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
                        .add(API_BASE_URL,
                                "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
                        .build())
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS));
        if (getSSlSocketFactory() != null && getX509TrustManager() != null) {
            builder.sslSocketFactory(getSSlSocketFactory(), getX509TrustManager());
        }
        return builder;
    }

    @NonNull
    String getApiBaseUrl() {
        return API_BASE_URL;
    }

    @Override
    public void onTerminate() {
        disposable.dispose();
        super.onTerminate();
    }

    private void initializeRealm() {
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name("app.realm")
                .modules(Realm.getDefaultModule(), new LibraryModule())
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build());
    }

    private boolean checkAppTampering(Context context) {
        return true;
        //        return checkAppSignature(context)
        //                && verifyInstaller(context)
        //                && checkEmulator()
        //                && checkDebuggable(context);
    }

    X509TrustManager getX509TrustManager() {
        return null;
    }

    private void initializeStrictMode() {
        if (BuildConfig.DEBUG
                || "true".equals(Settings.System.getString(getContentResolver(), "firebase.test.lab"))) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().penaltyLog().build());
            StrictMode
                    .setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
        }
    }

    SSLSocketFactory getSSlSocketFactory() {
        return null;
    }

    public RefWatcher getRefwatcher() {
        return refwatcher;
    }
}
