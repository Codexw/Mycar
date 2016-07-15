package com.ahstu.mycar.widght;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author 吴天洛
 *         功能：加油站列表中的信息以网格形式呈现
 */
public class NoScrollListView extends ListView {

    public NoScrollListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
