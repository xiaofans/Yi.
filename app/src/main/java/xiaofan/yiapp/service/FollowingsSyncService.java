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
public class FollowingsSyncService extends IntentService{

    private static final String TAG = "FollowingsSyncService";
    public FollowingsSyncService() {
        super("FollowingsSyncService");
    }

    public static Intent newIntent(Context context, User user)
    {
        Intent localIntent = new Intent(context, FollowingsSyncService.class);
        localIntent.putExtra("user", user);
        return localIntent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return;
        }
        boolean isSyncSuccess = true;
        User me = intent.getParcelableExtra("user");
        try {
            ParseBase<ArrayList<User>> result = ApiService.getInstance().getFollowings(me);
            ArrayList<User> followings = result.result == null ? new ArrayList<User>() : result.result;
            Transaction transaction = new Transaction();
            Iterator<Connection> iterator = QueryBuilder.connections(me,null).get().iterator();
            while (iterator.hasNext()){
                iterator.next().delete(transaction);
            }

            Iterator<User> iterator2 = followings.iterator();
            while (iterator2.hasNext()){
                User user = iterator2.next();
                user.id = user.uid;
                if(user.save(transaction)){
                    if(new Connection(me.id,user.id).save(transaction)){
                        isSyncSuccess = true;
                     }else{
                        isSyncSuccess = false;
                    }
                }else{
                    isSyncSuccess = false;
                }
            }
            transaction.setSuccessful(isSyncSuccess);
            transaction.finish();
            if(isSyncSuccess){
                EventBus.post(new SuccessEvent());
                startService(TimelineSyncService.newIntent(this));
            }else{
                EventBus.post(new FailureEvent());
            }
        }catch (RetrofitError retrofitError){
            Log.w(TAG, retrofitError.toString());
            EventBus.post(new FailureEvent());
        }
    }

    public static class FailureEvent {}

    public static class SuccessEvent {}
}
