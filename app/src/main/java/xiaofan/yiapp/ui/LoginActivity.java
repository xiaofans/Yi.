package xiaofan.yiapp.ui;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import xiaofan.yiapp.R;
import xiaofan.yiapp.utils.Utils;
import xiaofan.yiapp.base.BaseActivity;
import xiaofan.yiapp.view.PanningBackgroundFrameLayout;

/**
 * login
 */
public class LoginActivity extends BaseActivity {

    private PanningBackgroundFrameLayout panningBackgroundFrameLayout;
    private Button clickBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        setUpActionBar();
        setUpViews();
    }

    private void setUpActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void setUpViews() {
        panningBackgroundFrameLayout = (PanningBackgroundFrameLayout) findViewById(R.id.panning_bg);
        Utils.addSystemUIPadding(this,panningBackgroundFrameLayout);
        panningBackgroundFrameLayout.setPanningBackground(BitmapFactory.decodeResource(getResources(),R.drawable.register_bg));
        panningBackgroundFrameLayout.setPanningEnabled(true);

        clickBtn = (Button) findViewById(R.id.click);
        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panningBackgroundFrameLayout.pc();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
