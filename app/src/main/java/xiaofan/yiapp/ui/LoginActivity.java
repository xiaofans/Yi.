package xiaofan.yiapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


import java.util.Iterator;

import xiaofan.yiapp.R;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.utils.Utils;
import xiaofan.yiapp.base.BaseActivity;
import xiaofan.yiapp.view.PanningBackgroundFrameLayout;

/**
 * login width socal service.
 * 1.qq
 * 2.weibo
 * 3.wechat *
 */
public class LoginActivity extends BaseActivity{
    private PanningBackgroundFrameLayout panningBackgroundFrameLayout;
    private LinearLayout socialButtons;
    private ProgressDialog pd;
    private boolean loginInProgress;
    private View.OnClickListener OnSocialButtonClicked = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            startActivity(TimelineActivity.newIntent(LoginActivity.this));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        setUpViews();
    }
    private void setUpViews() {
        pd = new ProgressDialog(this);
        pd.setMessage("登录中...");
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                EventBus.post(new LogoutEvent());
                loginInProgress = false;
            }
        });
        panningBackgroundFrameLayout = (PanningBackgroundFrameLayout) findViewById(R.id.panning_bg);
        Utils.addSystemUIPadding(this,panningBackgroundFrameLayout);
        panningBackgroundFrameLayout.setPanningEnabled(true);
        panningBackgroundFrameLayout.setClickToZoomEnabled(true);
        panningBackgroundFrameLayout.setShouldAnimateBackgroundChange(false);
        panningBackgroundFrameLayout.setPanningBackground(BitmapFactory.decodeResource(getResources(),R.drawable.register_bg));

        socialButtons = (LinearLayout) findViewById(R.id.social_buttons);
        // login socail buttons
        Iterator<String> iterator = SocialApi.ALL.keySet().iterator();
        while (iterator.hasNext()){
            String str = iterator.next();
            View view = SocialApi.ALL.get(str).getSocialButton(this,socialButtons);
            view.setTag(str);
            view.setOnClickListener(this.OnSocialButtonClicked);
            socialButtons.addView(view);
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context,LoginActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loginInProgress){
            pd.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(loginInProgress && SocialApi.getCurrent(this) != null){
            SocialApi.getCurrent(this).onActivityResult(this,requestCode,resultCode,data);
        }
    }
}
