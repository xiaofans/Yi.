package xiaofan.yiapp.api;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * Created by zhaoyu on 2014/10/21.
 */
@Table("connections")
public class Connection extends Model{
    @Column("follower_id")
    @Key
    public long followerId;

    @Column("following_id")
    @Key
    public long followingId;

    @Column("object_id")
    public String objectId;

    public Connection(){}

    public Connection(long followerId, long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Connection(long followerId, long followingId,String objectId) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.objectId = objectId;
    }
}
