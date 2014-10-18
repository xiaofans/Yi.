package xiaofan.yiapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class AvatarCircleView extends View implements Target{

    public AvatarCircleView(Context context) {
        super(context);
    }

    public AvatarCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {

    }

    @Override
    public void onBitmapFailed(Drawable drawable) {

    }

    @Override
    public void onPrepareLoad(Drawable drawable) {

    }
}
