package xiaofan.yiapp.api;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import se.emilsjolander.sprinkles.Model;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class PostTemplate extends Model implements Parcelable{

    public String color;
    public Uri image;
    public String type;
    public String text;

    public static final Creator<PostTemplate> CREATOR = new Creator<PostTemplate>(){

        @Override
        public PostTemplate createFromParcel(Parcel parcel) {
            return new PostTemplate(parcel);
        }

        @Override
        public PostTemplate[] newArray(int i) {
            return new PostTemplate[i];
        }
    };

    public PostTemplate(){}
    private PostTemplate(Parcel parcel){
        this.type = parcel.readString();
        this.color = parcel.readString();
        this.text = parcel.readString();
        this.image = parcel.readParcelable(Bitmap.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(color);
        parcel.writeString(text);
        parcel.writeParcelable(image,i);
    }
}
