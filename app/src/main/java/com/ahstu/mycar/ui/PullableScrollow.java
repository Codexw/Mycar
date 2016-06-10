package com.ahstu.mycar.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by xuning on 2016/6/6.
 */
public class PullableScrollow extends ScrollView implements Pullable {
    public PullableScrollow(Context context) {
        super(context);
    }

    public PullableScrollow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableScrollow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public Boolean canPullUp() {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            return true;
        else
            return false;
    }
}
