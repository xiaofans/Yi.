package xiaofan.yiapp.api;

import java.util.Date;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class Comment {
    public long cid;
    public long postId;
    public String text;
    public long authorId;

    public User author;

    public Date createdAt;
}
