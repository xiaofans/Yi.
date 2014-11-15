package xiaofan.yiapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.Comment;
import xiaofan.yiapp.base.RecycleAdapter;
import xiaofan.yiapp.view.AvatarCircleView;

/**
 * Created by zhaoyu on 2014/11/14.
 */
public class CommentsAdapter extends RecycleAdapter{

    private ArrayList<Comment> comments;
    private Context context;
    public CommentsAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View createView(int type, ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_comment_list_item, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    protected void prepareView(View view, int type, int position) {
        Comment comment = this.comments.get(position);
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.text.setText(comment.text);
        if(viewHolder.createdAt != null){
            viewHolder.createdAt.setText(parseDay(comment.createdAt));
        }
        if (!comment.author.avatar.equals(viewHolder.avatar.getTag()))
        {
            viewHolder.avatar.setAvatar(null);
            Picasso.with(context).load(comment.author.avatar).into(viewHolder.avatar);
            viewHolder.avatar.setTag(comment.author.avatar);
        }
    }

    @Override
    public int getCount() {
        if(comments == null) return 0;
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        if(comments == null) return null;
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 10000L + position;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }


    public void addComment(Comment comment)
    {
        comments.add(comment);
        notifyDataSetChanged();
    }

    static class ViewHolder
    {
        @InjectView(R.id.avatar)
        AvatarCircleView avatar;
        @InjectView(R.id.created_at)
        TextView createdAt;
        @InjectView(R.id.text)
        TextView text;

        ViewHolder(View paramView)
        {
            ButterKnife.inject(this, paramView);
        }
    }

    public String parseDay(Date date){
        String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'hh:ss:mm.SSS'Z'").format(date);
        if(TextUtils.isEmpty(dateStr)){
            return "";
        }else{
            dateStr = dateStr.substring(0,dateStr.indexOf("T"));
            return dateStr;
        }
    }
}
