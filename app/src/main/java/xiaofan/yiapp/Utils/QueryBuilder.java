package xiaofan.yiapp.utils;

import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.OneQuery;
import se.emilsjolander.sprinkles.Query;
import xiaofan.yiapp.api.Connection;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public final class QueryBuilder {

    public static OneQuery<Connection> connection(User user1, User user2)
    {
        Object[] projections = new Object[2];
        projections[0] = Long.valueOf(user1.id);
        projections[1] = Long.valueOf(user2.id);
        return Query.one(Connection.class, "select * from Connections where follower_id=? and following_id=?", projections);
    }

    public static ManyQuery<Connection> connections(User user, User user2)
    {
        if ((user != null) && (user2 == null))
        {
            Object[] arrayOfObject3 = new Object[1];
            arrayOfObject3[0] = Long.valueOf(user.id);
            return Query.many(Connection.class, "select * from Connections where follower_id=?", arrayOfObject3);
        }
        if ((user == null) && (user2 != null))
        {
            Object[] arrayOfObject2 = new Object[1];
            arrayOfObject2[0] = Long.valueOf(user2.id);
            return Query.many(Connection.class, "select * from Connections where following_id=?", arrayOfObject2);
        }
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = Long.valueOf(user.id);
        arrayOfObject1[1] = Long.valueOf(user2.id);
        return Query.many(Connection.class, "select * from Connections where follower_id=? and following_id=?", arrayOfObject1);
    }
    public static ManyQuery<User> followers(User paramUser)
    {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Long.valueOf(paramUser.id);
        return Query.many(User.class, "select * from Users inner join Connections on Users.id=Connections.follower_id where Connections.following_id=?", arrayOfObject);
    }

    public static ManyQuery<User> followings(User paramUser)
    {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Long.valueOf(paramUser.id);
        return Query.many(User.class, "select * from Users inner join Connections on Users.id=Connections.following_id where Connections.follower_id=?", arrayOfObject);
    }

    public static OneQuery<Post> post(User paramUser, long paramLong)
    {
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = Long.valueOf(paramUser.id);
        arrayOfObject[1] = Long.valueOf(paramLong);
        return Query.one(Post.class, "select * from Posts where author_id=? and id=? order by created_at desc", arrayOfObject);
    }

    public static ManyQuery<Post> posts(User paramUser)
    {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Long.valueOf(paramUser.id);
        return Query.many(Post.class, "select * from Posts where author_id=? order by created_at desc", arrayOfObject);
    }


    public static OneQuery<User> user(long paramLong)
    {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Long.valueOf(paramLong);
        return Query.one(User.class, "select * from Users where id=?", arrayOfObject);
    }

    public static OneQuery<User> me()
    {
        Object[] projections = new Object[1];
        projections[0] = Boolean.valueOf(true);
        return Query.one(User.class, "select * from Users where me=? limit 1", projections);
    }

    public static ManyQuery<Post> timeline(User user)
    {
        Object[] projections = new Object[2];
        projections[0] = Long.valueOf(user.id);
        projections[1] = Long.valueOf(user.id);
        return Query.many(Post.class, "select * from Posts where author_id in (select id from Users inner join Connections on Users.id=Connections.following_id where Connections.follower_id=?) or author_id=? order by created_at desc", projections);
    }
}
