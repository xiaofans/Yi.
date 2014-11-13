package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.InjectView;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.UserListAdapter;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.service.FollowToggleService;
import xiaofan.yiapp.service.FollowingsSyncService;
import xiaofan.yiapp.utils.QueryBuilder;

/**
 * Created by zhaoyu on 2014/11/12.
 */
public class FollowingsListActivity extends AuthenticatedActivity{

    private UserListAdapter adapter;
    @InjectView(android.R.id.empty)
    TextView emptyView;
    private List<User> initialFollowings = new ArrayList<User>();
    @InjectView(R.id.list)
    ListView list;

    private User user;
    private ManyQuery.ResultHandler<User> onFollowingsLoaded = new ManyQuery.ResultHandler<User>()
    {
        public boolean handleResult(CursorList<User> cursorList)
        {
            List<User> list = cursorList.asList();
            cursorList.close();
            Iterator<User> iterator = list.iterator();
            while (iterator.hasNext())
            {
                User user = iterator.next();
                if (!initialFollowings.contains(user)) {
                    initialFollowings.add(user);
                }
            }
          adapter.setUsers(FollowingsListActivity.this.initialFollowings);
            if(adapter.getUsers() == null || adapter.getUsers().size() == 0){
                emptyView.setText(getString(R.string._no_followings_tip));
            }
          adapter.setFollowings(list);
           return true;
        }
    };

    private AdapterView.OnItemClickListener onUserSelected = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
            startActivity(ProfileActivity.newIntent(FollowingsListActivity.this,adapter.getItem(position)));
        }
    };

    private UserListAdapter.OnUserFollowToggledListener onUserFollowToggledListener = new UserListAdapter.OnUserFollowToggledListener()
    {
        @Override
        public void onUserFollowToggled(User user, boolean follow) {
            startService(FollowToggleService.newIntent(FollowingsListActivity.this.getBaseContext(), user, follow));
        }
    };
    public static Intent newIntent(Context context, User user)
    {
        Intent intent = new Intent(context, FollowingsListActivity.class);
        intent.putExtra("user", user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        user = getIntent().getParcelableExtra("user");
        adapter = new UserListAdapter(this);
        adapter.setOnUserFollowToggledListener(onUserFollowToggledListener);
        list.setAdapter(adapter);
        emptyView.setText(getString(R.string._fetch_following_tip));
        list.setEmptyView(emptyView);
        list.setOnItemClickListener(onUserSelected);
        QueryBuilder.followings(this.user).getAsync(getLoaderManager(), onFollowingsLoaded, Connection.class);
        startService(FollowingsSyncService.newIntent(this, user));
    }
}
