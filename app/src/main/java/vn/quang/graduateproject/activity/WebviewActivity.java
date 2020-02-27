package vn.quang.graduateproject.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.roger.catloadinglibrary.CatLoadingView;

import vn.quang.graduateproject.R;

public class WebviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        CatLoadingView load = new CatLoadingView();
        load.show(getSupportFragmentManager(), "");

        String q = getIntent().getStringExtra("q");

        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("https://www.google.com/search?q=" + q);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                load.dismiss();
                super.onPageFinished(view, url);
            }
        });
    }
}
