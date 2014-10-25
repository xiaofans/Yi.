package xiaofan.yiapp.events;

import xiaofan.yiapp.api.User;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class UserClickEvent {
    public User user;
    public UserClickEvent(User user){
        this.user = user;
    }
}
