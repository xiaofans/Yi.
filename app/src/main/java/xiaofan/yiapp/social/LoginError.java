package xiaofan.yiapp.social;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class LoginError {
    public String error;
    public boolean networkError;

    public LoginError(String error, boolean networkError) {
        this.error = error;
        this.networkError = networkError;
    }
}
