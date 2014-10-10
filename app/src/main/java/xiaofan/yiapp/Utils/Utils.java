package xiaofan.yiapp.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by zhaoyu on 2014/10/10.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void addSystemUIPadding(Activity activity, View contentView)
    {
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        SystemBarTintManager.SystemBarConfig systemBarConfig = new SystemBarTintManager(activity).getConfig();
        int left = contentView.getPaddingLeft();
        int top = contentView.getPaddingTop() + systemBarConfig.getPixelInsetTop(false);
        int right = contentView.getPaddingRight() + systemBarConfig.getPixelInsetRight();
        int bottom = contentView.getPaddingBottom() + systemBarConfig.getPixelInsetBottom();
        Log.w(TAG,"padding > left:" + left +" top:" + top +" right:" + right +" bottom:" + bottom);
        contentView.setPadding(left,top,right,bottom);
    }

}
