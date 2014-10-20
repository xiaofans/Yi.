package xiaofan.yiapp.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.BaseFragment;
import xiaofan.yiapp.utils.Utils;
import xiaofan.yiapp.view.AvatarCircleView;
import xiaofan.yiapp.view.ColorPickerPopupWindow;
import xiaofan.yiapp.view.PanningBackgroundFrameLayout;

/**
 * Created by zhaoyu on 2014/10/19.
 */
public class UploadPostFragment extends BaseFragment{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CHOOSE = 2;

    @InjectView(R.id.avatar)
    AvatarCircleView avatar;
    @InjectView(R.id.camera)
    ImageButton cameraButton;
    @InjectView(R.id.upload)
    ImageButton upload;
    @InjectView(R.id.post_background)
    PanningBackgroundFrameLayout postBackground;
    @InjectView(R.id.text)
    EditText postText;
    private int postColor;
    private Bitmap postImage;
    private Uri postImageUri;

    private String postType;
    private View root;

    private User me;
    private ColorPickerPopupWindow.OnColorPickedListener onColorPickedListener = new ColorPickerPopupWindow.OnColorPickedListener() {
        @Override
        public void colorPicked(int color) {
            postType = Post.TYPE_TEXT;
            postColor = color;
            postImageUri = null;
            postBackground.setPanningEnabled(false);
            postBackground.setPanningBackground(null);
            postBackground.setBackgroundColor(color);
            if(postText.length() > 0){
                upload.setVisibility(View.VISIBLE);
            }

        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       root = inflater.inflate(R.layout.fragment_upload_post,container,false);
        ButterKnife.inject(this, this.root);
        Utils.addSystemUIPadding(getActivity(),root);
        return root;
    }

    public static Fragment newInstance() {
       UploadPostFragment uploadPostFragment = new UploadPostFragment();
        return uploadPostFragment;
    }

    @OnClick(R.id.color)
    public void bgWithColor(View paramView)
    {
        new ColorPickerPopupWindow(getActivity(), this.onColorPickedListener).showAsDropDown(paramView);
    }
}
