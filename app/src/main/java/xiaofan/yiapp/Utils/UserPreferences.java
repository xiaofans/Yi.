package xiaofan.yiapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class UserPreferences {
    public static final String PREF_ASK_TO_SHARE_POST = "pref_ask_to_share_post";
    public static final String PREF_NEXT_POST_TIME = "pref_next_post_time";
    public static final String PREF_NOTIFICATION_SOUND_ENABLED = "pref_notification_sound_enabled";

    public int prefMode;
    public String prefName;
    private final SharedPreferences prefs;

    private UserPreferences(SharedPreferences sharedPreferences)
    {
        this.prefs = sharedPreferences;
    }

    public static UserPreferences get(Context context, long id)
    {
        String str = "" + id;
        UserPreferences userPreferences = new UserPreferences(context.getSharedPreferences(str, 0));
        userPreferences.prefMode = 0;
        userPreferences.prefName = str;
        return userPreferences;
    }

    public boolean isNotificationSoundEnabled(){
        return prefs.getBoolean(PREF_NOTIFICATION_SOUND_ENABLED,true);
    }

    public void setAskToSharePost(boolean askToSharePost){
        prefs.edit().putBoolean(PREF_ASK_TO_SHARE_POST,askToSharePost).commit();
    }

    public void setNotificationSoundEnabled(boolean notificationSoundEnabled){
        prefs.edit().putBoolean(PREF_NOTIFICATION_SOUND_ENABLED,notificationSoundEnabled).commit();
    }

    public boolean shouldAskToSharePost(){
        return prefs.getBoolean(PREF_NOTIFICATION_SOUND_ENABLED,true);
    }


}
