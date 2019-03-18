package com.android.blue.smarthomefunc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.utils.X5WebView;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewVideoActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webView_activity_loading_bar)
    ProgressBar webViewActivityLoadingBar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.video_back)
    ImageView videoBack;
    @BindView(R.id.video_title)
    TextView videoTitle;
    @BindView(R.id.video_toolbar)
    LinearLayout videoToolbar;


    WebSettings mWebSettings;
    View myView;
    String title;
    Handler mHandler = new Handler();
    Thread jsoupThread;
    JsoupRunnable mJsoupRunnable;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_web_view_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null){
            videoPath = intent.getStringExtra("path");
        }
        LogUtils.i("path="+videoPath);
        mJsoupRunnable = new JsoupRunnable(videoPath);
        jsoupThread = new Thread(mJsoupRunnable);


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
        mWebSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setGeolocationEnabled(true);
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        mWebSettings.setAppCachePath(this.getDir("appcache",0).getPath());
        mWebSettings.setDatabasePath(this.getDir("databases", 0).getPath());
        mWebSettings.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        mWebSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        mWebSettings.setLoadsImagesAutomatically(true);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        //去除滚动条
        IX5WebViewExtension ix5WebViewExtension = webView.getX5WebViewExtension();
        if (ix5WebViewExtension != null){
            ix5WebViewExtension.setScrollBarFadingEnabled(false);
        }

        webView.loadUrl(videoPath);
        setToolbarTitle(videoPath);

        //由于设置了弹窗检验调用结果, 所以需要支持js对话框, webview只是载体, 内容的渲染需要使用webviewchromeClient类去实现
        //通过设置webchromeclient对象处理js的对话框, 设置响应js的alert函数
        webView.setWebChromeClient(new WebChromeClient() {

            //监听alert回调
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return false;

            }

            @Override
            public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsConfirm(webView, s, s1, jsResult);
            }

            //拦截输入框
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //根据协议的参数,判断是否是所需要的url
                //一般根据scheme & authority判断
                LogUtils.i("onJsPrompt start");
                Uri uri = Uri.parse(message);
                if (uri.getScheme().equals("js")) {
                    if (uri.getAuthority().equals("webview")) {
                        result.confirm("js 调用成功");
                    }
                    return true;
                }

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                LogUtils.i("webview title =" + title);
                //toolbar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtils.i("progress change  , new progress = " + newProgress);
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
                if (myView != null) {
                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);
                    parent.addView(webView);
                    myView = null;

                    quitFullScreen();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                LogUtils.i("shouldInterceptRequest  url = " + url);
                //拦截广告
                if (url.contains("li1l.hlzma.cn")
                        || url.contains("image.xcxzxc.cn")|| url.contains("p.mendoc.cn")
                        || url.contains("hm.baidu.com") || url.contains("js/moge/foot")
                        || url.contains("f5.mtqys.com")){
                    return new WebResourceResponse(null,null,null);
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.i("Url loading = " + url);
                if (url.contains("iqiyi") || url.contains("qiyi")) {
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.i("开始加载");
                if (webViewActivityLoadingBar != null)
                    webViewActivityLoadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.i("加载结束");
                if (webViewActivityLoadingBar != null)
                    webViewActivityLoadingBar.setVisibility(View.GONE);

            }

        });

        videoBack.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("-------------------"+webView.getX5WebViewExtension());
    }

    /**
     * 设置title
     *
     * @param urlStr
     */
    private void setToolbarTitle(final String urlStr) {
        jsoupThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class JsoupRunnable implements Runnable{
        String urlStr;
        public JsoupRunnable(String str){
            urlStr = str;
        }

        @Override
        public void run() {
            try {
                Document doc = Jsoup.connect(urlStr).get();

                Elements titleElements = doc.select("div.playList-head");
                for (Element e : titleElements) {
                    LogUtils.i("title =" + e.select("a").attr("title"));
                    //解析html中title标题
                    title = e.select("a").attr("title");
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //如果title html中没有就截取webview中title
                        if (TextUtils.isEmpty(title)) {
                            if (webView != null) {
                                String[] arrStr = webView.getTitle().split("-");
                                LogUtils.i(" arrstr =" + arrStr[0]);
                                title = arrStr[0];
                            }
                        }
                        LogUtils.i(" title = " + title);
                        if (videoTitle != null)
                            videoTitle.setText(title);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        videoToolbar.setVisibility(View.GONE);
    }

    private void quitFullScreen() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        videoToolbar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || webView == null || intent.getData() == null) return;
        LogUtils.i(intent.getData().toString());
        webView.loadUrl(intent.getData().toString());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.video_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (webView != null && webView.canGoBack()){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
