package com.ahstu.mycar.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.ahstu.mycar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 徐伟 on 2016/5/19.
 * 功能：地图菜单管理
 */
public class MapMenuActivity extends AppCompatActivity {

    private int[] res = {R.id.btn_map_menu, R.id.btn_map_normal, R.id.btn_map_site, R.id.btn_map_mode_normal, R.id.btn_map_mode_compass};
    private List<Button> ButtonList = new ArrayList<Button>();
    private boolean flag = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

    }

    private void init() {
        for (int i = 0; i < res.length; i++) {
            Button button = (Button) findViewById(res[i]);
            ButtonList.add(button);
        }
    }

    public void mapMenuClick() {
        init();
        if (flag) {
            startAnim();
        } else {
            closeAnmi();
        }
    }

    //菜单的回收
    private void closeAnmi() {
        for (int i = 1; i < res.length; i++) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonList.get(i), "translationY", i * 100, 0F);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(ButtonList.get(i), "rotation", 0, 360F);
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(animator, animator2);
            animator.setDuration(300);
            animator.setStartDelay(i * 100);
            animator.start();
            animator.start();
            animator2.setDuration(300);
            animator2.setStartDelay(i * 100);
            animator2.start();
            flag = true;
        }
    }
    //菜单的弹出

    private void startAnim() {
        for (int i = 1; i < res.length; i++) {

            ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonList.get(i), "translationY", 0F, i * 100);
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(animator);
            animator.setDuration(300);
            animator.setStartDelay(i * 100);
            animator.start();
            flag = false;
        }
    }
}
