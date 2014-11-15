package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.CommentsAdapter;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Comment;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.api.entity.UniversalBean;
import xiaofan.yiapp.base.AuthenticatedActivity;
import xiaofan.yiapp.base.ParseBase;
import xiaofan.yiapp.social.LoginCallback;
import xiaofan.yiapp.social.LoginError;
import xiaofan.yiapp.social.SocialApi;
import xiaofan.yiapp.social.SocialAuth;
import xiaofan.yiapp.utils.QueryBuilder;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/11/14.
 */

public class PostCommentsActivity extends AuthenticatedActivity{

    public static Intent newIntent(Context context, Post post)
    {
        Intent intent = new Intent(context, PostCommentsActivity.class);
        intent.putExtra("post", post);
        return intent;
    }



    @InjectView(R.id.comment)
    EditText comment;
    @InjectView(R.id.submit_comment)
    ImageButton commentBtn;
    @InjectView(android.R.id.empty)
    TextView empty;
    @InjectView(R.id.list)
    ListView list;
    @InjectView(R.id.loading_spinner)
    ProgressBar loadingSpinner;
    @InjectView(R.id.posting_spinner)
    ProgressBar postingSpinner;

    private Post post;
    private CommentsAdapter adapter;

    private AdapterView.OnItemClickListener onCommentClicked = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
            Comment comment  = adapter.getItem(position);
            User author = comment.author;
            comment.author.id = comment.author.uid;
            startActivity(ProfileActivity.newIntent(PostCommentsActivity.this,author));
        }
    };
    private TextWatcher onCommentTextChanged = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            updateCommentButtonStatus();
        }
    };

    Callback<ParseBase<ArrayList<Comment>>> onCommentsLoaded = new Callback<ParseBase<ArrayList<Comment>>>() {
        @Override
        public void success(ParseBase<ArrayList<Comment>> result, Response response) {
            loadingSpinner.setVisibility(View.GONE);
            list.setEmptyView(empty);
           if(result != null && result.result != null){
               adapter.setComments(result.result);
               post.commentCount = result.result.size();
               post.save();
           }
        }

        @Override
        public void failure(RetrofitError error) {
           list.setEmptyView(empty);
           loadingSpinner.setVisibility(View.GONE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_post_comments);
        comment.addTextChangedListener(onCommentTextChanged);
        post = getIntent().getParcelableExtra("post");
        adapter = new CommentsAdapter(this);
        list.setAdapter(adapter);
        empty.setText(getString(R.string._no_commits_tip));
        empty.setVisibility(View.GONE);
        updateCommentButtonStatus();
        UniversalBean universalBean = new UniversalBean();
        universalBean.postId = post.pid;
        ApiService.getInstance().getComments(universalBean,onCommentsLoaded);
    }

    private void updateCommentButtonStatus() {
        ViewPropertyAnimator viewPropertyAnimator = commentBtn.animate().setDuration(300);
       if(comment.getText().length() > 0){
           viewPropertyAnimator.alpha(1f);
           commentBtn.setEnabled(true);
       }else {
           commentBtn.setEnabled(false);
           viewPropertyAnimator.alpha(0.2f);
       }
    }

    @OnClick(R.id.submit_comment)
    public void submitComment(){
        comment.setEnabled(false);
        postingSpinner.animate().setDuration(200L).alpha(1.0F);
        commentBtn.animate().setDuration(200L).alpha(0.0F);

        final Comment c = new Comment();
        c.postId = post.pid;
        c.cid = System.currentTimeMillis();
        c.text = comment.getText().toString();
        c.authorId = QueryBuilder.me().get().id;
        c.author = QueryBuilder.me().get();
        ApiService.getInstance().postComment(c,new Callback<ParseBase<Boolean>>() {
            @Override
            public void success(ParseBase<Boolean> booleanParseBase, Response response) {
                postingDone(true);
                adapter.addComment(c);
            }

            @Override
            public void failure(RetrofitError error) {
                postingDone(false);
            }
        });
    }


    private void postingDone(boolean isPost) {
        comment.setEnabled(true);
        postingSpinner.animate().setDuration(200L).alpha(0.0F);
        commentBtn.animate().setDuration(200L).alpha(1.0F);
        if(isPost){
            comment.setText("");
            post.commentCount += 1;
            post.save();
        }else{
            Utils.showErrorDialog(this,getString(R.string._post_comment_failure));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
