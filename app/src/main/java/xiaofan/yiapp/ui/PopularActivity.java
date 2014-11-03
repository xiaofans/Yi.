package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.UserListAdapter;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.base.ParseBase;
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

    private Callback<ParseBase<ArrayList<User>>> onPopularLoaded = new Callback<ParseBase<ArrayList<User>>>() {
        @Override
        public void success(ParseBase<ArrayList<User>> result, Response response) {
            adapter.setUsers(result.results);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        adapter = new UserListAdapter(this);
        list.setAdapter(adapter);
        me = QueryBuilder.me().get();
        ApiService.getInstance().getPopular(onPopularLoaded);
    }



}
