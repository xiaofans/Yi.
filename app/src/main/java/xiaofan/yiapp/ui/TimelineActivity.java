package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.PostsFragmentAdapter;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.fragment.DrawerFragment;
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
    private PostsFragmentAdapter pagerAdapter;
    private boolean firstLoad;
    private User me;
    private int pagerPosition;


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

    private ViewPager.OnPageChangeListener pageScrollListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
            Utils.hideKeyboard(TimelineActivity.this);
        }

        @Override
        public void onPageSelected(int position) {
            pagerPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        if(savedInstanceState != null){
            pagerPosition = savedInstanceState.getInt("current_item");
        }
        firstLoad = true;
        drawer.setDrawerListener(drawerCallbacks);
        Utils.addSystemUIPadding(this, this.drawerIndicator); // why
        pager.setPageMargin((int) (2.0F * getResources().getDisplayMetrics().density));
        this.pagerAdapter = new PostsFragmentAdapter(getSupportFragmentManager());
        this.pager.setAdapter(this.pagerAdapter);
        pager.setOnPageChangeListener(pageScrollListener);
        testUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_item",pager.getCurrentItem());

    }

    private void testUI() {
        List<Post> list = new ArrayList<Post>();
        Post post1 = new Post();
        post1.type = Post.TYPE_IMAGE;
        list.add(post1);

        Post post2 = new Post();
        post2.type = Post.TYPE_TEXT;
        list.add(post2);

        pagerAdapter.setPosts(list);
    }

    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context, TimelineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @OnClick(R.id.drawer_indicator)
    public void onDrawerIndicatorClicked(){
        drawer.openDrawer(Gravity.LEFT);
    }

    private void closeDrawerAndDo(Runnable runnable){
        drawer.closeDrawer(Gravity.LEFT);
        drawer.postDelayed(runnable , 200L);
    }

    @Subscribe
    public void popularSelected(DrawerFragment.PopularClickedEvent popularClickedEvent){
        closeDrawerAndDo(new Runnable() {
            @Override
            public void run() {
                startActivity(PopularActivity.newIntent(TimelineActivity.this));
            }
        });
    }
    @Subscribe
    public void settingsSelected(DrawerFragment.SettingsClickedEvent settingsClickedEvent){
        closeDrawerAndDo(new Runnable() {
            @Override
            public void run() {
                startActivity(SettingsActivity.newIntent(TimelineActivity.this));
            }
        });
    }
}
