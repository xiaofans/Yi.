package xiaofan.yiapp.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import xiaofan.yiapp.events.EventBus;


/**
 * Created by zhaoyu on 2014/10/9.
 * add sth later.
 */
public abstract class BaseActivity extends FragmentActivity{

    /**
     FragmnetActivity extends Activity extends ContextThemeWrapper extends ContextWrapper(define attachBaseContext)
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onBackPressed() {
        if(!dispatchBackPressedToFragments()){
            super.onBackPressed();
        }
    }

    private boolean dispatchBackPressedToFragments() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        if(list == null) return false;
        Iterator<Fragment> iterator = list.iterator();
        while (iterator.hasNext()){
            Fragment fragment = iterator.next();
            if(fragment instanceof BaseFragment){
                return ((BaseFragment)fragment).onBackPressed();
            }

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.unregister(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.inject(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.inject(this);
    }
}
