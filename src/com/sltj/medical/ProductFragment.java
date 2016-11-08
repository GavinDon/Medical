package com.sltj.medical;

import com.sltj.medical.base.WebAppInterface;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 产品Fragment
 * @author linan
 *
 */
public class ProductFragment extends Fragment {
	private ImageView ivBack;
	private TextView tvTitle;
	// 进度条
	private WebView mWebview;
	private WebSettings mWebSettings;

	// 进度条
	private ProgressBar pbarMember = null;

	private String strTitle = "";
	private String strUrl = "http://www.baidu.com/";
	private View view;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 view=inflater.from(this.getActivity()).inflate(R.layout.fragment_product, null);
		 pbarMember=(ProgressBar) view.findViewById(R.id.rbar_webview);
		 initweb();
		return view;
	}

	/**
	 * 初始化WebView
	 */
	private void initweb() {
		mWebview = (WebView) view.findViewById(R.id.wv_sevcomplaint);
		mWebview.addJavascriptInterface(new WebAppInterface(this.getActivity()), "Android");
		mWebSettings = mWebview.getSettings();
		  if(Build.VERSION.SDK_INT >= 19) {
			  //设置自动加载图片
			  mWebview.getSettings().setLoadsImagesAutomatically(true);
		    } else {
		    	mWebview.getSettings().setLoadsImagesAutomatically(false);
		    }
		  mWebview.getSettings().setJavaScriptEnabled(true);
		  mWebview.getSettings().setDomStorageEnabled(true);
		  mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		  mWebview.getSettings().setSupportMultipleWindows(false);
		  mWebview.getSettings().getAllowFileAccess();
		  mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		  mWebview.getSettings().setUseWideViewPort(true);
		  mWebview.getSettings().setLoadWithOverviewMode(true);
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebview.loadUrl(strUrl);
		mWebview.setWebChromeClient(new WebChromeClienter());
		mWebview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			};
			@Override
			public void onPageFinished(WebView view, String url) {
				  if(!mWebview.getSettings().getLoadsImagesAutomatically()) {
					  mWebview.getSettings().setLoadsImagesAutomatically(true);
				    }
			}
		});
	}

	class WebChromeClienter extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {

			if (pbarMember.getProgress() < newProgress) {
				pbarMember.setProgress(newProgress);
				pbarMember.postInvalidate();
			}

			if (newProgress == 100) {
				pbarMember.setVisibility(View.GONE);
			}

			super.onProgressChanged(view, newProgress);

		}
		
		
		
	};


//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
//        	mWebview.goBack(); // goBack()表示返回WebView的上一页面
//            return true;
//        }
//        return super.onKeyDown(keyCode,event);
//    }



}
