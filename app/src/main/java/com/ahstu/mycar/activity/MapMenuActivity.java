package com.ahstu.mycar.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ahstu.mycar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class MapMenuActivity extends AppCompatActivity implements View.OnClickListener{

    private int[] res ={R.id.btn_map_menu,R.id.btn_map_mode_compass,R.id.btn_map_mode_following,R.id.btn_map_mode_normal,R.id.btn_map_mylocation,
            R.id.btn_map_normal, R.id.btn_map_site,R.id.btn_map_traffic};
    private List<Button> ButtonList = new ArrayList<Button>();
    private boolean flag = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        for (int i =0;i<res.length;i++){
            Button button = (Button) findViewById(res[i]);
            button.setOnClickListener(this);
            ButtonList.add(button);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_map_menu:
            case R.id.btn_map_normal:
            case R.id.btn_map_site:
            case R.id.btn_map_traffic :
            case R.id.btn_map_mylocation:
            case R.id.btn_map_mode_normal:
            case R.id.btn_map_mode_following:
            case R.id.btn_map_traffic:
            default:
                break;
        }
        if (flag){
            startAnim();
        }else {
            closeAnmi();
        }
    }

    //菜单的回收
    private void closeAnmi() {
        for (int i=1;i<res.length;i++){
            ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonList.get(i),"translationY",i*100,0F);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(ButtonList.get(i),"rotation",0,360F);
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(animator,animator2);
            animator.setDuration(300);
            animator.setStartDelay(i*100);
            animator.start();
            animator.start();
            animator2.setDuration(300);
            animator2.setStartDelay(i*100);
            animator2.start();
            flag = true;
        }
    }
    //菜单的弹出

    private void startAnim() {
        for (int i=1;i<res.length;i++){

            ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonList.get(i),"translationY",0F,i*100);
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(animator);
            animator.setDuration(300);
            animator.setStartDelay(i*100);
            animator.start();
            flag = false;
        }
    }
}
