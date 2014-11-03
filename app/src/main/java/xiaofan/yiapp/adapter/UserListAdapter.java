package xiaofan.yiapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.RecycleAdapter;
import xiaofan.yiapp.view.AvatarCircleView;

/**
 * Created by zhaoyu on 2014/11/3.
 */
public class UserListAdapter extends RecycleAdapter{

    private final  Context context;
    private List<User> followings = new ArrayList<User>();
    private List<User> users;
    public UserListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_user_list_item,parent,false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    protected void prepareView(View view, int type, int position) {
        User user = users.get(position);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.userName.setText(user.name);
        if (!user.avatar.equals(viewHolder.avatar.getTag()))
        {
            viewHolder.avatar.setAvatar(null);
            Picasso.with(context).load(user.avatar).into(viewHolder.avatar);
            viewHolder.avatar.setTag(user.avatar);
        }
    }

    @Override
    public int getCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return this.users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).id;
    }

    static class ViewHolder{
        @InjectView(R.id.avatar)
        AvatarCircleView avatar;
        @InjectView(R.id.toggle_follow)
        ToggleButton followToggle;
        @InjectView(R.id.user_name)
        TextView userName;
        ViewHolder(View view)
        {
            ButterKnife.inject(this, view);
        }
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
}
