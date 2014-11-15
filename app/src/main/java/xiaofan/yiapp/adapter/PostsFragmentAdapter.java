package xiaofan.yiapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.fragment.PostFragment;
import xiaofan.yiapp.fragment.UploadPostFragment;

/**
 * Created by zhaoyu on 2014/10/19.
 */

public class PostsFragmentAdapter extends NewFragmentStatePagerAdapter {
   private List<Post> posts;
   public PostsFragmentAdapter(FragmentManager mFragmentManager) {
        super(mFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return UploadPostFragment.newInstance();
        }
          return PostFragment.newInstance(posts.get(position - 1));
    }

    @Override
    public int getCount() {
        if(posts == null) return 1;
        return 1 + posts.size();
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof UploadPostFragment){
            return 0;
        }
        System.out.println("getItemPosition:" + object);
        Post post = ((PostFragment)object).getPost();
        if ((this.posts == null) || (!this.posts.contains(post))) {
            return -2;
        }
        return 1 + this.posts.indexOf(post);
    }

    @Override
    public int getItemId(int position) {
       if(position == 0){
           return 0;
       }else{
           return (int)posts.get(position - 1).pid;
       }
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}
