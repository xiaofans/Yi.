package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/3.
 */
public class FollowToggleService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static Intent newIntent(Context context, User user, boolean checked) {
        Intent intent = new Intent(context,FollowToggleService.class);
        intent.putExtra("user",user);
        intent.putExtra("follow",checked);
        return intent;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return START_FLAG_REDELIVERY;
        }

        User me = QueryBuilder.me().get();
        User user = intent.getParcelableExtra("user");
        boolean follow = intent.getBooleanExtra("follow", false);
        Connection connection = QueryBuilder.connection(me,user).get();
        if(connection == null && follow){
            new  Connection(me.id,user.id).save();
            user.followingsCount += 1;
            user.save();
        }else if(connection != null && !follow){
            connection.delete();
            user.followingsCount -= 1;
            user.save();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public static class FailureEvent {}
    public static class SuccessEvent {}
}
