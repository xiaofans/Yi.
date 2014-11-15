package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.entity.UniversalBean;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.LoginCallback;
import xiaofan.yiapp.social.LoginError;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.social.SocialAuth;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/15.
 */
public class DeletePostService extends Service{

    public static Intent newIntent(Context context, Post post)
    {
        Intent intent = new Intent(context, DeletePostService.class);
        intent.putExtra("post", post);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return START_FLAG_REDELIVERY;
        }
        if(intent == null){
            stopSelf();
        }
        final Post post = intent.getParcelableExtra("post");
        SocialApi.getCurrent(this).getSocialAuth(this,new LoginCallback() {
            @Override
            public void failure(LoginError loginError) {
                EventBus.post(new FailureEvent());
                if (!loginError.networkError) {
                    EventBus.post(new LogoutEvent());
                }
                stopSelf();
            }

            @Override
            public void success(SocialAuth socialAuth) {
                UniversalBean universalBean = new UniversalBean();
                universalBean.postId = post.pid;
                ApiService.getInstance().deletePost(universalBean,new PostDeletedCallback(post));
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    class PostDeletedCallback
            implements Callback<Post>
    {

        private Post p;
        public PostDeletedCallback(Post post) {
            this.p = post;
        }

        @Override
        public void success(Post post, Response response) {
           p.delete();
           EventBus.post(new SuccessEvent());
           stopSelf();
        }

        @Override
        public void failure(RetrofitError error) {
            EventBus.post(new FailureEvent());
            stopSelf();
        }
    }
    public static class SuccessEvent {}
    public static class FailureEvent {}
}
