package xiaofan.yiapp.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import xiaofan.yiapp.R;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class ColorPickerPopupWindow extends PopupWindow{

    private OnColorPickedListener listener;
    private final View.OnClickListener OnColorCircleClicked = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            ColorCircleView colorCircleView = (ColorCircleView) view;
            listener.colorPicked(colorCircleView.getColor());
            dismiss();
        }
    };

    public ColorPickerPopupWindow(Context context, OnColorPickedListener listener) {
        super(context);
        this.listener = listener;
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_color_picker, null);
        viewGroup.measure(View.MeasureSpec.makeMeasureSpec(-2,0),View.MeasureSpec.makeMeasureSpec(-2,0));
        setContentView(viewGroup);
        setWidth(viewGroup.getMeasuredWidth());
        setHeight(viewGroup.getMeasuredHeight());
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        for(int i = 0; i < viewGroup.getChildCount(); i++){
            viewGroup.getChildAt(i).setOnClickListener(this.OnColorCircleClicked);
        }

    }

    public static abstract interface OnColorPickedListener{
        public abstract void colorPicked(int color);
    }

    @Override
    public void showAsDropDown(View anchor) {
        float f = getContentView().getResources().getDisplayMetrics().density;
        super.showAsDropDown(anchor,-getWidth() / 2 + anchor.getWidth() / 2,(int)(10.0 * f));
    }
}
