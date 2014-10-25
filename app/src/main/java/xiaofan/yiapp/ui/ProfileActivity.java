package xiaofan.yiapp.ui;

import android.content.Intent;
import android.os.Bundle;

import xiaofan.yiapp.R;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class ProfileActivity extends AuthenticatedActivity{
    public static Intent newIntent(TimelineActivity activity, User user) {
        return new Intent(activity,ProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
