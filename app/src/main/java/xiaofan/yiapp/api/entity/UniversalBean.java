package xiaofan.yiapp.api.entity;

/**
 * Created by zhaoyu on 2014/11/4.
 */
public class UniversalBean {
    public String objectId;
    public String userId;

    public long postId;

    public UniversalBean(){}
    public UniversalBean(String objectId, String userId) {
        this.objectId = objectId;
        this.userId = userId;
    }

}
