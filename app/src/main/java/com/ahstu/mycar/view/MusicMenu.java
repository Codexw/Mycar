package com.ahstu.mycar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.MainActivity;
import com.ahstu.mycar.activity.MyApplication;
import com.ahstu.mycar.music.MusicPlayService;

import java.util.ArrayList;

public class MusicMenu extends ViewGroup {

    private static final int PADDING_L_R = 1;
    private static final int PADDING_T_B = 1;
    private static int DURATION = 500;//动画持续时间
    private final int SMALL_RADIUS;

    private boolean isAnimating;
    private int currentIndex;
    private int startX, startY;
    private boolean isShown;

    private ImageView childOne;
    private ImageView childTwo;
    private ImageView childThree;
    private ImageView childFour;

    private MusicPlayService mService;
    private MyApplication application;
    private MainActivity mainActivity;

    //强制转换成ImageView
    private ArrayList<ImageView> views = new ArrayList<>();
    private MusicMenuListener musicMenuListener;
    private boolean is_play;

    public MusicMenu(Context context) {
        super(context);
        SMALL_RADIUS = (int) context.getResources().getDimension(R.dimen.radius);
        init(context);
    }

    public MusicMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        SMALL_RADIUS = (int) context.getResources().getDimension(R.dimen.radius);

        init(context);
    }

    public MusicMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        SMALL_RADIUS = (int) context.getResources().getDimension(R.dimen.radius);

        init(context);
    }

    public void setIs_play(boolean is_play) {
        this.is_play = is_play;
    }

    public void setOnMusicMenuListener(MusicMenuListener listener) {
        musicMenuListener = listener;
    }

    private void init(final Context context) {
        childOne = new ImageView(context);
        childTwo = new ImageView(context);
        childThree = new ImageView(context);
        childFour = new ImageView(context);
        application = new MyApplication();
        mService = new MusicPlayService();

        childOne.setImageResource(R.drawable.previous_bt);
        childTwo.setImageResource(R.drawable.pause_bt);
        childThree.setImageResource(R.drawable.next_bt);
        childFour.setImageResource(R.drawable.songlist_bt);

        childOne.setTag("previous");
        childTwo.setTag("pause");
        childThree.setTag("next");
        childFour.setTag("list");

        addView(childOne);
        addView(childTwo);
        addView(childThree);
        addView(childFour);

        views.add(childOne);
        views.add(childTwo);
        views.add(childThree);
        views.add(childFour);

        childOne.setVisibility(View.INVISIBLE);
        childTwo.setVisibility(View.INVISIBLE);
        childThree.setVisibility(View.INVISIBLE);
        childFour.setVisibility(View.INVISIBLE);


//        childOne.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });

        childOne.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                musicMenuListener.dealMusicclick(childOne);
                in();
            }
        });
        childTwo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                musicMenuListener.dealMusicclick(childTwo);
                in();
            }
        });

        childThree.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                musicMenuListener.dealMusicclick(childThree);
                in();
            }
        });
        childFour.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                musicMenuListener.dealMusicclick(childFour);
                in();
            }
        });

    }

    public void out() {

        if (isAnimating)
            return;

        isAnimating = true;

        if (is_play) {
            childTwo.setImageResource(R.drawable.pause_bt);
        } else {
            childTwo.setImageResource(R.drawable.play_bt);
        }
        int length = views.size();
        for (int i = 0; i < length; i++) {
            final View v = views.get(i);
            //控件对象的位置的动画
            TranslateAnimation animation = new TranslateAnimation(startX - v.getLeft(), 0, startY - v.getTop(), 0);//动画位置，相对父布局

            //继承android.view.animation.Animation方法
            animation.setInterpolator(new OvershootInterpolator(1.5f));//设置此动画的加速曲线
            animation.setDuration(DURATION);//动画应该持续多久

            v.startAnimation(animation);//开始指定动画
            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                //通知动画的结束。这个回调不调用动画重复计数设置为无限
                @Override
                public void onAnimationEnd(Animation animation) {
                    currentIndex++;
                    v.setVisibility(View.VISIBLE);
                    if (currentIndex == views.size()) {
                        isAnimating = false;
                        currentIndex = 0;
                        isShown = true;
                    }
                }
            });
        }

    }

    public void in() {

        if (isAnimating) {
            return;
        }

        isAnimating = true;

        int length = views.size();
        for (int i = 0; i < length; i++) {
            final View v = views.get(i);
            TranslateAnimation animation = new TranslateAnimation(0, startX - v.getLeft() - v.getWidth() / 2, 0, startY - v.getTop() + v.getHeight() / 2);
            animation.setInterpolator(new OvershootInterpolator(1.2f));
            if (i >= length / 2) {
                animation.setDuration(DURATION - 100);
            } else {
                animation.setDuration(DURATION);
            }
            v.startAnimation(animation);
            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentIndex++;
                    v.setVisibility(View.INVISIBLE);
                    if (currentIndex == views.size()) {
                        isAnimating = false;
                        currentIndex = 0;
                        isShown = false;
                    }
                }
            });
        }
    }

    public boolean isShown() {
        return isShown;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();//组件数量
        if (childCount == 0)
            return;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null) {
                child.measure(
                        MeasureSpec.makeMeasureSpec(SMALL_RADIUS,
                                MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(SMALL_RADIUS,
                                MeasureSpec.EXACTLY));//测量规范尺寸，模式
            }

        }


        int screenWidth = getResources().getDisplayMetrics().widthPixels;//获取屏幕宽度

        //计算控件的高度h
        int r = getChildAt(0).getMeasuredWidth();
        int R = screenWidth / 2 - r;
        int h = R + r * 2 + PADDING_T_B * 2;
        setMeasuredDimension(screenWidth, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount == 0 || getChildAt(0) == null)
            return;
        int width = getMeasuredWidth() - PADDING_L_R * 2;
        int height = getMeasuredHeight() - PADDING_T_B * 2;

        View childView = getChildAt(0);
        //大半圆半径
        int radiusBig = (width - childView.getMeasuredWidth()) / 3;
        //小圆半径
        int radiusSmall = childView.getMeasuredWidth() / 2;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null)
                break;

            if (i == 0) {
                int child_x = (int) (width / 2 - radiusBig * Math.cos(Math.PI / 3)) + PADDING_L_R - 40;
                int child_y = (int) (height - radiusSmall - radiusBig * Math.sin(Math.PI / 3)) + 130;

                layout(child, child_x, child_y, radiusSmall);
            } else if (i == 1) {
                int child_x = width / 2 + PADDING_L_R;
                int child_y = height - radiusSmall - radiusBig + 40;

                layout(child, child_x, child_y, radiusSmall);
            } else if (i == 2) {

                int child_x = (int) (width / 2 + radiusBig * Math.cos(Math.PI / 3)) + PADDING_L_R + 40;
                int child_y = (int) (height - radiusSmall - radiusBig * Math.sin(Math.PI / 3)) + 130;

                layout(child, child_x, child_y, radiusSmall);

            } else if (i == 3) {
                int child_x = width / 2 + PADDING_L_R;
                int child_y = (height - radiusSmall - radiusBig) * 3 / 2 + 60;

                layout(child, child_x, child_y, radiusSmall);
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startX = getWidth() / 2;
        startY = getHeight() - childOne.getWidth() / 2 - PADDING_T_B;
    }

    private void layout(View view, int child_x, int child_y, int radiusSmall) {
        int child_l = getChildOffset_l(child_x, child_y, radiusSmall);
        int child_t = getChildOffset_t(child_x, child_y, radiusSmall);
        int child_r = getChildOffset_r(child_x, child_y, radiusSmall);
        int child_b = getChildOffset_b(child_x, child_y, radiusSmall);
        view.layout(child_l, child_t, child_r, child_b);
    }

    private int getChildOffset_l(int child_x, int child_y, int radius) {
        return child_x - radius;
    }

    private int getChildOffset_t(int child_x, int child_y, int radius) {
        return child_y - radius;
    }

    private int getChildOffset_r(int child_x, int child_y, int radius) {
        return child_x + radius;
    }

    private int getChildOffset_b(int child_x, int child_y, int radius) {
        return child_y + radius;
    }

    public MyApplication getApplication() {
        return application;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);

    }

    public interface MusicMenuListener {
        void dealMusicclick(View v);
    }
}
