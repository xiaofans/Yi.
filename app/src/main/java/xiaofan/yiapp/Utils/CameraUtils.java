package xiaofan.yiapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class CameraUtils {


    public static void ensurePhotoNotRotated(Context context, Uri uri) {
        try {
            ExifInterface exifInterface = new ExifInterface(uri.getPath());
            int ori = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            int imageDegree = 0;
            switch (ori){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageDegree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageDegree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageDegree = 270;
                    break;
            }

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Matrix matrix = new Matrix();
            matrix.postRotate(imageDegree);
            Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap1,0,0,bitmap1.getWidth(),bitmap1.getHeight(),matrix,true);
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap2.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            exifInterface.setAttribute("Orientation", String.valueOf(1));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
