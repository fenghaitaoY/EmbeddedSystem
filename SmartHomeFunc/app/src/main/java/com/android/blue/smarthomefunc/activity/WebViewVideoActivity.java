package com.android.blue.smarthomefunc.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.JsonCallback;
import com.android.blue.smarthomefunc.model.Splash;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WebViewVideoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webView_activity_loading_bar)
    ProgressBar webViewActivityLoadingBar;
    @BindView(R.id.webView)
    WebView webView;

    WebSettings mWebSettings;
    View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_video);
        ButterKnife.bind(this);

        String urlStr = getIntent().getStringExtra("url");

        mWebSettings = webView.getSettings();

        mWebSettings.setJavaScriptEnabled(true); //设置与js交互
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //允许js弹窗

        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);

        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);

        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setAllowFileAccess(true);

        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.loadUrl(urlStr);

        OkHttpUtils.get().url("http://www.iqiyi.com/v_19rrdkbu90/").build().execute(new JsonCallback<Splash>(Splash.class) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(Splash response, int id) {
            }

            @Override
            public void onAfter(int id) {
            }
        });

        //由于设置了弹窗检验调用结果, 所以需要支持js对话框, webview只是载体, 内容的渲染需要使用webviewchromeClient类去实现
        //通过设置webchromeclient对象处理js的对话框, 设置响应js的alert函数
        webView.setWebChromeClient(new WebChromeClient(){

            //监听alert回调
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return true;

            }

            //拦截输入框
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //根据协议的参数,判断是否是所需要的url
                //一般根据scheme & authority判断
                LogUtils.i("onJsPrompt start");
                Uri uri = Uri.parse(message);
                if (uri.getScheme().equals("js")){
                    if (uri.getAuthority().equals("webview")){
                        result.confirm("js 调用成功");
                    }
                    return true;
                }

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                LogUtils.i("webview title ="+title);
                toolbar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtils.i("progress change  , new progress = "+newProgress);
                if (webViewActivityLoadingBar != null) {
                    if (newProgress < 100) {
                        webViewActivityLoadingBar.setProgress(newProgress);
                    } else {
                        webViewActivityLoadingBar.setProgress(100);
                    }
                }
            }


            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                LogUtils.i("onShowCustomView");
                ViewGroup parent = (ViewGroup) webView.getParent();
                parent.removeView(webView);

                view.setBackgroundColor(Color.BLACK);
                parent.addView(view);

                myView = view;
                setFullScreen();


            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                LogUtils.i("onHideCustomView");
                if (myView != null){
                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);
                    parent.addView(webView);
                    myView = null;

                    quitFullScreen();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                LogUtils.i("shouldInterceptRequest  url = "+url);
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.i("Url loading = "+url);
                if (url.startsWith("intent") || url.startsWith("youku") || url.startsWith("iqiyi") || url.startsWith("qiyi")){
                    return true;
                }else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.i("开始加载");
                if (webViewActivityLoadingBar!= null)
                    webViewActivityLoadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.i("加载结束");
                if (webViewActivityLoadingBar!= null)
                    webViewActivityLoadingBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

    }



    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toolbar.setVisibility(View.GONE);
        webView.setRotation(90);
    }

    private void quitFullScreen(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        toolbar.setVisibility(View.VISIBLE);
        webView.setRotation(-90);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.loadDataWithBaseURL(null,"", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup)webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }
}
