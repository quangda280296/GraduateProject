package vn.quang.graduateproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.utils.Check;

public class MainFragment_1 extends Fragment {

    private View view;

    Runnable wait = new Runnable() {
        @Override
        public void run() {
            if (Check.checkInternetConnection(getContext())) {
                load();
                view.findViewById(R.id.layout_no_internet).setVisibility(View.GONE);
            }
            view.findViewById(R.id.layout_progress).setVisibility(View.GONE);
            view.findViewById(R.id.layout_display).setVisibility(View.VISIBLE);
        }
    };

    // no Internet
    public void tryAgain() {
        view.findViewById(R.id.layout_progress).setVisibility(View.VISIBLE);
        view.findViewById(R.id.layout_display).setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(wait, 1000);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        tryAgain();

        if (Check.checkInternetConnection(getContext())) {
            load();
        } else {
            view.findViewById(R.id.txt_tryAgain).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryAgain();
                }
            });
        }
    }

    public void load() {
        WebView webView = view.findViewById(R.id.webview);
        webView.loadUrl("http://kenh14.vn/doi-song/du-lich.chn");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
    }
}
