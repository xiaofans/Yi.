package xiaofan.yiapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import xiaofan.yiapp.utils.Log_YA;

/**
 * Created by zhaoyu on 2014/10/9.
 * 1.background panning
 * 2.add fackbook spring
 * 3.learn how to draw
 */
//--------------------------------------------------
/**
  private void performTraversals(){
    final View host = mView;
    host.measure... onMeasure()
    host.layout...  onLayout()
    host.draw....   onDraw
 }
  onMeasure 决定视图的大小
  onLayout 决定视图的位置
  onDraw   将视图呈现在画布上（
  Draw traversal performs several drawing steps which must be executed
  in the appropriate order:

  1. Draw the background
  2. If necessary, save the canvas' layers to prepare for fading
  3. Draw view's content
  4. Draw children
  5. If necessary, draw the fading edges and restore layers
  6. Draw decorations (scrollbars for instance)
 ）
 measure过程确定视图的大小 而layout过程确定视图的位置
 loyout是从view的layout方法开始的
 from
 1.http://blog.csdn.net/xyz_lmn/article/details/20385049
 2.http://developer.android.com/guide/topics/ui/how-android-draws.html
 3.http://www.2cto.com/kf/201312/267855.html
 （2 3 need to look）
 */
//--------------------------------------------------

/**
 ayalysis the code.
 setWillNotDraw(false);
 !implements Target.
 不用ImageView 因为还要加其他元素
 so good.
 */

public class PanningBackgroundFrameLayout extends FrameLayout{

   private static final String TAG = PanningBackgroundFrameLayout.class.getSimpleName();
   private boolean isPanningEnabled;
   private boolean canPan;
   private boolean isAnimatingBackground;
   private BitmapDrawable background;
   private int backgroundColor = -13421773;
   private int backgroundHeight;
   private int backgroundWidth;
   private double backgroundOffset;
   private double backgroundScale;
   private double minBackgroundOffset;
   private double minBackgroundScale;
   private double panPerSecond = 10.0F * getResources().getDisplayMetrics().density;
   private long lastPan;

    private Runnable updateOffset = new Runnable() {
        @Override
        public void run() {
            double d = panPerSecond *(System.currentTimeMillis() - lastPan) / 1000.0D;
            lastPan = System.currentTimeMillis();
            backgroundOffset = d;
            ViewCompat.postInvalidateOnAnimation(PanningBackgroundFrameLayout.this);
            postDelayed(this,16L);
        }
    };

    public PanningBackgroundFrameLayout(Context context) {
        this(context,null);
    }

    public PanningBackgroundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log_YA.w(TAG,"-- onMeasure --");
        measureBackground();
        setPanningEnabled(isPanningEnabled);
    }

    private void measureBackground() {
        if(background == null) return;
        this.backgroundHeight = background.getBitmap().getHeight();
        this.backgroundWidth =  background.getBitmap().getWidth();
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = 1920;
        if(measuredWidth == 0 || measuredHeight == 0) return;
        Log_YA.w(TAG,"backgroundHeight:" + backgroundHeight +" ,backgroundWidth:" + backgroundWidth +" , measured height:" + getMeasuredHeight() +" , measured width:" + getMeasuredWidth());
        backgroundScale = (double)measuredHeight / (double)backgroundHeight;
        backgroundWidth = (int)(backgroundWidth * backgroundScale);
        backgroundHeight = (int)(backgroundHeight * backgroundScale);

        backgroundOffset = ((getMeasuredHeight() - this.backgroundHeight) / 2);
        backgroundScale = 1.0D;

        minBackgroundScale = (getMeasuredWidth() / this.backgroundWidth);
        minBackgroundOffset = (getMeasuredWidth() - this.backgroundWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log_YA.w(TAG,"-- onLayout --");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log_YA.w(TAG,"-- onDraw --");
        if(background == null) return;
        canvas.drawColor(backgroundColor);
        Log_YA.w(TAG,"scale is:" + backgroundScale +" scale width is:" + backgroundScale * backgroundWidth + " scale height is" + backgroundScale * backgroundHeight);
        background.setBounds(0,0,backgroundWidth, backgroundHeight);
        background.draw(canvas);
    }


    public void setPanningEnabled(boolean isPanningEnabled) {
        this.isPanningEnabled = isPanningEnabled;
        removeCallbacks(updateOffset);
        if(isPanningEnabled){
            lastPan = System.currentTimeMillis();
            postDelayed(updateOffset,16L);
        }
    }

    public void setPanningBackground(Bitmap bitmap) {
       if(bitmap == null){
           this.background = null;
           return;
       }
        this.background = new BitmapDrawable(getResources(),bitmap);
        measureBackground();
        setPanningEnabled(isPanningEnabled);
    }

    public void pc() {
        Log_YA.w(TAG,"perfrom to redraw...");
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
