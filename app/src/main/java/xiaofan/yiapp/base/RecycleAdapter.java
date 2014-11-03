package xiaofan.yiapp.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by zhaoyu on 2014/11/3.
 */
public abstract class RecycleAdapter extends BaseAdapter{
    protected LayoutInflater inflater;

    public RecycleAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if(convertView == null){
            convertView = createView(position,parent);
        }
        prepareView(convertView,type,position);
        return convertView;
    }

    protected abstract View createView(int position,ViewGroup parent);

    protected abstract void prepareView(View view,int type,int position);
}
