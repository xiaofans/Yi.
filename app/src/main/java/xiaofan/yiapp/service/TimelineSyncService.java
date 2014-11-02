package xiaofan.yiapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit.RetrofitError;
import se.emilsjolander.sprinkles.Transaction;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/2.
 */
public class TimelineSyncService extends IntentService{

    public static Intent newIntent(Context context)
    {
        return new Intent(context, TimelineSyncService.class);
    }

    public TimelineSyncService() {
        super("TimelineSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return;
        }
        try {
            User me = QueryBuilder.me().get();
            Transaction transaction =  new Transaction();
            ArrayList<Post> posts = ApiService.getInstance().getTimeline(me.id, me.id);
            Iterator<Post> iterator = posts.iterator();
            if(!iterator.hasNext()) return;
            while (iterator.hasNext()){
                Post post = iterator.next();
                if(post.deleted){
                    post.delete(transaction);
                }else{
                    post.save(transaction);
                }
            }
            transaction.setSuccessful(true);
            transaction.finish();
            EventBus.post(new SuccessEvent());
        }catch (RetrofitError retrofitError){
            EventBus.post(new FailureEvent());
        }

    }



    public static class FailureEvent {}

    public static class SuccessEvent {}
}
