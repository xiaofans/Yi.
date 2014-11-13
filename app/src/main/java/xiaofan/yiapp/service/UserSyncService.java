package xiaofan.yiapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import retrofit.RetrofitError;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.ParseBase;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

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
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return;
        }
        try {
            User me = QueryBuilder.me().get();
            User user = intent.getParcelableExtra("user");
            ParseBase<User> result = ApiService.getInstance().getUser(user.uid);
            User newUser = result.result;
            newUser.id = newUser.uid;
            if(newUser.id == me.id){
                newUser.me = true;
            }
            if(newUser.save()){
                EventBus.post(new SuccessEvent());
            }else{
                EventBus.post(new FailureEvent());
            }
        }catch (RetrofitError retrofitError){
            EventBus.post(new FailureEvent());
        }
    }


    public static class FailureEvent {}
    public static class SuccessEvent {}

}
