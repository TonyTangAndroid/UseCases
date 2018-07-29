package com.zeyad.usecases.app.components.snackbar;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;

/**
 * @author by ZIaDo on 7/21/17.
 */
public final class SnackBarFactory {

    public static final String TYPE_INFO = "typeInfo";
    public static final String TYPE_ERROR = "typeError";

    private SnackBarFactory() {
    }

    public static Snackbar getSnackBar(String snackBarType, @NonNull View view,
            @StringRes int stringId, int duration) {
        return getSnackBar(view, stringId, duration, getColorId(snackBarType));
    }

    public static Snackbar getSnackBar(String snackBarType, @NonNull View view,
            @NonNull CharSequence text, int duration) {
        return getSnackBar(view, text, duration, getColorId(snackBarType));
    }

    public static Snackbar getSnackBar(@NonNull View view, @StringRes int stringId, int duration,
            @ColorInt int colorId) {
        return createSnackBar(Snackbar.make(view, stringId, duration), colorId);
    }

    public static Snackbar getSnackBar(@NonNull View view, @NonNull CharSequence text, int duration,
            @ColorInt int colorId) {
        return createSnackBar(Snackbar.make(view, text, duration), colorId);
    }

    public static Snackbar getSnackBarWithAction(String snackBarType, @NonNull View view,
            @NonNull CharSequence text, String actionText,
            View.OnClickListener onClickListener) {
        return createSnackBar(Snackbar.make(view, text, LENGTH_INDEFINITE)
                                      .setAction(actionText != null && !actionText.isEmpty() ? actionText : "RETRY", onClickListener)
                                      .setActionTextColor(Color.BLACK), getColorId(snackBarType));
    }

    private static Snackbar createSnackBar(Snackbar snackbar, @ColorInt int colorId) {
        return ColoredSnackBar.info(snackbar, colorId);
    }

    private static int getColorId(String snackBarType) {
        return snackBarType.equals(TYPE_INFO) ? Color.parseColor("#45d482") : Color.parseColor("#e15D50");
    }
}
