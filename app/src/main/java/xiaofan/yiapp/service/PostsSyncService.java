package xiaofan.yiapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.RetrofitError;
import se.emilsjolander.sprinkles.Transaction;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.Posts;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.ParseBase;
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
        try {
            User me = QueryBuilder.me().get();
            User user = intent.getParcelableExtra("user");
            ParseBase<List<Post>> result = ApiService.getInstance().getPosts(user.id);
            Transaction transaction  = new Transaction();
            Iterator<Post> iterator = result.results.iterator();
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
    public static class SuccessEvent{}
    public static class FailureEvent{}


}
