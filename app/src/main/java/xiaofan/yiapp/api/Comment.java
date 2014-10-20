package xiaofan.yiapp.api;

import java.util.Date;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class Comment {
    public User author;
    public Date createdAt;
    public long id;
    public long postId;
    public String text;
}
