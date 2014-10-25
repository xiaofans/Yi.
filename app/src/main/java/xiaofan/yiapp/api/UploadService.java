package xiaofan.yiapp.api;

import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import xiaofan.yiapp.api.entity.UploadFile;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class UploadService {
    private static Api instance;
    public static String API_UPLOAD_BASE = "https://api.parse.com/1/files";
    public static Api getInstance(){
        if (instance == null){
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class,new DateAdapter());
            RequestInterceptor interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("X-Parse-Application-Id",ApiService.PARSE_APP_ID);
                    request.addHeader("X-Parse-REST-API-Key",ApiService.PARSE_APP_REST_API_KEY);
                    request.addHeader("Content-Type","image/jpeg");
                }
            };
            instance = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(API_UPLOAD_BASE).setLog(new AndroidLog("UploadService")).setLogLevel(RestAdapter.LogLevel.FULL).setConverter(new GsonConverter(gsonBuilder.create())).build().create(Api.class);
        }
        return instance;
    }

    public static abstract interface Api
    {
        @POST("/{file_name}")
        public abstract void uploadPostFile(@Path("file_name") String fileName,@Body TypedFile typedFile, Callback<UploadFile> callback);

    }
}
