package xiaofan.yiapp.api;


import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import xiaofan.yiapp.api.entity.HeartToggle;
import xiaofan.yiapp.api.entity.Timeline;
import xiaofan.yiapp.api.entity.ToggleFollow;
import xiaofan.yiapp.api.entity.UniversalBean;
import xiaofan.yiapp.base.CreateInfo;
import xiaofan.yiapp.base.ParseBase;
import xiaofan.yiapp.service.FollowToggleService;

/**
 * Created by zhaoyu on 2014/10/22.
 */
public class ApiService {
    /**
     * appid g1QLpI6Z5egPEKEv3zZglnMcFXCuwltS5ECQ8f96
     * rest api key zjXx0IXAxkAYzEn4v6zojytzwyXK10VakvmdHfhk
     * client key yES2AA5ca0SyopB01S9z5rKKNSp2t0jUylZNM5Nq
     */
    public static final int API_VERSION = 1;
    public static final String API_BASE = "https://api.parse.com/"+API_VERSION+"/";
    public static final String PARSE_APP_ID="g1QLpI6Z5egPEKEv3zZglnMcFXCuwltS5ECQ8f96";
    public static final String PARSE_APP_REST_API_KEY="zjXx0IXAxkAYzEn4v6zojytzwyXK10VakvmdHfhk";
    public static final String PARSE_APP_MASTER_KEY = "PXl7qSGpLY4YNvxL0yKRqzcmcTGrCcDfrjZKkNxZ";
    private static Api instance;

    public static Api getInstance(){
        if (instance == null){
            GsonBuilder gsonBuilder = new GsonBuilder();
           // gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            gsonBuilder.registerTypeAdapter(Date.class,new DateAdapter());
            RequestInterceptor interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                        request.addHeader("X-Parse-Application-Id",PARSE_APP_ID);
                        request.addHeader("X-Parse-REST-API-Key",PARSE_APP_REST_API_KEY);
                        request.addHeader("X-Parse-Master-Key",PARSE_APP_MASTER_KEY);
                }
            };
           instance = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(API_BASE).setLog(new AndroidLog("ApiService")).setLogLevel(RestAdapter.LogLevel.FULL).setConverter(new GsonConverter(gsonBuilder.create())).build().create(Api.class);
        }
        return instance;
    }

    public static abstract interface Api{
     /*   @Headers({
                "X-Parse-Application-Id:"+PARSE_APP_ID,
                "Content-Type:application/json",
                "X-Parse-REST-API-Key:"+PARSE_APP_REST_API_KEY
        })*/
        @POST("/functions/signup")
        public abstract void login(@Body JSONObject jsonObject,Callback<ParseBase<User>> callback);

        @POST("/classes/Users")
        public abstract void signUpUser(@Body SignUpUser user,Callback<CreateInfo> callback);

        @PUT("/classes/Users/{objectId}")
        public abstract void updateUser(@Body User user,@Path("objectId") String objectId,Callback<CreateInfo> callback);

        @POST("/classes/Posts")
        public abstract void uploadPost(@Body Post post,Callback<CreateInfo> callback);

        @GET("/classes/Posts")
        public ParseBase<List<Post>>  getPosts(@Query("authorId") long authorId);

        // ---------------------------------------------------------------------
        @POST("/functions/getFollowings")
        public  ParseBase<ArrayList<User>> getFollowings(@Body User user);

        @POST("/functions/timeline")
        public ParseBase<ArrayList<Post>> getTimeline(@Body Timeline timeline);

        @POST("/functions/getFollowers")
        public ParseBase<ArrayList<User>>  getFollowers(@Body User user);

        @GET("/classes/Users")
        public abstract void getPopular(Callback<ParseBase<ArrayList<User>>> callback);

        @POST("/functions/setFollow")
        public abstract void setFollow(@Body ToggleFollow toggleFollow, Callback<ParseBase<Connection>> callback);

        @POST("/functions/setCancelFollow")
        public abstract void setCancelFollow(@Body Connection connection,  Callback<ParseBase<Boolean>> callback);

        @POST("/classes/Users")
        public abstract ParseBase<User> getUser(@Query("uid") long id);

        @POST("/functions/setHeart")
        public abstract void heartToggle(@Body HeartToggle heartToggle,Callback<HeartToggle> callback);

    }
}
