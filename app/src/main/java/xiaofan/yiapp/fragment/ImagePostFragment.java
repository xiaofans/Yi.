package xiaofan.yiapp.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    public static final String EXTRA_ANIMATE_NEW_BACKGROUND = "extra_animate_new_background";
    @InjectView(R.id.post)
    PanningBackgroundFrameLayout postView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_post,container,false);
        ButterKnife.inject(this, view);
        Utils.addSystemUIPadding(getActivity(), this.postView);
        this.content.setText(this.post.text);
        this.commentCounter.setText("" + this.post.commentCount);
        this.heartCounter.setText("" + this.post.heartCount);
        this.postView.setShouldAnimateBackgroundChange(true);
        Picasso.with(getActivity()).load(this.post.image).noFade().into(this.postView);
        return view;
    }

    public boolean onBackPressed()
    {
        if (this.postView.isZoomedOut())
        {
            this.postView.toggleZoomedOut();
            return true;
        }
        return super.onBackPressed();
    }

    public void onPause()
    {
        super.onPause();
        this.postView.setPanningEnabled(false);
        this.postView.setClickToZoomEnabled(false);
    }

    public void onResume()
    {
        super.onResume();
        this.postView.setPanningEnabled(true);
        this.postView.setClickToZoomEnabled(true);
    }
}
