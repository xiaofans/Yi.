package xiaofan.yiapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.InjectView;
import xiaofan.yiapp.R;
import xiaofan.yiapp.adapter.CommentsAdapter;
import xiaofan.yiapp.api.ApiService;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.base.AuthenticatedActivity;

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
       // ApiService.getInstance().getComments();
        updateCommentButtonStatus();
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
}
