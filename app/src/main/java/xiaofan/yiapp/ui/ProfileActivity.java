package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.OneQuery;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.PostsGridHeaderAdapter;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.fragment.ImagePostFragment;
import xiaofan.yiapp.fragment.PostFragment;
import xiaofan.yiapp.service.FollowToggleService;
import xiaofan.yiapp.service.PostsSyncService;
import xiaofan.yiapp.service.UserSyncService;
import xiaofan.yiapp.utils.QueryBuilder;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class ProfileActivity extends AuthenticatedActivity{
    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context,ProfileActivity.class);
        intent.putExtra("user",user);
        return intent;
    }

    @InjectView(R.id.avatar)
    ImageView avatar;
    @InjectView(android.R.id.empty)
    TextView emptyView;
    @InjectView(R.id.fake_action_bar)
    ViewGroup fakeActionBar;
    @InjectView(R.id.followers_count)
    TextView followersCount;
    @InjectView(R.id.following_count)
    TextView followingCount;
    @InjectView(R.id.header)
    View header;

    @InjectView(R.id.post_detail_container)
    FrameLayout postDetailContainer;
    @InjectView(R.id.posts_grid)
    GridView postsGrid;

    @InjectView(R.id.toggle_follow)
    ToggleButton toggleButton;
    @InjectView(R.id.user_name)
    Button userName;

    private User user;

    private Post detailPost;
    private int detailPosition;
    private PostsGridHeaderAdapter postsAdapter;
    private double gridItemScale;

    private Spring scaleSpring;
    private SpringSystem springSystem;

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private SpringListener detailScaleSpringListener = new SpringListener()
    {

        @Override
        public void onSpringUpdate(Spring spring) {
            View localView = postsGrid.getChildAt(ProfileActivity.this.detailPosition);
            if(localView == null) return;
            double d1 = spring.getCurrentValue();
            double d2 = (d1 - ProfileActivity.this.gridItemScale) / (1.0D - ProfileActivity.this.gridItemScale);
            float f1 = localView.getLeft() + localView.getWidth() / 2;
            float f2 = localView.getTop() + localView.getHeight() / 2;
            float f3 = ProfileActivity.this.postDetailContainer.getWidth() / 2;
            float f4 = ProfileActivity.this.postDetailContainer.getHeight() / 2;
            localView.setScaleX((float)(d1 / ProfileActivity.this.gridItemScale));
            localView.setScaleY((float)(d1 / ProfileActivity.this.gridItemScale));
            localView.setTranslationX((float)(d2 * (f3 - f1) + 0.0D * (1.0D - d2)));
            localView.setTranslationY((float)(d2 * (f4 - f2) + 0.0D * (1.0D - d2)));
            postDetailContainer.setScaleX((float)d1);
            postDetailContainer.setScaleY((float)d1);
            postDetailContainer.setTranslationX((float)(0.0D * d2 + (1.0D - d2) * (f1 - f3)));
            postDetailContainer.setTranslationY((float)(0.0D * d2 + (1.0D - d2) * (f2 - f4)));
            postDetailContainer.setAlpha((float)(1.0D * d2 + 0.0D * (1.0D - d2)));
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            if (spring.getCurrentValue() != 1.0D)
            {
                Fragment fragment =getSupportFragmentManager().findFragmentById(R.id.post_detail_container);
               getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }

        @Override
        public void onSpringActivate(Spring spring) {

        }

        @Override
        public void onSpringEndStateChange(Spring spring) {

        }
    };

    private AbsListView.OnScrollListener onPostGridScrolled = new AbsListView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            float disHeight = fakeActionBar.getHeight() - header.getHeight();
            float transHeight = 0.0f;
            if(postsGrid.getChildCount() > 0){
                transHeight =  Math.min(0.0F, Math.max(disHeight, postsGrid.getChildAt(0).getBottom() - header.getHeight()));
                if(postsGrid.getFirstVisiblePosition() != 0){
                    transHeight = disHeight;
                }
            }
            float f3 = Math.min(transHeight - disHeight / 2.0F, 0.0F) / (disHeight / 2.0F);
            float f4 = f3 * (f3 * f3);
            fakeActionBar.getBackground().setAlpha((int)(255.0F * f4 + 0.0F * (1.0F - f4)));
            int j = (int)(0.0F * f4 + 255.0F * (1.0F - f4));
            userName.setTextColor(Color.argb(255, j, j, j));
            header.setTranslationY(transHeight);
            int k = Color.argb((int)(0.0F * f4 + 255.0F * (1.0F - f4)), 51, 51, 51);
            userName.setShadowLayer(2.0F, 0.0F, 2.0F, k);
        }
    };

    private ManyQuery.ResultHandler<Post> onPostsLoaded = new ManyQuery.ResultHandler<Post>()
    {
        @Override
        public boolean handleResult(CursorList<Post> posts) {
            postsAdapter.setPosts(posts.asList());
            return true;
        }
    };
    private OneQuery.ResultHandler<User> onUserChanged = new OneQuery.ResultHandler<User>()
    {

        @Override
        public boolean handleResult(User user) {
            if(user != null){
                setUser(user);
            }
            return true;
        }
    };

    private AdapterView.OnItemClickListener onPostClicked = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
            if(position < 2) return;
            detailPosition = position - parent.getFirstVisiblePosition();
            detailPost = postsAdapter.getItem(position);
            PostFragment postFragment = PostFragment.newInstance(detailPost, false);
            if ((postFragment instanceof ImagePostFragment)) {
                postFragment.getArguments().putBoolean("extra_animate_new_background", false);
            }
           getSupportFragmentManager().beginTransaction().replace(R.id.post_detail_container, postFragment).commit();
           scaleSpring.setVelocity(10.0D);
           scaleSpring.setEndValue(1.0D);

        }
    };

    private OneQuery.ResultHandler<Connection> onFollowStatusChanged = new OneQuery.ResultHandler<Connection>()
    {

        @Override
        public boolean handleResult(Connection connection) {
            if(connection != null){
                toggleButton.setChecked(true);
            }else{
                toggleButton.setChecked(false);
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if(savedInstanceState != null){
            detailPost = savedInstanceState.getParcelable("detailPost");
            detailPosition = savedInstanceState.getInt("detailPosition");
        }
        setUser((User)getIntent().getParcelableExtra("user"));
        User me = QueryBuilder.me().get();
        if(me.id == user.id){
            user.me = true;
        }else{
            user.me = false;
        }
        user.save();
        header.measure(View.MeasureSpec.makeMeasureSpec(RelativeLayout.LayoutParams.MATCH_PARENT, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(RelativeLayout.LayoutParams.WRAP_CONTENT, View.MeasureSpec.UNSPECIFIED));
        postsAdapter = new PostsGridHeaderAdapter(this);
        int height = this.header.getMeasuredHeight() - this.postsGrid.getVerticalSpacing();
        postsAdapter.setHeaderHeight(height);
        int sHeight = getResources().getDisplayMetrics().heightPixels;
        int sWidth = getResources().getDisplayMetrics().widthPixels;
        gridItemScale = ((double)(sWidth / 2 - (postsGrid.getPaddingLeft() + postsGrid.getPaddingRight() + postsGrid.getHorizontalSpacing())) / (double)sWidth);
        postsAdapter.setPostHeight((int)(sHeight * gridItemScale));
        postsGrid.setAdapter(postsAdapter);
        postsGrid.setOnScrollListener(onPostGridScrolled);
        postsGrid.setOnItemClickListener(onPostClicked);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)emptyView.getLayoutParams();
        layoutParams.topMargin = (sHeight / 2);
        if(me.id == user.id){
            toggleButton.setVisibility(View.GONE);
        }
        springSystem = SpringSystem.create();
        scaleSpring = springSystem.createSpring();
        scaleSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(60.0D, 9.0D));
        scaleSpring.setCurrentValue(gridItemScale);
        this.scaleSpring.addListener(detailScaleSpringListener);
        QueryBuilder.user(user.id).getAsync(getLoaderManager(), this.onUserChanged, Connection.class);
        QueryBuilder.posts(user).getAsync(getLoaderManager(), this.onPostsLoaded, Post.class);
        startService(PostsSyncService.newIntent(this, user));
        startService(UserSyncService.newIntent(this, user));
        QueryBuilder.connection(me, user).getAsync(getLoaderManager(), onFollowStatusChanged, new Class[0]);
    }

    public void setUser(User user) {
        this.user = user;
        Picasso.with(this).load(user.avatar).into(this.avatar);
        userName.setText(user.name);
        followersCount.setText(getString(R.string.num_followers,user.followersCount));
        followingCount.setText(getString(R.string.num_followings,user.followingsCount));
    }

    @OnClick(R.id.toggle_follow)
    public void onFollowToggleClicked(ToggleButton toggleButton){
        startService(FollowToggleService.newIntent(getBaseContext(), this.user, toggleButton.isChecked()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("detailPost", detailPost);
        outState.putInt("detailPosition", detailPosition);
    }

    @Override
    public void onBackPressed() {
        if (!dispatchBackPressedToFragments())
        {
            if (this.detailPost != null)
            {
                this.detailPost = null;
                this.scaleSpring.setVelocity(-10.0D);
                this.scaleSpring.setEndValue(gridItemScale);
            }else{
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        springSystem.removeAllListeners();
    }

    @OnClick(R.id.user_name)
    public void onUpClicked()
    {
        finish();
    }
}
