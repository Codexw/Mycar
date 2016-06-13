package com.ahstu.mycar.me;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author
 */
public class RoundProgressBar extends View {
    private RectF mColorWheelRectangle = new RectF();
    private Paint mDefaultWheelPaint;
    private Paint mColorWheelPaint;
    private Paint mColorWheelPaintCentre;
    private Paint mTextnum;//显示油量

    private float circleStrokeWidth;
    private float mSweepAnglePer;
    private int box_point, box_pointnow;
    private float pressExtraStrokeWidth;
    private BarAnimation anim;
    private int box_max = 100;// 默认最大

    public RoundProgressBar(Context context) {
        super(context);
        init(null, 0);
    }
    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public int getBox_point() {
        return box_point;
    }

    public void setBox_point(int box_point) {
        this.box_point = box_point;
    }

    private void init(AttributeSet attrs, int defStyle) {

        mColorWheelPaint = new Paint();
        mColorWheelPaint.setColor(Color.rgb(249, 135, 49));
        mColorWheelPaint.setStyle(Paint.Style.STROKE);// 空心
        mColorWheelPaint.setStrokeCap(Paint.Cap.ROUND);// 圆角画笔
        mColorWheelPaint.setAntiAlias(true);// 去锯齿

//		mColorWheelPaintCentre = new Paint();
//		mColorWheelPaintCentre.setColor(Color.rgb(250, 250, 250));
//		mColorWheelPaintCentre.setStyle(Paint.Style.STROKE);
//		mColorWheelPaintCentre.setStrokeCap(Paint.Cap.ROUND);
//		mColorWheelPaintCentre.setAntiAlias(true);

        mDefaultWheelPaint = new Paint();
        mDefaultWheelPaint.setColor(Color.rgb(127, 127, 127));
        mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
        mDefaultWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mDefaultWheelPaint.setAntiAlias(true);

        mTextnum = new Paint();
        mTextnum.setAntiAlias(true);
        mTextnum.setColor(Color.BLACK);

        anim = new BarAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mColorWheelRectangle, 0, 360, false, mDefaultWheelPaint);
//		canvas.drawArc(mColorWheelRectangle, 0, 359, false,
//				mColorWheelPaintCentre);
        String str = String.valueOf(box_pointnow) + "%";
        Rect rect = new Rect();
        mTextnum.getTextBounds(str, 0, str.length(), rect);
        canvas.drawArc(mColorWheelRectangle, 90, mSweepAnglePer, false,
                mColorWheelPaint);
        canvas.drawText(box_pointnow + "%", mColorWheelRectangle.centerX()
                        - rect.width() / 2,
                mColorWheelRectangle.centerX() + rect.height() / 2, mTextnum);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);// 获取View最短边的长度
        setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形
        circleStrokeWidth = Textscale(50, min);// 圆弧的宽度
        pressExtraStrokeWidth = Textscale(2, min);// 圆弧离矩形的距离
        mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth,
                circleStrokeWidth + pressExtraStrokeWidth, min
                        - circleStrokeWidth - pressExtraStrokeWidth, min
                        - circleStrokeWidth - pressExtraStrokeWidth);// 设置矩形
        mTextnum.setTextSize(Textscale(130, min));
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth);
        mDefaultWheelPaint
                .setStrokeWidth(circleStrokeWidth - Textscale(2, min));
        mDefaultWheelPaint.setShadowLayer(Textscale(10, min), 0, 0,
                Color.rgb(127, 127, 127));// 设置阴影
    }

    /**
     * 根据控件的大小改变绝对位置的比例
     *
     * @param n
     * @param m
     * @return
     */
    public float Textscale(float n, float m) {
        return n / 500 * m;
    }

    /**
     * 更新步数和设置一圈动画时间
     */
    public void update() {
//        box_point = 90;//传入油量百分比
        anim.setDuration(box_point * 15);//设置动画加载时间ms
        //setAnimationTime(time);
        this.startAnimation(anim);
    }

    // 设置进度条颜色 
    public void setColor(int red, int green, int blue) {
        mColorWheelPaint.setColor(Color.rgb(red, green, blue));
    }

    //设置动画时间
    public void setAnimationTime(int time) {
        anim.setDuration(time * box_point / box_max);// 按照比例设置动画执行时间
    }


    /**
     * 进度条动画
     *
     * @author Administrator
     */
    public class BarAnimation extends Animation {
        public BarAnimation() {

        }

        /**
         * 每次系统调用这个方法时， 改变mSweepAnglePer，mPercent，box_pointnow的值，
         * 然后调用postInvalidate()不停的绘制view。
         */
        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
//				mPercent = Float.parseFloat(fnum.format(interpolatedTime
//						* box_point * 100f / box_max));// 将浮点值四舍五入保留一位小数
                mSweepAnglePer = interpolatedTime * box_point * 360
                        / box_max;
                box_pointnow = (int) (interpolatedTime * box_point);
            } else {
//				mPercent = Float.parseFloat(fnum.format(box_point * 100f
//						/ box_max));// 将浮点值四舍五入保留一位小数
                mSweepAnglePer = box_point * 360 / box_max;
                box_pointnow = box_point;
            }
            postInvalidate();
        }
    }
}
