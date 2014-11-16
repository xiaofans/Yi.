package xiaofan.yiapp.social;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class SocialAuth {
    public String id;
    public String network;
    public String token;

    public String name;
    public String avatar;
    public SocialAuth(){}

    public SocialAuth(String id, String network, String token) {
        this.id = id;
        this.network = network;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getNetwork() {
        return network;
    }

    public String getToken() {
        return token;
    }


    @Override
    public String toString() {
        return "id:" + id +" network:" + network +"token:" + token;
    }
}
