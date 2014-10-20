package xiaofan.yiapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by zhaoyu on 2014/10/9.
 */
public class YiApp  extends MultiDexApplication{

    private static final String TAG = YiApp.class.getSimpleName();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }



    public static String getLogName(){
        return TAG;
    }
}
