package xiaofan.yiapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import xiaofan.yiapp.base.RecycleAdapter;

/**
 * Created by zhaoyu on 2014/11/14.
 */
public class CommentsAdapter extends RecycleAdapter{
    public CommentsAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        return null;
    }

    @Override
    protected void prepareView(View view, int type, int position) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
