package xiaofan.yiapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.sprinkles.Model;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.api.entity.ToggleFollow;
import xiaofan.yiapp.api.entity.UniversalBean;
import xiaofan.yiapp.base.ParseBase;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/3.
 */
public class FollowToggleService extends Service{

    private static final String TAG = "FollowToggleService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static Intent newIntent(Context context, User user, boolean checked) {
        Intent intent = new Intent(context,FollowToggleService.class);
        intent.putExtra("user",user);
        intent.putExtra("follow",checked);
        return intent;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!Utils.isNetworkAvailable(this)){
            EventBus.post(new FailureEvent());
            return START_FLAG_REDELIVERY;
        }

        User me = QueryBuilder.me().get();
        User user = intent.getParcelableExtra("user");
        boolean follow = intent.getBooleanExtra("follow", false);
        Connection connection = QueryBuilder.connection(me,user).get();
        if(connection == null && follow){
            new Connection(me.id,user.id).save();
            user.followingsCount += 1;
            user.save();
            ApiService.getInstance().setFollow(new ToggleFollow(me.id,user.id,follow,user.objectId),new FollowToggleCallback(me,user,follow));
        }else if(connection != null && !follow){
            user.followingsCount -= 1;
            user.save();
            connection = QueryBuilder.connection(me,user).get();
            String objectId = connection.objectId;
            Log.w(TAG,"objectId is:" + objectId);
            ApiService.getInstance().setCancelFollow(new UniversalBean(objectId,user.objectId), new CancelFollowToggleCallback(me, user, false,objectId));
            connection.deleteAsync(new Model.OnDeletedCallback() {
                @Override
                public void onDeleted() {
                    Log.w(TAG,"connection deleted....");
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    class CancelFollowToggleCallback implements Callback<ParseBase<Boolean>> {
        private User me;
        private User user;
        private boolean follow;
        private String objectId;
        public CancelFollowToggleCallback(User me,User toggleUser,boolean follow,String objectId){
            this.me = me;
            this.user = toggleUser;
            this.follow = follow;
            this.objectId = objectId;
        }
        @Override
        public void success(ParseBase<Boolean> booleanParseBase, Response response) {
            if(booleanParseBase != null){
                if(!booleanParseBase.result){
                    fail(me, user, follow, false,objectId);
                }
            }
        }

        @Override
        public void failure(RetrofitError error) {
            boolean notAuth = false;
            if(error.getResponse() != null && error.getResponse().getStatus() == 401){
                notAuth = true;
            }
            fail(me, user, follow, notAuth,objectId);
        }
    }
    class FollowToggleCallback implements Callback<ParseBase<Connection>> {

        private User me;
        private User user;
        private boolean follow;
        public FollowToggleCallback(User me,User toggleUser,boolean follow){
            this.me = me;
            this.user = toggleUser;
            this.follow = follow;
        }

        @Override
        public void success(ParseBase<Connection> connectionParseBase, Response response) {
            if(connectionParseBase.result != null){
                if(connectionParseBase.result.save()){
                    Log.w(TAG,"connection save success!");
                }else{
                    Log.w(TAG,"connection save error!");
                }
            }

            startService(TimelineSyncService.newIntent(FollowToggleService.this));
            EventBus.post(new SuccessEvent());
            stopSelf();
        }

        @Override
        public void failure(RetrofitError error) {
            boolean notAuth = false;
            if(error.getResponse() != null && error.getResponse().getStatus() == 401){
                notAuth = true;
            }
            fail(me, user, follow, notAuth,null);
        }
    }

    private void fail(User me,User toggleUser,boolean follow,boolean notAuth,String objectId){
            if(follow){
                new Connection(me.id,toggleUser.id).delete();
                me.followingsCount -= 1;
                me.save();
            }else {
                new Connection(me.id,toggleUser.id,objectId).save();
                me.followingsCount += 1;
                me.save();
            }
        if(notAuth){
            EventBus.post(new LogoutEvent());
        }
        EventBus.post(new FailureEvent());
    }

    public static class FailureEvent {}
    public static class SuccessEvent {}
}
