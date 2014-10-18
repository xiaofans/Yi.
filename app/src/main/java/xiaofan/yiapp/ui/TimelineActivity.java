package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;

import butterknife.InjectView;
import butterknife.OnClick;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import xiaofan.yiapp.R;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class TimelineActivity extends AuthenticatedActivity{
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.drawer_indicator)
    ImageView drawerIndicator;

    @InjectView(R.id.view_pager)
    VerticalViewPager pager;

    private DrawerLayout.DrawerListener drawerCallbacks = new DrawerLayout.DrawerListener(){

        @Override
        public void onDrawerSlide(View view, float v) {
            float f = (float)(0.92D * v + 1.0F * (1.0F - v));
            pager.setScaleX(f);
            pager.setScaleY(f);
        }

        @Override
        public void onDrawerOpened(View view) {

        }

        @Override
        public void onDrawerClosed(View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {
            Utils.hideKeyboard(TimelineActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        drawer.setDrawerListener(drawerCallbacks);
        Utils.addSystemUIPadding(this, this.drawerIndicator); // why
        pager.setPageMargin((int)(2.0F * getResources().getDisplayMetrics().density));
    }

    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context, TimelineActivity.class);
        return intent;
    }

    @OnClick(R.id.drawer_indicator)
    public void onDrawerIndicatorClicked(){
        drawer.openDrawer(3);
    }
}
