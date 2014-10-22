package xiaofan.yiapp.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

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
    public static final String PARSE_APP_REST_API_KEY="CAdX8t6zV9OnfHrSqEWe3qx6NIN9Muuvj9Mfntex";
    private static Api instance;

    public static Api getInstance(){
        if (instance == null){
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            gsonBuilder.registerTypeAdapter(Date.class,new DateAdapter());
            RequestInterceptor interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                        request.addHeader("X-Parse-Application-Id",PARSE_APP_ID);
                        request.addHeader("X-Parse-REST-API-Key",PARSE_APP_REST_API_KEY);
                        request.addHeader("Content-Type","application/json");
                }
            };
           instance = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(API_BASE).setLogLevel(RestAdapter.LogLevel.NONE).setConverter(new GsonConverter(gsonBuilder.create())).build().create(Api.class);
        }
        return instance;
    }

    public static abstract interface Api{
        @FormUrlEncoded
        @POST("/functions/hello")
        public abstract void login(@Field("social_network") String network, @Field("social_token") String token, @Field("social_id") String id, Callback<User> callback);
    }
}
