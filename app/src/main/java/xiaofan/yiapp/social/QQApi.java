package xiaofan.yiapp.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaofan.yiapp.R;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class QQApi extends SocialApi{

    public static final String TAG = "qq";

    @Override
    public void getSocialAuth(Context context, LoginCallback loginCallback) {

    }

    @Override
    public View getSocialButton(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.view_social_button_qq,parent,false);
    }

    @Override
    public void login(Activity activity, LoginCallback loginCallback) {

    }

    @Override
    public void logout(Context context) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {

    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {

    }
}