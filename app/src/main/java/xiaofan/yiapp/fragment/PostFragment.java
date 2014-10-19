package xiaofan.yiapp.fragment;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.BaseFragment;
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
        this.post = ((Post)getArguments().getParcelable("post"));
        this.showAuthor = getArguments().getBoolean("show_author", true);
    }

    @OnClick(R.id.avatar)
    public void onAvatarClicked(){

    }

    @OnClick(R.id.comment_post)
    public void onCommentClicked()
    {

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
    }

    @OnClick(R.id.overflow)
    public void onOverflowClicked(View view)
    {


    }


}
