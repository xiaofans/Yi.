package xiaofan.yiapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class ApplicationPreferences {
    public static final String PREF_APP_VERSION = "pref_app_version";
    private static final String PREF_AUTHENTICATED_NETWORK = "pref_authenticated_network";
    private final SharedPreferences prefs;
    private ApplicationPreferences(SharedPreferences prefs){
        this.prefs = prefs;
    }

    public static ApplicationPreferences get(Context context){
        return new ApplicationPreferences(context.getSharedPreferences("yiapp",0));
    }

    public synchronized String getAuthenticatedNetwork(){
        return prefs.getString(PREF_AUTHENTICATED_NETWORK,null);
    }

    public synchronized  void setAuthenticatedNetwork(String authenticatedNetwork){
        prefs.edit().putString(PREF_AUTHENTICATED_NETWORK,authenticatedNetwork).commit();
    }

}
