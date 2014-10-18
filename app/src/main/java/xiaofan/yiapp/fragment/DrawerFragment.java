package xiaofan.yiapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaofan.yiapp.R;
import xiaofan.yiapp.base.BaseFragment;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class DrawerFragment extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View drawerView = inflater.inflate(R.layout.fragment_drawer,container,false);
        return drawerView;
    }
}
