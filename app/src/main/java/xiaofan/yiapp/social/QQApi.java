package xiaofan.yiapp.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import xiaofan.yiapp.R;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class QQApi extends SocialApi{

    /**
     * APP ID:1103477785
     APP KEY:AZy4B8MtllEIBOBA
     */
    public static final String TAG = "qq";
    public static Tencent mTencent;
    public static final String mAppid = "1103477785";

    private static final String PREF_OAUTH_TOKEN = "qq_oauth_token";
    private static final String PREF_OAUTH_NETWORK = "qq_oauth_network";
    private static final String PREF_OAUTH_ID = "qq_oauth_id";

    private static final String PREF_OAUTH_NAME = "qq_oauth_name";
    private static final String PREF_OAUTH_AVATAR = "qq_oauth_avatar";

    private Activity activity;
    private LoginCallback loginCallback;
    private UserInfo mInfo;

    class LoginListener extends BaseUiListener{
        private LoginCallback loginCallback;

        LoginListener(LoginCallback loginCallback) {
            this.loginCallback = loginCallback;
        }

        @Override
        protected void doComplete(JSONObject jsonObject) {
            try {
                String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                        && !TextUtils.isEmpty(openId)) {
                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
                }
                SocialAuth socialAuth = new SocialAuth();
                socialAuth.id = openId;
                socialAuth.token = token;
                socialAuth.network = TAG;

               // loginCallback.success(socialAuth);
                updateUserInfo(socialAuth,loginCallback);
            } catch(Exception e) {
                loginCallback.failure(new LoginError(e.toString(),false));
            }
        }

        @Override
        public void onError(UiError uiError) {
            loginCallback.failure(new LoginError(uiError.errorMessage + " "+uiError.errorDetail,false));
        }
    }

    private void updateUserInfo(final SocialAuth socialAuth, final LoginCallback loginCallback) {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    loginCallback.failure(new LoginError(e.toString(),false));
                }

                @Override
                public void onComplete(final Object response) {
                    if(response != null){
                        JSONObject jsonObject = (JSONObject) response;
                        try {
                            if (jsonObject.has("nickname")) {
                                    socialAuth.name = jsonObject.getString("nickname");
                            }
                            if(jsonObject.has("figureurl")){
                                socialAuth.avatar = jsonObject.getString("figureurl_qq_2");
                            }
                            saveQQToken(socialAuth);
                            loginCallback.success(socialAuth);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loginCallback.failure(new LoginError("null json",false));
                        }
                    }else{
                        loginCallback.failure(new LoginError("null json",false));
                    }
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(activity, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        } else {

        }
    }



    private void saveQQToken(SocialAuth socialAuth) {
        if(socialAuth == null) return;
        SharedPreferences.Editor editor = activity.getSharedPreferences(TAG,0).edit();
        editor.putString(PREF_OAUTH_TOKEN, socialAuth.getToken());
        editor.putString(PREF_OAUTH_ID,socialAuth.getId());
        editor.putString(PREF_OAUTH_NETWORK,TAG);
        editor.putString(PREF_OAUTH_NAME,socialAuth.name);
        editor.putString(PREF_OAUTH_AVATAR,socialAuth.avatar);
        editor.apply();
    }

    private SocialAuth getSocialAuth(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG,0);
        String id = sharedPreferences.getString(PREF_OAUTH_ID,null);
        if(TextUtils.isEmpty(id)) return null;
        String token = sharedPreferences.getString(PREF_OAUTH_TOKEN,null);
        String network = sharedPreferences.getString(PREF_OAUTH_NETWORK,null);
        String name = sharedPreferences.getString(PREF_OAUTH_NAME,null);
        String avatar = sharedPreferences.getString(PREF_OAUTH_AVATAR,null);
        SocialAuth socialAuth = new SocialAuth();
        socialAuth.id = id;
        socialAuth.network = network;
        socialAuth.token = token;
        socialAuth.name = name;
        socialAuth.avatar = avatar;
        return socialAuth;
    }


    @Override
    public void getSocialAuth(Context context, LoginCallback loginCallback) {
        SocialAuth socialAuth = getSocialAuth(context);
        this.loginCallback = loginCallback;
        if(socialAuth != null){
            loginCallback.success(socialAuth);
        }else {
            loginCallback.failure(new LoginError("NULL",false));
        }
    }

    @Override
    public View getSocialButton(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.view_social_button_qq,parent,false);
    }

    @Override
    public void login(Activity activity, LoginCallback loginCallback) {
        this.activity = activity;
        this.loginCallback = loginCallback;
        if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, activity);
        }
        mTencent.login(activity, "all", new LoginListener(loginCallback));
    }

    @Override
    public void logout(Context context) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, new LoginListener(loginCallback));
                Log.d(TAG, "-->onActivityResult handle logindata");
            }
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {

    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Log.w(TAG, "返回为空,登录失败");

                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Log.w(TAG ,"返回为空登录失败");
                return;
            }
            Log.w(TAG,"登录成功"+response.toString());
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Log.w(TAG, "onError: " + e.errorDetail);
        }

        @Override
        public void onCancel() {
            Log.w(TAG,"onCancel---");
        }
    }
}
