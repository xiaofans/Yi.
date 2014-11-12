package xiaofan.yiapp.api.entity;

/**
 * Created by zhaoyu on 2014/11/12.
 */
public class Timeline {
    public long meId;
    public long userId;

    public Timeline() {
    }
    public Timeline(long meId, long userId) {
        this.meId = meId;
        this.userId = userId;
    }
}
