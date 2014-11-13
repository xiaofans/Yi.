package xiaofan.yiapp.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.entity.HeartToggle;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.LoginCallback;
import xiaofan.yiapp.social.LoginError;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.social.SocialAuth;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/13.
 */
public class HeartToggleService extends Service{

    public static Intent newIntent(Context context, Post post,HeartToggle heartToggle)
    {
        Intent intent = new Intent(context, HeartToggleService.class);
        intent.putExtra("post", post);
        intent.putExtra("heartToggle",heartToggle);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return START_FLAG_REDELIVERY;
        }

        final HeartToggle heartToggle =  intent.getExtras().getParcelable("heartToggle");
        final Post post = intent.getExtras().getParcelable("post");
        SocialApi.getCurrent(this).getSocialAuth(this,new LoginCallback() {
            @Override
            public void failure(LoginError loginError) {
                EventBus.post(new FailureEvent());
            }

            @Override
            public void success(SocialAuth socialAuth) {
                ApiService.getInstance().heartToggle(heartToggle,new HeartToggleCallback(post));
            }
        });


        return START_FLAG_REDELIVERY;

    }

    class HeartToggleCallback implements Callback<HeartToggle>{
        private Post post;
        public HeartToggleCallback(Post post) {
            this.post = post;
        }
        @Override
        public void success(HeartToggle heartToggle, Response response) {
                post.hasHearted = heartToggle.hasHearted;
                if(heartToggle.hasHearted){
                    post.heartCount += 1;
                }else{
                    if(post.heartCount > 0){
                        post.heartCount -= 1;
                    }
                }
                post.save();
                EventBus.post(new SuccessEvent());
        }
        @Override
        public void failure(RetrofitError error) {
            EventBus.post(new FailureEvent());
            if(error.getResponse() != null && error.getResponse().getStatus() == 401){
                EventBus.post(new LogoutEvent());
            }
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class SuccessEvent {}
    public static class FailureEvent {}
}
