package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

    private AbsListView.OnScrollListener onPostGridScrolled = new AbsListView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
        user.save();
        header.measure(View.MeasureSpec.makeMeasureSpec(RelativeLayout.LayoutParams.MATCH_PARENT, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(RelativeLayout.LayoutParams.WRAP_CONTENT, View.MeasureSpec.UNSPECIFIED));
        postsAdapter = new PostsGridHeaderAdapter(this);
        int height = this.header.getMeasuredHeight() - this.postsGrid.getVerticalSpacing();
        postsAdapter.setHeaderHeight(height);
        int sHeight = getResources().getDisplayMetrics().heightPixels;
        int sWidth = getResources().getDisplayMetrics().widthPixels;
        gridItemScale = ((sWidth / 2 - (this.postsGrid.getPaddingLeft() + this.postsGrid.getPaddingRight() + this.postsGrid.getHorizontalSpacing())) / sWidth);
        postsGrid.setAdapter(postsAdapter);
        postsGrid.setOnScrollListener(onPostGridScrolled);
        postsGrid.setOnItemClickListener(onPostClicked);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)emptyView.getLayoutParams();
        layoutParams.topMargin = (sHeight / 2);
        User me = QueryBuilder.me().get();
        if(me.id == user.id){
            toggleButton.setVisibility(View.GONE);
        }
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
}
