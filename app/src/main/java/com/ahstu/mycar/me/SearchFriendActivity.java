package com.ahstu.mycar.me;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.User;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/6/5.
 */
public class SearchFriendActivity extends Activity implements FriendAdpterOnItemClick {

    private EditText friendName;
    private Button bt_searchFriend;
    private Button bt_shareLocation;
    private ListView friendList;
    private List<User> userList;
    private CarFriendAdapter carFriendAdapter;
    private String namestring;
    private BmobPushManager bmobPush;
    private BmobQuery<BmobInstallation> moblie_id;
    private ShareLocationMessage shareLocationMessage;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_download);

        // 初始化BmobSDK
        Bmob.initialize(this, "ccd46e34cec57d61dbcedaa08f722296");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();//消息推送
        // 启动推送服务
        BmobPush.startWork(this);

        friendName = (EditText) findViewById(R.id.music_search_name);
        friendName.setHint("请输入需要查找的车友姓名");
        bt_searchFriend = (Button) findViewById(R.id.bt_music_search);
        friendList = (ListView) findViewById(R.id.music_search_listview);
        bt_shareLocation = (Button) findViewById(R.id.bt_music_download);

        //获取当前用户
        sp = getSharedPreferences("User", MODE_PRIVATE);

        //推送查询
        bmobPush = new BmobPushManager(SearchFriendActivity.this);
        moblie_id = BmobInstallation.getQuery();//查询设备表

        bt_searchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namestring = friendName.getText().toString();
                BmobQuery<User> friendquery = new BmobQuery<User>();
                friendquery.addWhereContains("username", namestring);//对歌曲名字进行模糊查询
                friendquery.addWhereNotEqualTo("username", sp.getString("name", ""));
                friendquery.findObjects(SearchFriendActivity.this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if (list.size() >= 1) {
                            userList = list;
                            carFriendAdapter = new CarFriendAdapter(SearchFriendActivity.this, list);
                            carFriendAdapter.onListener(SearchFriendActivity.this);
                            friendList.setAdapter(carFriendAdapter);
                            Toast.makeText(SearchFriendActivity.this, "search success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SearchFriendActivity.this, "搜索的车友不存在", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void onAdpterClick(int postion) {
        //查询本机登录的用户名和手机id
        String name = sp.getString("name", "");
        String mobileid = BmobInstallation.getInstallationId(this);
        //进行消息推送
        if (userList.get(postion).getMyInstallation() != null && userList.get(postion).getMyInstallation().length() > 5) {
            shareLocationMessage.setUsername(userList.get(postion).getUsername());//获取请求对象的name
            moblie_id.addWhereEqualTo("installationId", userList.get(postion).getMyInstallation());
            bmobPush.setQuery(moblie_id);
            bmobPush.pushMessage(name + "车友发送位置共享请求" + mobileid);
        } else {
            Toast.makeText(this, "搜索车友未上线，发送位置共享请求失败！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
