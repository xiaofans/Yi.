package xiaofan.yiapp.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * Created by zhaoyu on 2014/10/19.
 */
@Table("Posts")
public class Post extends Model implements Parcelable{

    public static final String TYPE_IMAGE = "ImagePost";
    public static final String TYPE_TEXT = "TextPost";

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }

        @Override
        public Post[] newArray(int i) {
            return new Post[i];
        }
    };

    @Column("author_id")
    @Key
    public long authorId;
    @Column("color")
    public String color;
    @Column("comment_count")
    public int commentCount;
    @Column("created_at")
    public Date createdAt;
    public boolean deleted;
    @Column("has_hearted")
    public boolean hasHearted;
    @Column("heart_count")
    public  int heartCount;
    @Column("id")
    @Key
    public  long id;
    @Column("image")
    public String image;
    @Column("share_link")
    public String shareLink;
    @Column("text")
    public String text;
    @Column("type")
    public String type;

    public Post(){}
    private Post(Parcel parcel){
        this.id = parcel.readLong();
        this.authorId = parcel.readLong();
        this.type = parcel.readString();
        this.createdAt = (Date) parcel.readSerializable();
        this.text = parcel.readString();
        this.image = parcel.readString();
        this.color = parcel.readString();
        this.shareLink = parcel.readString();
        this.heartCount = parcel.readInt();
        if(parcel.readInt() == 1){
            this.hasHearted = true;
        }else{
            this.hasHearted = false;
        }
        this.commentCount = parcel.readInt();
    }


    @Override
    public int hashCode() {
        return (id + "-" +authorId).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        boolean isInstance = o instanceof Post;
        if(!isInstance) return false;
        Post post = (Post) o;
        if(this.id == post.id && this.authorId == post.id){
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(authorId);
        parcel.writeString(type);
        parcel.writeSerializable(createdAt);
        parcel.writeString(text);
        parcel.writeString(image);
        parcel.writeString(color);
        parcel.writeString(shareLink);
        parcel.writeInt(heartCount);
        if(hasHearted){
            parcel.writeInt(1);
        }else{
            parcel.writeInt(0);
        }
        parcel.writeInt(commentCount);
    }
}
