package xiaofan.yiapp.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import xiaofan.yiapp.api.Post;

/**
 * Created by zhaoyu on 2014/11/13.
 */
public class HeartToggle implements Parcelable {

    public long postId;
    public long authorId;
    public boolean hasHearted;

    public static final Creator<HeartToggle> CREATOR = new Creator<HeartToggle>() {
        @Override
        public HeartToggle createFromParcel(Parcel parcel) {
            return new HeartToggle(parcel);
        }

        @Override
        public HeartToggle[] newArray(int i) {
            return new HeartToggle[i];
        }
    };

    public HeartToggle(){}
    public HeartToggle(Parcel parcel){
        this.authorId = parcel.readLong();
        this.postId = parcel.readLong();
        this.hasHearted = parcel.readInt() == 1 ? true : false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(authorId);
        parcel.writeLong(postId);
        parcel.writeInt(hasHearted ? 1 : 0);
    }
}
