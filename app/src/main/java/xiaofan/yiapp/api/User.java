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
    @Column("followings_count")
    public int followingsCount;
    @Column("id")
    @Key
    public long id;
    @Column("me")
    public boolean me;
    @Column("name")
    public String name;
    public boolean isRegisterOnServer;
    public String objectId;

    public static final Creator<User> CREATOR = new Creator<User>(){

        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };
    public User(){}
    private User(Parcel parcel){
        this.id = parcel.readLong();
        if(parcel.readInt() == 0){
            this.me = false;
        }else{
            this.me = true;
        }
        this.name = parcel.readString();
        this.avatar = parcel.readString();
        this.followersCount = parcel.readInt();
        this.followingsCount = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        if(this.me){parcel.writeInt(1);}else {parcel.writeInt(0);}
        parcel.writeString(name);
        parcel.writeString(avatar);
        parcel.writeInt(followersCount);
        parcel.writeInt(followingsCount);
    }

    @Override
    public int hashCode() {
        return (int)id;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        boolean isInstance = o instanceof User;
        if(!isInstance) return false;
        User user = (User) o;
        if(this.id == user.id && this.name.equals(user.name)){
            return true;
        }
        return false;
    }

    public boolean isValid(){
        return this.name != null  && this.avatar != null;
    }

}
