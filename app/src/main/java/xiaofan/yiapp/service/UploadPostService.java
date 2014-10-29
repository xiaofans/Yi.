package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.PostTemplate;
import xiaofan.yiapp.api.UploadService;
import xiaofan.yiapp.api.entity.UploadFile;
import xiaofan.yiapp.base.CreateInfo;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.LoginCallback;
import xiaofan.yiapp.social.LoginError;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.social.SocialAuth;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/10/25.
 */
public class UploadPostService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent newIntent(Context context, PostTemplate postTemplate) {
        Intent intent = new Intent(context,UploadPostService.class);
        intent.putExtra("post",postTemplate);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            stopSelf();
            return START_FLAG_REDELIVERY;
        }
        final  PostTemplate postTemplate = intent.getParcelableExtra("post");
        SocialApi.getCurrent(this).getSocialAuth(this,new LoginCallback() {
            @Override
            public void failure(LoginError loginError) {
                EventBus.post(new FailureEvent());
                if(!loginError.networkError){
                    EventBus.post(new LogoutEvent());
                }
                stopSelf();
            }

            @Override
            public void success(SocialAuth socialAuth) {
                PostUploadTask uploadTask = new PostUploadTask(socialAuth,postTemplate);
                uploadTask.execute(postTemplate);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
    private Post uploadPost(
                            final String id,
                            final String type,
                            final String text,
                            final String color,
                            File file)
    {
        final Post post = new Post();
        post.authorId = Long.parseLong(id);
        post.color = color;
        post.type = type;
        post.text = text;
        if(file == null){
            uploadPost___(post);
        }else{
            uploadImagePost(post,file);
        }


        return null;
    }

    private void uploadImagePost(final Post post,File file) {
        final TypedFile typedFile = new TypedFile("image/jpeg", file);
        UploadService.getInstance().uploadPostFile(file.getName(),typedFile,new Callback<UploadFile>() {

            @Override
            public void success(final UploadFile uploadFile, Response response) {
                if(uploadFile != null){
                    post.image = uploadFile.url;
                    post.imageFile = uploadFile;
                    post.imageFile.__type = "File";
                    uploadPost___(post);
                }else{
                    EventBus.post(new FailureEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.post(new FailureEvent());
                stopSelf();
            }
        });
    }

    private void uploadPost___(final Post post) {
        ApiService.getInstance().uploadPost(post,new Callback<CreateInfo>() {
            @Override
            public void success(CreateInfo createInfo, Response response) {
                if(createInfo != null){
                    post.objectId = createInfo.objectId;
                    post.id = post.objectId.hashCode();
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:ss:mm.SSS\'Z\'");
                    try {
                        post.createdAt = sf.parse(createInfo.createdAt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    post.save();
                    EventBus.post(new SuccessEvent(post));
                }else{
                    EventBus.post(new FailureEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w("UploadPostService","Upload post failure!");
                EventBus.post(new FailureEvent());
            }
        });
    }




    private class PostUploadTask
            extends AsyncTask<PostTemplate, Void, File>
    {
        private SocialAuth authInfo;
        private PostTemplate postTemplate;
        public PostUploadTask(SocialAuth socialAuth,PostTemplate postTemplate) {
            this.authInfo  = socialAuth;
            this.postTemplate = postTemplate;
        }

        @Override
        protected File doInBackground(PostTemplate... postTemplates) {
            PostTemplate postTemplate = postTemplates[0];
            boolean isImagePost = Post.TYPE_IMAGE.equals(postTemplate.type);
            File file = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if(isImagePost){
                try {
                    file = File.createTempFile("yi_upload_temp",".jpeg");
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(postTemplate.image),null,options);
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inSampleSize = options.outHeight / 1280;
                    options2.inPreferQualityOverSpeed = false;
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(postTemplate.image),null,options2);
                    if(bitmap.getHeight() > 1280){
                        bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth() * ((double)1280 / (double)bitmap.getHeight())), 1280, true);
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG,80,new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            uploadPost(authInfo.id,postTemplate.type,postTemplate.text,postTemplate.color,file);
        }
    }

    public static class FailureEvent {}
    public static class SuccessEvent{
        public Post post;

        public SuccessEvent(Post post) {
            this.post = post;
        }
    }
}
