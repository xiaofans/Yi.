package xiaofan.yiapp.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class Users implements Iterable<User> {
    public List<User> users = new ArrayList<User>();
    @Override
    public Iterator<User> iterator() {
        return users.iterator();
    }

    public int size(){
        return users.size();
    }
}
