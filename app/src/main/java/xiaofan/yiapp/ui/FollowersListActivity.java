package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.InjectView;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.UserListAdapter;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.service.FollowToggleService;
import xiaofan.yiapp.service.FollowersSyncService;
import xiaofan.yiapp.service.FollowingsSyncService;
import xiaofan.yiapp.utils.QueryBuilder;

/**
 * Created by zhaoyu on 2014/11/12.
 */
public class FollowersListActivity extends AuthenticatedActivity{

    private UserListAdapter adapter;
    @InjectView(android.R.id.empty)
    TextView emptyView;
    @InjectView(R.id.list)
    ListView list;
    private User me;
    private User user;

    public static Intent newIntent(Context context, User user)
    {
        Intent localIntent = new Intent(context, FollowersListActivity.class);
        localIntent.putExtra("user", user);
        return localIntent;
    }
    private ManyQuery.ResultHandler<User> onFollowersLoaded = new ManyQuery.ResultHandler<User>()
    {

        @Override
        public boolean handleResult(CursorList<User> cursorList) {
            adapter.setUsers(cursorList.asList());
            if(adapter.getUsers() == null || adapter.getUsers().size() == 0){
                emptyView.setText(getString(R.string._no_followers_tip));
            }
            cursorList.close();
            return true;
        }
    };
    private ManyQuery.ResultHandler<User> onFollowingsLoaded = new ManyQuery.ResultHandler<User>()
    {
        @Override
        public boolean handleResult(CursorList cursorList) {
           adapter.setFollowings(cursorList.asList());
            cursorList.close();
            return true;
        }
    };

    private AdapterView.OnItemClickListener onUserSelected = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
           startActivity(ProfileActivity.newIntent(FollowersListActivity.this, adapter.getItem(position)));
        }
    };

    private UserListAdapter.OnUserFollowToggledListener onUserFollowToggledListener = new UserListAdapter.OnUserFollowToggledListener()
    {
        @Override
        public void onUserFollowToggled(User user, boolean follow) {
           startService(FollowToggleService.newIntent(FollowersListActivity.this.getBaseContext(), user, follow));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        user = getIntent().getParcelableExtra("user");
        adapter = new UserListAdapter(this);
        adapter.setOnUserFollowToggledListener(onUserFollowToggledListener);
        list.setAdapter(adapter);
        emptyView.setText(getString(R.string._fetch_followers_tip));
        list.setEmptyView(emptyView);
        list.setOnItemClickListener(onUserSelected);
        me = QueryBuilder.me().get();
        QueryBuilder.followers(user).getAsync(getLoaderManager(), onFollowersLoaded, Connection.class);
        QueryBuilder.followings(me).getAsync(getLoaderManager(), onFollowingsLoaded, Connection.class);
        startService(FollowersSyncService.newIntent(this, user));
        startService(FollowingsSyncService.newIntent(this, me));
    }
}
