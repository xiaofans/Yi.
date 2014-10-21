package xiaofan.yiapp.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class Comments implements Iterable<Comment> {

    public List<Comment> comments = new ArrayList<Comment>();

    public int size()
    {
        return this.comments.size();
    }

    @Override
    public Iterator<Comment> iterator() {
        return comments.iterator();
    }
}
