package xiaofan.yiapp.social;

/**
 * Created by zhaoyu on 2014/10/18.
 */
public class SocialAuth {
    private String id;
    private String network;
    private String token;

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
}
