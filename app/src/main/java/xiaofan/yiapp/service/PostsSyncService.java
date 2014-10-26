package xiaofan.yiapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import xiaofan.yiapp.api.User;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/10/26.
 */
public class PostsSyncService extends IntentService{

    public static Intent newIntent(Context context,User user){
        Intent intent = new Intent(context,PostsSyncService.class);
        intent.putExtra("user",user);
        return intent;
    }

    public PostsSyncService() {
        super("PostsSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return;
        }
        User me = QueryBuilder.me().get();
        




    }


    public static class SuccessEvent{}
    public static class FailureEvent{}


}
