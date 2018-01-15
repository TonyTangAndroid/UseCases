package com.zeyad.usecases.app.screens;

import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.usecases.app.GenericApplication;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;

/**
 * @author by ZIaDo on 7/21/17.
 */

public abstract class BaseFragment<S extends Parcelable, VM extends BaseViewModel<S>>
        extends com.zeyad.rxredux.core.redux.prelollipop.BaseFragment<S, VM> {

    public void showToastMessage(String message) {
        showToastMessage(message, Toast.LENGTH_LONG);
    }

    public void showToastMessage(String message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} message.
     *
     * @param message An string representing a message to be shown.
     */
    public void showSnackBarMessage(View view, String message, int duration) {
        if (view != null) {
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show();
        } else {
            throw new IllegalArgumentException("View is null");
        }
    }

    public void showSnackBarWithAction(String typeSnackBar, View view,
            String message, String actionText, View.OnClickListener onClickListener) {
        if (view != null) {
            SnackBarFactory.getSnackBarWithAction(
                    typeSnackBar, view, message, actionText, onClickListener)
                           .show();
        } else {
            throw new IllegalArgumentException("View is null");
        }
    }

    public void showSnackBarWithAction(String typeSnackBar, View view,
            String message, int actionText, View.OnClickListener onClickListener) {
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener);
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} errorResult message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    public void showErrorSnackBar(String message, View view, int duration) {
        if (view != null) {
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration).show();
        } else {
            throw new IllegalArgumentException("View is null");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((GenericApplication) getContext().getApplicationContext()).getRefwatcher().watch(this);
    }
}
