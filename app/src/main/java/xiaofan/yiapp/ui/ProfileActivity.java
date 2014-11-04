package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.service.FollowToggleService;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUser((User)getIntent().getParcelableExtra("user"));
        user.save();
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
}
