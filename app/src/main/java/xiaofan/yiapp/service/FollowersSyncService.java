package xiaofan.yiapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit.RetrofitError;
import se.emilsjolander.sprinkles.Transaction;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.ParseBase;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/2.
 */
public class FollowersSyncService extends IntentService{

    private final static String TAG = "FollowersSyncService";

    public static Intent newIntent(Context context, User user)
    {
        Intent localIntent = new Intent(context, FollowersSyncService.class);
        localIntent.putExtra("user", user);
        return localIntent;
    }

    public FollowersSyncService() {
        super("FollowerSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return;
        }
        try {
            User user = intent.getParcelableExtra("user");
           ParseBase<ArrayList<User>> result = ApiService.getInstance().getFollowers(user);
           ArrayList<User> users = result.result == null ? new ArrayList<User>() : result.result;
           Transaction transaction = new Transaction();
            Iterator<Connection> iterator = QueryBuilder.connections(null,user).get().iterator();
             while (iterator.hasNext()){
                iterator.next().delete(transaction);
            }
            Iterator<User> iterator2 = users.iterator();
            while (iterator2.hasNext()){
                User user2 = iterator2.next();
                if(user2.save()){
                    new Connection(user2.id,user.id).save(transaction);
                }
            }
            transaction.setSuccessful(true);
            transaction.finish();
            EventBus.post(new SuccessEvent());
        }catch (RetrofitError retrofitError){
            Log.w(TAG,retrofitError.toString());
            EventBus.post(new FailureEvent());
        }

    }


    public static class FailureEvent {}

    public static class SuccessEvent {}
}
