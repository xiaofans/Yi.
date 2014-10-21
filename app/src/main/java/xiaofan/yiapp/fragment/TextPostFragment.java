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

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class TextPostFragment extends PostFragment{
    @InjectView(R.id.post)
    View postView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_post,container,false);
        ButterKnife.inject(this, view);
        Utils.addSystemUIPadding(getActivity(), this.postView);
        return view;
    }

}
