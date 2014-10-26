package xiaofan.yiapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import se.emilsjolander.sprinkles.OneQuery;
import xiaofan.yiapp.R;
import xiaofan.yiapp.api.DateAdapter;
import xiaofan.yiapp.api.Post;
import xiaofan.yiapp.api.PostTemplate;
import xiaofan.yiapp.api.User;
import xiaofan.yiapp.base.BaseFragment;
import xiaofan.yiapp.events.EventBus;
import xiaofan.yiapp.events.UserClickEvent;
import xiaofan.yiapp.service.UploadPostService;
import xiaofan.yiapp.utils.CameraUtils;
import xiaofan.yiapp.utils.QueryBuilder;
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
    public static Fragment newInstance() {
        UploadPostFragment uploadPostFragment = new UploadPostFragment();
        return uploadPostFragment;
    }

    @InjectView(R.id.avatar)
    AvatarCircleView avatar;
    @InjectView(R.id.camera)
    ImageButton cameraButton;
    @InjectView(R.id.gallery)
    ImageButton galleryButton;
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

    private ProgressDialog pd;

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

    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() > 0){
                upload.setVisibility(View.VISIBLE);
            }else{
                upload.setVisibility(View.GONE);
            }
        }
    };

    private OneQuery.ResultHandler<User> authenticatedUserResult = new OneQuery.ResultHandler<User>()
    {
        public boolean handleResult(User user)
        {
            if (user == null) {
                return false;
            }
            me = user;
            Picasso.with(UploadPostFragment.this.getActivity()).load(user.avatar).into(UploadPostFragment.this.avatar);
            return true;
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       root = inflater.inflate(R.layout.fragment_upload_post,container,false);
        ButterKnife.inject(this, this.root);
        Utils.addSystemUIPadding(getActivity(),root);
        postText.addTextChangedListener(textChangeListener);
        me = QueryBuilder.me().get();
        QueryBuilder.me().getAsync(getLoaderManager(),authenticatedUserResult,User.class);
        this.postText.addTextChangedListener(this.textChangeListener);
        this.pd = new ProgressDialog(getActivity());
        this.pd.setMessage(getString(R.string.upload_post));
        this.pd.setCancelable(false);
        if(savedInstanceState != null){
            postType = savedInstanceState.getString("postType");
            postColor = savedInstanceState.getInt("postColor");
            postImage = savedInstanceState.getParcelable("postImage");
            postImageUri = savedInstanceState.getParcelable("postImageUri");
            if(postImage != null || postText.getText().length() > 0){
                upload.setVisibility(View.VISIBLE);
                if(!Post.TYPE_TEXT.equals(postType)){
                    postBackground.setPanningEnabled(true);
                    postBackground.setBackgroundColor(-13421773);
                    postBackground.setPanningBackground(this.postImage);
                }else{
                    this.postBackground.setPanningEnabled(false);
                    this.postBackground.setPanningBackground(null);
                    this.postBackground.setBackgroundColor(this.postColor);
                }
            }
        }else{
            this.upload.setVisibility(View.GONE);
            this.postType = Post.TYPE_TEXT;
            this.postColor = Utils.randomHipsterColor(getActivity());
            this.postImage = null;
            this.postImageUri = null;
            this.postBackground.setPanningEnabled(false);
            this.postBackground.setPanningBackground(null);
            this.postBackground.setBackgroundColor(this.postColor);
        }
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if(intent.getAction() == "android.intent.action.SEND"){

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("postColor",postColor);
        outState.putString("postType",postType);
        outState.putParcelable("postImage",postImage);
        outState.putParcelable("postImageUri",postImageUri);
    }

    @OnClick(R.id.color)
    public void bgWithColor(View paramView)
    {
        new ColorPickerPopupWindow(getActivity(), this.onColorPickedListener).showAsDropDown(paramView);
    }

    @OnClick(R.id.avatar)
    public void onAvatarClick(){
        EventBus.post(new UserClickEvent(this.me));
    }

    @OnClick(R.id.camera)
    public void bgFromCamera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            File file = createImageFile();
            if(file == null){
                Toast.makeText(getActivity(), R.string.could_not_save_file, Toast.LENGTH_LONG).show();
                return;
            }
            postImageUri = Uri.fromFile(file);
            intent.putExtra("output",postImageUri);
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }else {
            Toast.makeText(getActivity(),R.string.could_not_find_camera,Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile(){
        String str = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            File file = File.createTempFile("yiapp_"+str+"_",".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            return file;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @OnClick(R.id.gallery)
    public void bgFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_IMAGE_CHOOSE);
        }else{
            Toast.makeText(getActivity(), R.string.could_not_find_gallery, Toast.LENGTH_LONG).show();
        }
    }
    @OnClick(R.id.upload)
    public void onUploadClick(){
        if(!Utils.isNetworkAvailable(getActivity())){
            Utils.showErrorDialog(getActivity(),R.string.no_internetwork_connection);
            return;
        }
        Utils.hideKeyboard(getActivity());
        root.animate().scaleX(0.8F).scaleY(0.8F).setInterpolator(new OvershootInterpolator(1.5F)).start();
        pd.show();
        PostTemplate postTemplate = new PostTemplate();
        postTemplate.type = postType;
        postTemplate.text = postText.getText().toString();
        postTemplate.image = postImageUri;
        postTemplate.color = Utils.colorToHex(postColor);
        getActivity().startService(UploadPostService.newIntent(getActivity(),postTemplate));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            boolean formCamera = requestCode == REQUEST_IMAGE_CAPTURE ? true : false;
            BitmapDecoderTask bitmapDecoderTask = new BitmapDecoderTask(formCamera);
            if(!formCamera){
                postImageUri = data.getData();
            }
            bitmapDecoderTask.execute(postImageUri);
        }
    }

    @Subscribe
    public void uploadSuccess(UploadPostService.SuccessEvent successEvent){
        pd.dismiss();
        root.animate().scaleX(1.0f).scaleY(1.0f).start();
        postType = Post.TYPE_TEXT;
        postColor = Utils.randomHipsterColor(getActivity());
        postImage = null;
        postImageUri = null;
        postBackground.setPanningEnabled(false);
        postBackground.setPanningBackground(null);
        postBackground.setBackgroundColor(postColor);
        postText.setText(null);
    }
    @Subscribe
    public void uploadFailure(UploadPostService.FailureEvent failureEvent){
        pd.dismiss();
        root.animate().scaleX(1.0f).scaleY(1.0f).start();
        Utils.showErrorDialog(getActivity(),R.string.upload_fialure_message);
    }

    class BitmapDecoderTask
            extends AsyncTask<Uri, Void, Bitmap> {

        boolean fromCamera;

        BitmapDecoderTask(boolean fromCamera) {
            this.fromCamera = fromCamera;
        }

        @Override
        protected Bitmap doInBackground(Uri... uris) {
            Uri uri = uris[0];
            if(fromCamera){
                CameraUtils.ensurePhotoNotRotated(getActivity(), uri);
            }

            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                return BitmapFactory.decodeStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null && getView() != null) {
                postType = Post.TYPE_IMAGE;
                postImage = bitmap;
                postBackground.setBackgroundColor(-13421773);
                postBackground.setPanningBackground(UploadPostFragment.this.postImage);
                postBackground.setPanningEnabled(true);
                upload.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getActivity(),R.string.could_not_open_image,Toast.LENGTH_LONG).show();
            }

        }
    }
}
