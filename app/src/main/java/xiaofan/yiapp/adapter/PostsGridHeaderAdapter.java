package xiaofan.yiapp.adapter;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.base.RecycleAdapter;
import xiaofan.yiapp.view.PanningBackgroundFrameLayout;

/**
 * Created by zhaoyu on 2014/11/13.
 */
public class PostsGridHeaderAdapter extends RecycleAdapter{

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_IMAGE_POST = 1;
    private static final int VIEW_TYPE_TEXT_POST = 2;
    private TimeInterpolator animInterpolator = new OvershootInterpolator(1.5f);
    private SparseBooleanArray animatedPositions = new SparseBooleanArray();
    private final Context context;
    private int headerHeight;
    private int postHeight;
    private List<Post> posts;
    private Random rand;


    public PostsGridHeaderAdapter(Context context) {
        super(context);
        this.context = context;
        this.rand = new Random();
    }

    @Override
    protected View createView(int type, ViewGroup parent) {
        switch (type){
            case VIEW_TYPE_HEADER:
                return new View(context);
            case VIEW_TYPE_IMAGE_POST:
                View imgaePostView = inflater.inflate(R.layout.view_small_image_post,parent,false);
                imgaePostView.setTag(new ImagePostViewHolder(imgaePostView));
                return imgaePostView;
            case VIEW_TYPE_TEXT_POST:
                View textPostView = inflater.inflate(R.layout.view_small_image_post,parent,false);
                textPostView.setTag(new TextPostViewHolder(textPostView));
                return textPostView;
                default:
                    return null;
        }
    }

    @Override
    protected void prepareView(View view, int type, int position) {
        if(!animatedPositions.get(position)){
            view.setScaleX(0.8F);
            view.setScaleY(0.8F);
            view.setAlpha(0.0F);
            view.animate().scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setStartDelay(100 + this.rand.nextInt(100)).setDuration(300L).setInterpolator(this.animInterpolator);
        }
        Post post = getItem(position);
        switch (type){
            case VIEW_TYPE_HEADER:
                view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, this.headerHeight));
                break;
            case VIEW_TYPE_IMAGE_POST:
                AbsListView.LayoutParams localLayoutParams = (AbsListView.LayoutParams)view.getLayoutParams();
                localLayoutParams.height = this.postHeight;
                view.setLayoutParams(localLayoutParams);
                AbsListView.LayoutParams params = (AbsListView.LayoutParams)view.getLayoutParams();
                params.height = this.postHeight;
                ImagePostViewHolder imagePostViewHolder = (ImagePostViewHolder)view.getTag();
                imagePostViewHolder.day.setText(parseDay(post.createdAt));
                imagePostViewHolder.content.setText(post.text);
                imagePostViewHolder.postView.setPanningBackground(null);
                Picasso.with(context).load(post.image).into(imagePostViewHolder.postView);
                break;
            case VIEW_TYPE_TEXT_POST:
                AbsListView.LayoutParams localLayoutParams2 = (AbsListView.LayoutParams)view.getLayoutParams();
                localLayoutParams2.height = this.postHeight;
                view.setLayoutParams(localLayoutParams2);
                TextPostViewHolder localTextPostViewHolder = (TextPostViewHolder)view.getTag();
                localTextPostViewHolder.postView.setBackgroundColor(Color.parseColor(post.color));
                localTextPostViewHolder.day.setText(parseDay(post.createdAt));
                localTextPostViewHolder.content.setText(post.text);
                break;
        }

        animatedPositions.put(position, true);
      /*  view.animate().cancel();
        view.setScaleX(1.0F);
        view.setScaleY(1.0F);
        view.setAlpha(1.0F);*/
    }

    public String parseDay(Date createdAt){
        String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'hh:ss:mm.SSS'Z'").format(createdAt);
        if(TextUtils.isEmpty(dateStr)){
            return "";
        }else{
            dateStr = dateStr.substring(0,dateStr.indexOf("T"));
            return dateStr;
        }
    }


    @Override
    public int getCount() {
        if(posts == null) return 2;
        return posts.size() + 2;
    }

    @Override
    public Post getItem(int position) {
        if(position < 2) return null;
        return posts.get(position - 2);
    }

    @Override
    public long getItemId(int position) {
        if(position < 2){
            return  -1;
        }
        return posts.get(position - 2).pid;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < 2){
            return VIEW_TYPE_HEADER;
        }else if(Post.TYPE_TEXT.equals(getItem(position).type)){
            return VIEW_TYPE_TEXT_POST;
        }else if(Post.TYPE_IMAGE.equals(getItem(position).type)){
            return VIEW_TYPE_IMAGE_POST;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
        notifyDataSetChanged();
    }

    public void setPostHeight(int postHeight) {
        this.postHeight = postHeight;
        notifyDataSetChanged();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        animatedPositions.clear();
        notifyDataSetChanged();
    }

    static class ImagePostViewHolder
    {
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.day)
        TextView day;
        @InjectView(R.id.post)
        PanningBackgroundFrameLayout postView;

        public ImagePostViewHolder(View view)
        {
            ButterKnife.inject(this, view);
        }
    }

    static class TextPostViewHolder
    {
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.day)
        TextView day;
        @InjectView(R.id.post)
        View postView;

        public TextPostViewHolder(View paramView)
        {
            ButterKnife.inject(this, paramView);
        }
    }
}
