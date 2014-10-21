package xiaofan.yiapp.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class Posts implements Iterable<Post>{
    public List<Post> posts = new ArrayList<Post>();
    @Override
    public Iterator<Post> iterator() {
        return posts.iterator();
    }

    public int size(){
        return posts.size();
    }

}
