package xiaofan.yiapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import xiaofan.yiapp.R;
import xiaofan.yiapp.utils.Utils;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class TextPostFragment extends PostFragment{
    @InjectView(R.id.post)
    View postView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_post,container,false);
        ButterKnife.inject(this, view);
        Utils.addSystemUIPadding(getActivity(), postView);
        postView.setBackgroundColor(Color.parseColor(post.color));
        day.setText(parseDay());
        content.setText(post.text);
        commentCounter.setText("" + post.commentCount);
        heartCounter.setText("" + post.heartCount);
        if(post.hasHearted){
            heartImage.setImageResource(R.drawable.ic_heart_filled);
        }else{
            heartImage.setImageResource(R.drawable.ic_heart_empty);
        }
        if(showAuthor){
            Picasso.with(getActivity()).load(author.avatar).noFade().into(this.avatar);
        }
        return view;
    }

    public String parseDay(){
      String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'hh:ss:mm.SSS'Z'").format(post.createdAt);
      if(TextUtils.isEmpty(dateStr)){
          return "";
      }else{
        dateStr = dateStr.substring(0,dateStr.indexOf("T"));
        return dateStr;
      }
    }



}
