package com.ahstu.mycar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.ahstu.mycar.R;

/**
 * @author 吴天洛
 *         功能：法律条例
 */
public class RegisterLawsActivity extends Activity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_laws);
        findViewById(R.id.register_laws_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl("file:/android_asset/index.html");
    }
}
