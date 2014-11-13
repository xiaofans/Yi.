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

import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.UserListAdapter;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.base.ParseBase;
import xiaofan.yiapp.service.FollowToggleService;
import xiaofan.yiapp.service.FollowingsSyncService;
import xiaofan.yiapp.utils.QueryBuilder;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class PopularActivity extends AuthenticatedActivity {

    public static Intent newIntent(Context context)
    {
        return new Intent(context, PopularActivity.class);
    }

    private UserListAdapter adapter;
    @InjectView(android.R.id.empty)
    TextView emptyView;
    @InjectView(R.id.list)
    ListView list;

    private User me;
    private ManyQuery.ResultHandler<User> onFollowingsLoaded = new ManyQuery.ResultHandler<User>()
    {

        @Override
        public boolean handleResult(CursorList<User> cursorList) {
            adapter.setFollowings(cursorList.asList());
            cursorList.close();
            return true;
        }
    };

    private Callback<ParseBase<ArrayList<User>>> onPopularLoaded = new Callback<ParseBase<ArrayList<User>>>() {
        @Override
        public void success(ParseBase<ArrayList<User>> result, Response response) {
            if(result.results != null && result.results.size() > 0){
                Iterator<User> iterator = result.results.iterator();
                while (iterator.hasNext()){
                    User user = iterator.next();
                    user.id = user.uid;
                }
            }
            adapter.setUsers(result.results);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    private AdapterView.OnItemClickListener onUserSelected = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> parent, View convertView, int position, long id)
        {
            startActivity(ProfileActivity.newIntent(PopularActivity.this, adapter.getItem(position)));
        }
    };

    private UserListAdapter.OnUserFollowToggledListener onUserFollowToggledListener = new UserListAdapter.OnUserFollowToggledListener()
    {
        @Override
        public void onUserFollowToggled(User user, boolean follow) {
            startService(FollowToggleService.newIntent(PopularActivity.this.getBaseContext(), user, follow));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        adapter = new UserListAdapter(this);
        adapter.setOnUserFollowToggledListener(onUserFollowToggledListener);
        list.setAdapter(adapter);
        list.setOnItemClickListener(onUserSelected);
        emptyView.setText(getString(R.string.fetch_popular_tip));
        list.setEmptyView(emptyView);
        me = QueryBuilder.me().get();
        QueryBuilder.followings(this.me).getAsync(getLoaderManager(),onFollowingsLoaded, Connection.class);
        startService(FollowingsSyncService.newIntent(this, me));
        ApiService.getInstance().getPopular(onPopularLoaded);
    }



}
