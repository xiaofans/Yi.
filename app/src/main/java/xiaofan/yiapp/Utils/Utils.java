package xiaofan.yiapp.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by zhaoyu on 2014/10/10.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void addSystemUIPadding(Activity activity, View contentView)
    {
        SystemBarTintManager.SystemBarConfig systemBarConfig = new SystemBarTintManager(activity).getConfig();
        int left = contentView.getPaddingLeft();
        int top = contentView.getPaddingTop() + systemBarConfig.getPixelInsetTop(false);
        int right = contentView.getPaddingRight() + systemBarConfig.getPixelInsetRight();
        int bottom = contentView.getPaddingBottom() + systemBarConfig.getPixelInsetBottom();
        contentView.setPadding(left,top,right,bottom);
    }

    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager localInputMethodManager = (InputMethodManager)activity.getSystemService("input_method");
        View localView = activity.getCurrentFocus();
        if (localView != null) {
            localInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

}
