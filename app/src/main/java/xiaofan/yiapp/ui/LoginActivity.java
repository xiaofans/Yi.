package xiaofan.yiapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.squareup.otto.Subscribe;

import java.util.Iterator;

import xiaofan.yiapp.R;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.service.AuthenticationService;
import xiaofan.yiapp.social.LoginCallback;
import xiaofan.yiapp.social.LoginError;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.social.SocialAuth;
import xiaofan.yiapp.social.WeiboApi;
import xiaofan.yiapp.utils.QueryBuilder;
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
            loginInProgress = true;
            SocialApi.setCurrent(LoginActivity.this,(String)view.getTag());
           //if(WeiboApi.TAG.equals(view.getTag())){
                SocialApi.getCurrent(LoginActivity.this).login(LoginActivity.this,new LoginCallback() {
                    @Override
                    public void failure(LoginError loginError) {
                        Toast.makeText(LoginActivity.this,"WeiBo login failure! " + loginError.error,Toast.LENGTH_LONG).show();
                        Utils.showErrorDialog(LoginActivity.this,getString(R.string._login_fail_tip));
                    }

                    @Override
                    public void success(SocialAuth socialAuth) {
                       // Toast.makeText(LoginActivity.this,"WeiBo login success!" + socialAuth.toString(),Toast.LENGTH_LONG).show();
                        pd.show();
                        startService(AuthenticationService.newIntent(LoginActivity.this));
                    }
                });
//           }else{
//               startActivity(TimelineActivity.newIntent(LoginActivity.this));
//           }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Utils.fitScreenIfNeeded(this);
        if(SocialApi.getCurrent(this) != null){
            if(QueryBuilder.me().get() != null){
                startService(AuthenticationService.newIntent(this));
                startActivity(TimelineActivity.newIntent(this));
                finish();
            }
        }
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
        panningBackgroundFrameLayout.setPanningBackground(BitmapFactory.decodeResource(getResources(),R.drawable.rb));

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

    @Subscribe
    public void authenticationSuccess(AuthenticationService.SuccessEvent successEvent){
        pd.dismiss();
        startActivity(TimelineActivity.newIntent(LoginActivity.this));
        finish();
    }

    @Subscribe
    public void authenticationFailure(AuthenticationService.FailureEvent failureEvent){
        pd.dismiss();
        Utils.showErrorDialog(LoginActivity.this,getString(R.string._login_fail_tip));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(loginInProgress && SocialApi.getCurrent(this) != null){
            SocialApi.getCurrent(this).onNewIntent(this, intent);
        }
    }
}
