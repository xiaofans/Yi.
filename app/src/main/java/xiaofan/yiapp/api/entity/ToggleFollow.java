package xiaofan.yiapp.api.entity;

/**
 * Created by zhaoyu on 2014/11/4.
 */
public class ToggleFollow {

    public long me;
    public long toggleId;
    public boolean follow;
    public String objectId;

    public ToggleFollow(long id, long toggleId, boolean follow, String objectId) {
        this.me = id;
        this.toggleId = toggleId;
        this.follow = follow;
        this.objectId = objectId;
    }
}
