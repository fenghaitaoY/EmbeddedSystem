package com.android.blue.smarthomefunc.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.WebViewVideoActivity;
import com.android.blue.smarthomefunc.entity.LogUtils;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @BindView(R.id.music_toolbar_search)
    ImageView musicToolbarSearch;
    @BindView(R.id.music_toolbar)
    LinearLayout musicToolbar;
    @BindView(R.id.webView_loading_bar)
    ProgressBar webViewLoadingBar;
    @BindView(R.id.webView)
    WebView webView;


    private static final String IQIYI_URL = "http://m.iqiyi.com/";

    private View mVideoFragmentView;
    private Context mContext;
    private Unbinder butterknife;
    private WebSettings mWebSettings;
    private View myView;


    public VideoFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVideoFragmentView = inflater.inflate(R.layout.fragment_video, container, false);
        mContext = getActivity();
        butterknife = ButterKnife.bind(this, mVideoFragmentView);

        webViewLoadingBar.setVisibility(View.GONE);

        return mVideoFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        webView.loadUrl(IQIYI_URL);



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

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtils.i("progress change  , new progress = "+newProgress);
                if (webViewLoadingBar != null) {
                    if (newProgress < 100) {
                        webViewLoadingBar.setProgress(newProgress);
                    } else {
                        webViewLoadingBar.setProgress(100);
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
                //此处为了解决进入界面自己会启动activity的bug
                if (url.equals(IQIYI_URL)){
                    return false;
                }

                //点击界面内容链接, 跳转到新的activity
                webView.onPause();

                Intent intent = new Intent(getActivity(), WebViewVideoActivity.class);
                intent.putExtra("url", url);
                getActivity().startActivity(intent);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.i("开始加载");
                if (webViewLoadingBar!= null)
                    webViewLoadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.i("加载结束");
                if (webViewLoadingBar!= null)
                    webViewLoadingBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                LogUtils.i(""+error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtils.i(""+errorCode);
            }
        });
        
        
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i("canGoBack ="+webView.canGoBack());
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }


    private void setFullScreen(){
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void quitFullScreen(){
        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setAttributes(attrs);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknife.unbind();
        if (webView != null){
            webView.loadDataWithBaseURL(null,"", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup)webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }
}
