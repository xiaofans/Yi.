package xiaofan.yiapp.social;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public abstract interface LoginCallback {
    public abstract void failure(LoginError loginError);
    public abstract void success(SocialAuth  socialAuth);
}
