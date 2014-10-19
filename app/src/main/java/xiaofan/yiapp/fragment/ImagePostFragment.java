package xiaofan.yiapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        this.day.setText("IMAGE");
        this.date.setText("IMAGE");
        this.content.setText("IMAGE");
        this.commentCounter.setText("2");
        this.heartCounter.setText("22");
        Utils.addSystemUIPadding(getActivity(), this.postView);
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
