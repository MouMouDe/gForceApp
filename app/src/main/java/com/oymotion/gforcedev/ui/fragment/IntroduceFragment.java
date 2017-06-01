package com.oymotion.gforcedev.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.oymotion.gforcedev.R;
import com.oymotion.gforcedev.utils.ToastUtil;

/**
 * Created by MouMou on 2017/5/17.
 */
public class IntroduceFragment extends BaseFragment {

    private WebView wv_introduce;
    private View view;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_introduce, null);
        initView(view,R.string.menu_introduce);
        return view;
    }

    @Override
    public void onResume() {
        wv_introduce = (WebView) view.findViewById(R.id.wv_introduce);
        wv_introduce.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        String url = "http://www.oymotion.com/";
        wv_introduce.loadUrl(url);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }

    public void destroyWebView() {
        if(wv_introduce != null) {
            wv_introduce.clearHistory();
            wv_introduce.clearCache(true);
            wv_introduce.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            wv_introduce.freeMemory();
            wv_introduce.pauseTimers();
            wv_introduce.removeAllViews();
            wv_introduce = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }
    }
}
