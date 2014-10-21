package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import xiaofan.yiapp.R;
import xiaofan.yiapp.base.AuthenticatedActivity;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class SettingsActivity extends AuthenticatedActivity{
    public static Intent newIntent(Context context)
    {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }
}
