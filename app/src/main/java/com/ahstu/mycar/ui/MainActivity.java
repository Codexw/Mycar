package com.ahstu.mycar.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redowu.mycar.R;

/**
 * @author 吴天洛 2016,4,25
 */

public class MainActivity extends FragmentActivity implements OnClickListener {
    private TextView txtHome, txtSearch, txtFriend, txtMe;
    private ImageView imgAdd;
    private View currentButton;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        initView();
        initOnclick();
    }

    /**
     * 初始化点击事件
     */
    private void initOnclick() {
        txtHome.setOnClickListener(this);
        txtSearch.setOnClickListener(this);
        txtFriend.setOnClickListener(this);
        txtMe.setOnClickListener(this);
        imgAdd.setOnClickListener(this);

        txtHome.performClick();//默认textHome被点击
    }

    /**
     * 初始化，获取id
     */
    private void initView() {
        txtHome = (TextView) findViewById(R.id.txtHome);
        txtSearch = (TextView) findViewById(R.id.txtSearch);
        txtFriend = (TextView) findViewById(R.id.txtFriend);
        txtMe = (TextView) findViewById(R.id.txtMe);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
    }

    /**
     * 处理点击事件，加载fragment
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtHome:
                mainTabUtil(new HomeFragment());
                setButton(v);
                break;
            case R.id.txtSearch:
                mainTabUtil(new FindFragment());
                setButton(v);
                break;
            case R.id.txtFriend:
                mainTabUtil(new FriendFragment());
                setButton(v);
                break;
            case R.id.txtMe:
                mainTabUtil(new MeInfoFragment());
                setButton(v);
                break;
            case R.id.imgAdd:
                Toast.makeText(MainActivity.this, "imgAdd哈哈哈哈哈", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 切换下方导航栏的状态
     */
    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }

    /**
     * 加载fragment
     */
    public void mainTabUtil(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, fragment).commit();
    }
}
