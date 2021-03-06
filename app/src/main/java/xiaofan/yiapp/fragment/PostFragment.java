package xiaofan.yiapp.fragment;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.api.entity.HeartToggle;
import xiaofan.yiapp.base.BaseFragment;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.UserClickEvent;
import xiaofan.yiapp.service.DeletePostService;
import xiaofan.yiapp.service.HeartToggleService;
import xiaofan.yiapp.ui.PostCommentsActivity;
import xiaofan.yiapp.utils.MultiChoicePopup;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;
import xiaofan.yiapp.view.AvatarCircleView;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public abstract class PostFragment extends BaseFragment{

    public static final PropertyValuesHolder pvhScaleX;
    public static final PropertyValuesHolder pvhScaleY;

    @InjectView(R.id.avatar)
    AvatarCircleView avatar;
    @InjectView(R.id.comment_post_counter)
    TextView commentCounter;
    @InjectView(R.id.content)
    TextView content;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.day)
    TextView day;
    @InjectView(R.id.heart_post_counter)
    TextView heartCounter;
    @InjectView(R.id.heart_post_image)
    ImageView heartImage;
    @InjectView(R.id.overflow)
    View overflow;
    protected Post post;
    protected boolean showAuthor;
    protected User me;
    protected User author;
    static {
        Property xProperty = View.SCALE_X;
        Keyframe[] xArrayKeyframe = new Keyframe[3];
        xArrayKeyframe[0] = Keyframe.ofFloat(0.0F,1.0F);
        xArrayKeyframe[1] = Keyframe.ofFloat(0.5F,1.3F);
        xArrayKeyframe[2] = Keyframe.ofFloat(1.0F,1.0F);
        pvhScaleX = PropertyValuesHolder.ofKeyframe(xProperty,xArrayKeyframe);
        Property yProperty = View.SCALE_Y;
        Keyframe[] yArrayKeyframe = new Keyframe[3];
        yArrayKeyframe[0] = Keyframe.ofFloat(0.0F,1.0F);
        yArrayKeyframe[1] = Keyframe.ofFloat(0.5F,1.3F);
        yArrayKeyframe[2] = Keyframe.ofFloat(1.0F,1.0F);
        pvhScaleY = PropertyValuesHolder.ofKeyframe(yProperty,yArrayKeyframe);
    }

    public static PostFragment newInstance(Post post){
        return newInstance(post,true);
    }

    public static PostFragment newInstance(Post post, boolean showAuthor) {
        String str = post.type;
        PostFragment postFragment = null;
        if(Post.TYPE_IMAGE.equals(str)){
            postFragment = new ImagePostFragment();
        }else if(Post.TYPE_TEXT.equals(str)){
            postFragment = new TextPostFragment();
        }else{
            throw new IllegalArgumentException("Post type not valid");
        }
        if(postFragment != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("post", post);
            bundle.putBoolean("show_author", showAuthor);
            postFragment.setArguments(bundle);
        }
        return postFragment;
    }

    public Post getPost(){
        if(post == null){
            return getArguments().getParcelable("post");
        }
        return post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post = ((Post)getArguments().getParcelable("post"));
        me = QueryBuilder.me().get();
        showAuthor = getArguments().getBoolean("show_author", true);
        if(showAuthor){
            author = QueryBuilder.user(post.authorId).get();
        }
    }

    @OnClick(R.id.avatar)
    public void onAvatarClicked(){
        EventBus.post(new UserClickEvent(this.author));
    }

    @OnClick(R.id.comment_post)
    public void onCommentClicked()
    {
        startActivity(PostCommentsActivity.newIntent(getActivity(),post));
    }

    @OnClick(R.id.heart_post)
    public void onHeartClicked()
    {
        // animaction
        PropertyValuesHolder[] valuesHolders = new PropertyValuesHolder[2];
        valuesHolders[0] = pvhScaleX;
        valuesHolders[1] = pvhScaleY;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(heartImage, valuesHolders);
        localObjectAnimator.setDuration(500L);
        localObjectAnimator.setInterpolator(new OvershootInterpolator());
        localObjectAnimator.start();
        if(post.hasHearted){
            if(post.heartCount > 0){
                post.heartCount -= 1;
            }
            heartImage.setImageResource(R.drawable.ic_heart_empty);
        }else{
            post.heartCount += 1;
            heartImage.setImageResource(R.drawable.ic_heart_filled);
        }
        post.hasHearted = !post.hasHearted;
        heartCounter.setText(""+post.heartCount);
        HeartToggle heartToggle = new HeartToggle();
        heartToggle.hasHearted = post.hasHearted;
        heartToggle.authorId = me.id;
        heartToggle.postId = post.pid;
        getActivity().startService(HeartToggleService.newIntent(getActivity(), post,heartToggle));
    }

    @OnClick(R.id.overflow)
    public void onOverflowClicked(View view)
    {
        MultiChoicePopup multiChoicePopup = new MultiChoicePopup(getActivity(), new MultiChoicePopup.OnItemSelectedListener()
        {
            public void onItemSelected(int position)
            {
                new PostFragment.DeleteStatusMonitor(getActivity()).run();
                getActivity().startService(DeletePostService.newIntent(getActivity(), post));
            }
        });
        multiChoicePopup.addChoice(getString(R.string._delete_post));
        multiChoicePopup.show(view);
    }

    private class DeleteStatusMonitor
    {
        private Context context;
        private ProgressDialog pd;

        private DeleteStatusMonitor(Context context)
        {
            this.context = context;
            this.pd = new ProgressDialog(context);
            this.pd.setMessage(context.getString(R.string._delete_tip_));
            this.pd.setCancelable(false);
        }

        @Subscribe
        public void postDeleteFail(DeletePostService.FailureEvent failure)
        {
            Utils.showErrorDialog(this.context, context.getString(R.string._delete_post_failure_tip));
            pd.dismiss();
            EventBus.unregister(this);
        }

        @Subscribe
        public void postDeleteSuccess(DeletePostService.SuccessEvent success)
        {
            pd.dismiss();
            EventBus.unregister(this);
        }

        public void run()
        {
            EventBus.register(this);
            pd.show();
        }
    }
}
