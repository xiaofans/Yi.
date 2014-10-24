package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.ParseBase;
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

    private static final String TAG = AuthenticationService.class.getSimpleName();

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
                Toast.makeText(getApplicationContext(),"auth failure!" + loginError.error,Toast.LENGTH_LONG).show();
                EventBus.post(new FailureEvent());
                if(!loginError.networkError){
                    EventBus.post(new LogoutEvent());
                }
                stopSelf();
            }

            @Override
            public void success(SocialAuth socialAuth) {
                Toast.makeText(getApplicationContext(),"auth success!",Toast.LENGTH_LONG).show();
                Gson gson = new Gson();
                try {
                    ApiService.getInstance().login(new JSONObject(gson.toJson(socialAuth)),new AuthenticationCallback());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
         return START_FLAG_REDELIVERY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG,"destory..");
    }


    public static class FailureEvent{}
    public static class SuccessEvent{}

    class AuthenticationCallback implements Callback<ParseBase<User>>{
        @Override
        public void success(ParseBase<User> userParseBase, Response response) {
            Log.w("AuthenticationCallback","user is:" + userParseBase.result.name +" response is:" + response.toString());
            EventBus.post(new SuccessEvent());
            stopSelf();
        }

        @Override
        public void failure(RetrofitError error) {
            Log.w("AuthenticationCallback","error si:" + error.getMessage());
            EventBus.post(new FailureEvent());
            if(error != null && error.getResponse().getStatus() == 401){
                EventBus.post(new LogoutEvent());
            }
            stopSelf();
        }
    }

}
