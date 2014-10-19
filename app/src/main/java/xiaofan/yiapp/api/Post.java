package xiaofan.yiapp.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class Post implements Parcelable{

    public static final String TYPE_IMAGE = "ImagePost";
    public static final String TYPE_TEXT = "TextPost";
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
