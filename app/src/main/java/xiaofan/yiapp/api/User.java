package xiaofan.yiapp.api;


import android.os.Parcel;
import android.os.Parcelable;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * Created by zhaoyu on 2014/10/19.
 */
@Table("Users")
public class User extends Model implements Parcelable{

    @Column("avatar")
    public String avatar;
    @Column("followers_count")
    public int followersCount;
    @Column("id")
    @Key
    public long id;
    @Column("me")
    public boolean me;
    @Column("name")
    public String name;

    public User(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
