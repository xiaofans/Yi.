package xiaofan.yiapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import se.emilsjolander.sprinkles.OneQuery;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.BaseFragment;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.events.UserClickedEvent;
import xiaofan.yiapp.utils.QueryBuilder;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class DrawerFragment extends BaseFragment{
    @InjectView(R.id.avatar)
    ImageView avatar;
    @InjectView(R.id.followers_count)
    TextView followerCount;
    @InjectView(R.id.following_count)
    TextView followingCount;
    @InjectView(R.id.user_name)
    TextView userName;
    private User me;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View drawerView = inflater.inflate(R.layout.fragment_drawer,container,false);
        ButterKnife.inject(this,drawerView);
        QueryBuilder.me().getAsync(getLoaderManager(),new OneQuery.ResultHandler<User>() {
            @Override
            public boolean handleResult(User user) {
                if(user == null) return false;
               followerCount.setText("" + user.followersCount);
               followingCount.setText("" + user.followingsCount);
               userName.setText(user.name);
               Picasso.with(DrawerFragment.this.getActivity()).load(user.avatar).into(DrawerFragment.this.avatar);
                return true;
            }
        });
        return drawerView;
    }

    @OnClick(R.id.followers_count)
    public void followers(){
        EventBus.post(new FollowersClickedEvent());
    }

    @OnClick(R.id.following_count)
    public void following(){
        EventBus.post(new FollowingClickedEvent());
    }

    @OnClick(R.id.log_out)
    public void logout(){
        EventBus.post(new LogoutEvent());
    }

    @OnClick(R.id.popular)
    public void popular(){
        EventBus.post(new PopularClickedEvent());
    }

    @OnClick(R.id.settings)
    public void settings(){
        EventBus.post(new SettingsClickedEvent());
    }

    @OnClick(R.id.avatar)
    public void profile(){
        EventBus.post(new UserClickedEvent(me));
    }

    public static  class FollowersClickedEvent{}
    public static  class FollowingClickedEvent{}
    public static  class PopularClickedEvent{}
    public static  class SettingsClickedEvent{}
}
