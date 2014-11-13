package xiaofan.yiapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import xiaofan.yiapp.api.User;

/**
 * Created by zhaoyu on 2014/11/13.
 */
public class UserSyncService extends IntentService{

    public static Intent newIntent(Context context, User user)
    {
        Intent localIntent = new Intent(context, UserSyncService.class);
        localIntent.putExtra("user", user);
        return localIntent;
    }
    public UserSyncService() {
        super("UserSyncService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {


    }

}
