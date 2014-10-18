package xiaofan.yiapp.base;

import android.content.Intent;
import android.support.v4.app.Fragment;

import butterknife.ButterKnife;
import xiaofan.yiapp.events.EventBus;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public abstract class BaseFragment extends Fragment{


    @Override
    public void onStart() {
        super.onStart();
        EventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public boolean onBackPressed() {
        return false;
    }

    // # getChildFragmentManager #
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
