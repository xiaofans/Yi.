package xiaofan.yiapp.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class AvatarCircleView extends View implements Target{

    private static String TAG = AvatarCircleView.class.getSimpleName();
    private Bitmap avater;
    private float density = getResources().getDisplayMetrics().density;
    private BitmapDrawable roundAvatar;

    public AvatarCircleView(Context context) {
        this(context,null);
    }

    public AvatarCircleView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public AvatarCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(this.avater != null){
            this.roundAvatar = makeRoundAndFit(avater);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.roundAvatar != null){
            this.roundAvatar.draw(canvas);
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        Log.w(TAG,"-- onBitmapLoaded --");
        setAvatar(bitmap);
    }

    public void setAvatar(Bitmap bitmap) {
        if(bitmap == null) return;
        this.avater = bitmap;
        this.roundAvatar = makeRoundAndFit(avater);
        if(this.roundAvatar != null){
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this.roundAvatar,"alpha",new int[]{0,255});
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(AvatarCircleView.this);
                }
            });
            objectAnimator.start();
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private BitmapDrawable makeRoundAndFit(Bitmap avater) {
        if(avater == null) return null;
        if(getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0) return null;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0,0,avater.getWidth(),avater.getHeight());
        Rect rect2 = new Rect(0,0,width,height);
        RectF rectF = new RectF(rect2);
        canvas.drawARGB(0,0,0,0);
        canvas.drawOval(rectF,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(avater,rect,rect2,paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bitmap);
        bitmapDrawable.setBounds(0,0,bitmap.getWidth(),bitmap.getHeight());
        return bitmapDrawable;
    }

    @Override
    public void onBitmapFailed(Drawable drawable) {
        roundAvatar = null;
        avater = null;
        invalidate();
    }

    @Override
    public void onPrepareLoad(Drawable drawable) {

    }



}
