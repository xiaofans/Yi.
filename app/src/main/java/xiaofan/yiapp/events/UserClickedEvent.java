package xiaofan.yiapp.events;

import xiaofan.yiapp.api.User;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class UserClickedEvent {
    public User user;
    public UserClickedEvent(User user){
        this.user = user;
    }
}
