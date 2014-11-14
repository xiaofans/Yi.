package xiaofan.yiapp.fragment;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import xiaofan.yiapp.R;
import xiaofan.yiapp.utils.Utils;
import xiaofan.yiapp.view.PanningBackgroundFrameLayout;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class ImagePostFragment extends PostFragment{

    private static final String TAG = ImagePostFragment.class.getSimpleName();
    public static final String EXTRA_ANIMATE_NEW_BACKGROUND = "extra_animate_new_background";

    @InjectView(R.id.post)
    PanningBackgroundFrameLayout postView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_post,container,false);
        ButterKnife.inject(this, view);
        Utils.addSystemUIPadding(getActivity(), this.postView);
        content.setText(post.text);
        commentCounter.setText("" + post.commentCount);
        heartCounter.setText("" + post.heartCount);
        postView.setShouldAnimateBackgroundChange(true);
        Picasso.with(getActivity()).load(post.image).noFade().into(postView);
        if(post.hasHearted){
            heartImage.setImageResource(R.drawable.ic_heart_filled);
        }else{
            heartImage.setImageResource(R.drawable.ic_heart_empty);
        }
        if(showAuthor){
            Picasso.with(getActivity()).load(author.avatar).into(avatar);
        }
        return view;
    }

    @Override
    public boolean onBackPressed() {
        Log.w(TAG,"--- onBackPressed invoked ---");
        if (postView.isZoomedOut())
        {
            postView.toggleZoomedOut();
            return true;
        }
        return super.onBackPressed();
    }
    @Override
    public void onPause() {
        super.onPause();
        postView.setPanningEnabled(false);
        postView.setClickToZoomEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        postView.setPanningEnabled(true);
        postView.setClickToZoomEnabled(true);
    }
}
