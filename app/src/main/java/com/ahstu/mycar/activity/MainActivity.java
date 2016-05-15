package com.ahstu.mycar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.fragment.FindFragment;
import com.ahstu.mycar.fragment.FriendFragment;
import com.ahstu.mycar.fragment.HomeFragment;
import com.ahstu.mycar.fragment.MeInfoFragment;

/**
 * @author 吴天洛 2016,4,25
 */

public class MainActivity extends FragmentActivity implements OnClickListener {
    private long exitTime;  //用于双击回退键退出软件的时间间隔处理
    private TextView txtHome, txtSearch, txtFriend, txtMe;
    private ImageView imgAdd;
    private View currentButton; //获取view，用于底部导航栏状态的切换

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        //将我的车辆里面的车辆信息选择保存在本地share中。

//        SharedPreferences share=getSharedPreferences("text",MODE_PRIVATE);
//        if(share.getString("number","").equals("")) {
//            DatabaseHelper helper=new DatabaseHelper(MainActivity.this,"node.db",null,1);
//            SQLiteDatabase db=helper.getReadableDatabase();
//            Cursor cursor=db.query("carinfo",new String[]{"car_number"},null,null,null,null,null);
//           
//            if(cursor!=null)
//            {
//                if(cursor.moveToFirst())
//                {
//                    SharedPreferences.Editor editer = share.edit();
//                    editer.putInt("position", 0);
//                    editer.putString("number", cursor.getString(cursor.getColumnIndex("car_number")).toString());
//                    editer.commit();
//                    Log.e("TAG","SSSSSSSSSSSSSSSSSSSS"+cursor.getString(cursor.getColumnIndex("car_number")).toString());
//                }
//                
//            }
//            else {
//                SharedPreferences.Editor editer = share.edit();
//                editer.putInt("position", 0);
//                editer.putString("number", "");
//                editer.commit();
//            }
//        }
//
//        //Log.e("sss","wwwwwwwwwwwwwwwwwwwwwwww"+share.getString("number","111111111"));
//        
//        
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

    /**
     * 双击回退键，退出软件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出MyCar", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent();
                intent.setClass(this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
