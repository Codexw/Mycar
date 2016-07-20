package com.ahstu.mycar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahstu.mycar.R;

/**
 * Created by xuning on 2016/7/16.
 */
public class ScanresultActivity extends Activity {
    TextView result;
    ImageView scan_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Bundle bundle = getIntent().getExtras();
        String s=bundle.getString("result");
        result=(TextView)findViewById(R.id.scan_result);
        scan_back=(ImageView)findViewById(R.id.scan_back);
        scan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        result.setText(s);
    }
    
}
