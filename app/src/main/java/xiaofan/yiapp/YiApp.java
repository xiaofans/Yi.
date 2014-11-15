package xiaofan.yiapp;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.otto.Subscribe;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.LogoutEvent;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.utils.QueryBuilder;

/**
 * Created by zhaoyu on 2014/10/9.
 */
public class YiApp  extends Application{

    private static final String TAG = YiApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Sprinkles sprinkles = Sprinkles.init(this);
        sprinkles.addMigration(new Migration() {
            @Override
            protected void doMigration(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL("CREATE TABLE Users (id INTEGER PRIMARY KEY,me INTEGER,name TEXT,avatar TEXT,followers_count INTEGER,followings_count INTEGER)");
                sqLiteDatabase.execSQL("CREATE TABLE Posts (pid INTEGER,type TEXT,created_at INTEGER,text TEXT,image TEXT,color TEXT,share_link TEXT,object_id text,author_id INTEGER,PRIMARY KEY (pid, author_id),FOREIGN KEY (author_id) REFERENCES Users(id) ON DELETE CASCADE)");
                sqLiteDatabase.execSQL("CREATE TABLE Connections (follower_id INTEGER,following_id,object_id TEXT INTEGER,PRIMARY KEY (follower_id, following_id),FOREIGN KEY (follower_id) REFERENCES Users(id) ON DELETE CASCADE,FOREIGN KEY (following_id) REFERENCES Users(id) ON DELETE CASCADE)");
            }
        });
        sprinkles.addMigration(new Migration()
        {
            @Override
            protected void doMigration(SQLiteDatabase sqLiteDatabase)
            {
                sqLiteDatabase.execSQL("ALTER TABLE Posts ADD COLUMN heart_count INTEGER DEFAULT 0");
                sqLiteDatabase.execSQL("ALTER TABLE Posts ADD COLUMN has_hearted INTEGER DEFAULT 0");
            }
        });
        sprinkles.addMigration(new Migration()
        {
            @Override
            protected void doMigration(SQLiteDatabase sqLiteDatabase)
            {
                sqLiteDatabase.execSQL("ALTER TABLE Posts ADD COLUMN comment_count INTEGER DEFAULT 0");
            }
        });
        sprinkles.addMigration(new Migration()
        {
            @Override
            protected void doMigration(SQLiteDatabase sqLiteDatabase)
            {
                sqLiteDatabase.execSQL("CREATE INDEX me ON Users(me)");
            }
        });

        EventBus.register(this);
    }



    @Subscribe
    public void loggedOut(LogoutEvent logoutEvent){
        SocialApi currentSocialApi = SocialApi.getCurrent(this);
        if(currentSocialApi != null){
            currentSocialApi.logout(this);
           SocialApi.setCurrent(this,null);
        }

        User user = QueryBuilder.me().get();
        if(user != null){
            user.delete();
        }
    }

    public static String getLogName(){
        return TAG;
    }
}
