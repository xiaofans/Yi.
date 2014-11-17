package xiaofan.yiapp.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import xiaofan.yiapp.R;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class WeiboApi extends SocialApi{

    public static final String TAG = "weibo";
    public static final String KEY = "2718899853";
    public static final String SCRECT = "3cd56f88bcdbc7d7706675d2f0abbb81";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    private WeiboAuth mWeiboAuth;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private LoginCallback loginCallback;
    private Activity activity;
    private static final String PREF_OAUTH_TOKEN = "weibo_oauth_token";
    private static final String PREF_OAUTH_NETWORK = "weibo_oauth_network";
    private static final String PREF_OAUTH_ID = "weibo_oauth_id";


    @Override
    public void getSocialAuth(Context context, LoginCallback loginCallback) {
        SocialAuth socialAuth = getSocialAuth(context);
        if(socialAuth != null){
            loginCallback.success(socialAuth);
        }else {
            loginCallback.failure(new LoginError("NULL",false));
        }
    }

    @Override
    public View getSocialButton(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.view_social_button_weibo,viewGroup,false);
    }

    @Override
    public void login(Activity activity, LoginCallback loginCallback) {
        this.activity = activity;
        this.loginCallback = loginCallback;
        // 创建微博实例
        mWeiboAuth = new WeiboAuth(activity, KEY, "https://api.weibo.com/oauth2/default.html", SCOPE);
        mSsoHandler = new SsoHandler(activity, mWeiboAuth);
        mSsoHandler.authorize(new AuthListener(loginCallback));

    }

    @Override
    public void logout(Context context) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        Toast.makeText(activity,"-- onNewIntent --",Toast.LENGTH_LONG).show();
    }


    class AuthListener implements WeiboAuthListener {

        private LoginCallback loginCallback;

        AuthListener(LoginCallback loginCallback) {
            this.loginCallback = loginCallback;
        }


        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                SocialAuth socialAuth = new SocialAuth();
                socialAuth.id = mAccessToken.getUid();
                socialAuth.token = mAccessToken.getToken();
                socialAuth.network = TAG;
                saveWeiBoToken(socialAuth);
                if(loginCallback != null){
                    loginCallback.success(socialAuth);
                }
            } else {
                String code = values.getString("code");
                WeiboException weiboException = new WeiboException(code);
                if(loginCallback != null){
                    loginCallback.failure(new LoginError(code,false));
                }
            }

        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
         if(loginCallback != null){
             loginCallback.failure(new LoginError(e.toString(),true));
         }
        }
    }

    private void saveWeiBoToken(SocialAuth socialAuth) {
        if(socialAuth == null) return;
        SharedPreferences.Editor editor = activity.getSharedPreferences(TAG,0).edit();
        editor.putString(PREF_OAUTH_TOKEN, socialAuth.getToken());
        editor.putString(PREF_OAUTH_ID,socialAuth.getId());
        editor.putString(PREF_OAUTH_NETWORK,TAG);
        editor.apply();
    }

    private SocialAuth getSocialAuth(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG,0);
        String id = sharedPreferences.getString(PREF_OAUTH_ID,null);
        if(TextUtils.isEmpty(id)) return null;
        String token = sharedPreferences.getString(PREF_OAUTH_TOKEN,null);
        String network = sharedPreferences.getString(PREF_OAUTH_NETWORK,null);
        SocialAuth socialAuth = new SocialAuth();
        socialAuth.id = id;
        socialAuth.network = network;
        socialAuth.token = token;
        return socialAuth;
    }


}
