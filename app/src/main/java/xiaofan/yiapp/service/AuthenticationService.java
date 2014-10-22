package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.LoginCallback;
import xiaofan.yiapp.social.LoginError;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.social.SocialAuth;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/10/22.
 */
public class AuthenticationService extends Service{

    public static Intent newIntent(Context context){
        return new Intent(context,AuthenticationService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("--onStartCommand--");
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            stopSelf();
            return START_FLAG_REDELIVERY;
        }
        final SocialApi socialApi = SocialApi.getCurrent(this);
        if(socialApi == null){
            EventBus.post(new FailureEvent());
            stopSelf();
            return START_FLAG_REDELIVERY;
        }
        socialApi.getSocialAuth(this,new LoginCallback() {
            @Override
            public void failure(LoginError loginError) {
                EventBus.post(new FailureEvent());
                if(!loginError.networkError){
                    EventBus.post(new LogoutEvent());
                }
                stopSelf();
            }

            @Override
            public void success(SocialAuth socialAuth) {
                ApiService.getInstance().login(socialAuth.network,socialAuth.token,socialAuth.id,new AuthenticationCallback());
            }
        });
         return START_FLAG_REDELIVERY;
    }

    public static class FailureEvent{}
    public static class SuccessEvent{}

    class AuthenticationCallback implements Callback<User>{

        @Override
        public void success(User user, Response response) {
             EventBus.post(new SuccessEvent());
            stopSelf();
        }

        @Override
        public void failure(RetrofitError error) {
                EventBus.post(new FailureEvent());
                if(error != null && error.getResponse().getStatus() == 401){
                    EventBus.post(new LogoutEvent());
                }
            stopSelf();
        }
    }

}
