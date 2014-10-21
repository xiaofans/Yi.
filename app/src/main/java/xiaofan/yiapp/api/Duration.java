package xiaofan.yiapp.api;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class Duration {
    private long duration;

    public long millis(){
        return  1000L * duration;
    }
}
