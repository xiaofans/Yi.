package xiaofan.yiapp;

import android.app.Application;

/**
 * Created by zhaoyu on 2014/10/9.
 */
public class YiApp  extends Application{

    private static final String TAG = YiApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }



    public static String getLogName(){
        return TAG;
    }
}
