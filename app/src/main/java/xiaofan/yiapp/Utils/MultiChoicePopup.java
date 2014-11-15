package xiaofan.yiapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyu on 2014/11/15.
 */
public class MultiChoicePopup {
    private final MultiChoicePopupAdapter adapter;
    private final List<String> choices = new ArrayList();
    private final Context context;
    private final OnItemSelectedListener listener;
    private final ListPopupWindow popup;
    private final AdapterView.OnItemClickListener onChoiceItemClicked = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
            listener.onItemSelected(position);
            dismiss();
        }
    };
    public MultiChoicePopup(Context context, OnItemSelectedListener onItemSelectedListener)
    {
        this.context = context;
        this.listener = onItemSelectedListener;
        this.adapter = new MultiChoicePopupAdapter();
        this.popup = new ListPopupWindow(context);
        this.popup.setAdapter(adapter);
        this.popup.setOnItemClickListener(onChoiceItemClicked);
        this.popup.setModal(true);
        float f = context.getResources().getDisplayMetrics().density;
        this.popup.setHorizontalOffset((int)(80.0F * -f));
        this.popup.setContentWidth((int)(100.0F * f));
    }

    public void addChoice(String choice)
    {
        choices.add(choice);
        adapter.notifyDataSetChanged();
    }

    public void dismiss()
    {
        popup.dismiss();
    }

    public void show(View view)
    {
        popup.setAnchorView(view);
        popup.show();
    }

    private class MultiChoicePopupAdapter
            extends BaseAdapter
    {
        @Override
        public int getCount() {
            return choices.size();
        }

        @Override
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            textView.setText(choices.get(position));
            return textView;
        }
    }

    public static abstract interface OnItemSelectedListener
    {
        public abstract void onItemSelected(int paramInt);
    }

}
