package xiaofan.yiapp.base;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.ui.LoginActivity;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public  abstract  class AuthenticatedActivity extends BaseActivity {

    private Object logOutListener = new Object(){
        @Subscribe
        public  void loggedOut(LogoutEvent logoutEvent){
            finish();
            startActivity(LoginActivity.newIntent(AuthenticatedActivity.this));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.register(logOutListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.unregister(logOutListener);
    }
}



