package xiaofan.yiapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import static xiaofan.yiapp.R.styleable.*;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class ColorCircleView extends View{

    private final Paint circlePaint = new Paint();
    private int color;
    private int radius;

    public ColorCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, ColorCircleView,0,0);
        try {
            this.color = typedArray.getColor(0,0);
            this.radius = (int)typedArray.getDimension(1,0.0F);
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setColor(this.color);
            circlePaint.setAntiAlias(true);
        }finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2,getHeight() / 2,this.radius,this.circlePaint);
    }

    public int getColor() {
        return color;
    }
}
