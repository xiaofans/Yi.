package xiaofan.yiapp.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedHashMap;
import java.util.Map;

import xiaofan.yiapp.utils.ApplicationPreferences;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public abstract class SocialApi {

    public static final Map<String,SocialApi> ALL = new LinkedHashMap<String, SocialApi>();
    private static SocialApi currentAuth;
    static {
        ALL.put(WeiboApi.TAG,new WeiboApi());
        ALL.put(QQApi.TAG,new QQApi());
    }
    public synchronized static SocialApi getCurrent(Context context){
        currentAuth = ALL.get(ApplicationPreferences.get(context).getAuthenticatedNetwork());
        return currentAuth;
    }

    public static void setCurrent(Context context,String authenticatedNetwork){
      //  if(authenticatedNetwork == null) return;
        ApplicationPreferences.get(context).setAuthenticatedNetwork(authenticatedNetwork);
    }

    public abstract void getSocialAuth(Context context,LoginCallback loginCallback);

    public abstract View getSocialButton(Context context,ViewGroup viewGroup);

    public abstract void login(Activity activity,LoginCallback loginCallback);

    public abstract void logout(Context context);

    public abstract void onActivityResult(Activity activity,int requestCode,int resultCode,Intent intent);

    public abstract void onNewIntent(Activity activity,Intent intent);

}
