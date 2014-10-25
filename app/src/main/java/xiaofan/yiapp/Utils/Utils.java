package xiaofan.yiapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.ref.WeakReference;

import xiaofan.yiapp.R;

/**
 * Created by zhaoyu on 2014/10/10.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private static WeakReference<AlertDialog> errorDialog;

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
        InputMethodManager localInputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View localView = activity.getCurrentFocus();
        if (localView != null) {
            localInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }
    public static boolean isNetworkAvailable(Context context)
    {
        NetworkInfo localNetworkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
    }

    public static String colorToHex(int color)
    {
        Object[] objectArray = new Object[1];
        objectArray[0] = Integer.valueOf(0xFFFFFF & color);
        return String.format("#%06X", objectArray);
    }

    public static int getAppVersion(Context context)
    {
        try
        {
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            return i;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
            throw new RuntimeException("Could not get package name: " + localNameNotFoundException);
        }
    }

    public static int randomHipsterColor(Context context)
    {
        int[] colors = {R.color.hipster_blue, R.color.hipster_green, R.color.hipster_red, R.color.hipster_orange, R.color.hipster_denim, R.color.hipster_purple };
        return context.getResources().getColor(colors[new java.util.Random().nextInt(colors.length)]);
    }

    public static void showErrorDialog(Context context,int sourceId){
        showErrorDialog(context,context.getResources().getString(sourceId));
    }

    public static void showErrorDialog(Context context,String message){
        if(errorDialog != null && errorDialog.get() != null && errorDialog.get().isShowing()){
            errorDialog.get().dismiss();
            errorDialog.clear();
        }
        errorDialog = new WeakReference<AlertDialog>(new AlertDialog.Builder(context).setTitle(R.string.sorry).setMessage(message).setPositiveButton(R.string.ok,null).create());
        errorDialog.get().show();
    }

}
