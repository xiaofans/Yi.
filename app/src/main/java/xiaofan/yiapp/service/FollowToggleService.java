package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import xiaofan.yiapp.api.User;

/**
 * Created by zhaoyu on 2014/11/3.
 */
public class FollowToggleService extends Service{


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static Intent newIntent(Context baseContext, User user, boolean checked) {
        return null;
    }
}
