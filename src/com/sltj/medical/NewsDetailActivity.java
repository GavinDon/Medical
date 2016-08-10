package com.sltj.medical;

import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eMSG_OPER_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.ICollectNewsReq;
import com.sltj.medical.publicMsg.MsgInncDef.ICommenListReq;
import com.sltj.medical.publicMsg.MsgInncDef.IReadNewsReq;
import com.sltj.medical.publicMsg.MsgInncDef.IUserCommentReq;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.CollectNewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.UserCommentResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.MTools;
import com.sltj.medical.util.ToastUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class NewsDetailActivity extends BaseActivity implements OnClickListener {
	private LinearLayout llBack;
	private Button btnCollect;
	private Button btnShare;
	private EditText etComment; // 评论控件
	private ImageButton btnCommentList;// 评论LIST
	private Button btnComment;// 评论
	private int newsId; // 资讯ID
	private String newsUrl;// 请求的资讯URL
	private WebView mWebview;
	private WebSettings mWebSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		newsId = Integer.parseInt(getIntent().getStringExtra("newsId"));
		newsUrl = Define.newsUrl + newsId;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		initialize();
		newsReadReq();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		btnCollect = (Button) findViewById(R.id.btn_news_collect);
		btnShare = (Button) findViewById(R.id.btn_news_share);
		etComment = (EditText) findViewById(R.id.et_comment);
		btnCommentList = (ImageButton) findViewById(R.id.btn_comment_list);
		btnComment = (Button) findViewById(R.id.btn_comment);
		initweb();
	}

	@Override
	public void setupData() {
		llBack.setOnClickListener(this);
		btnCommentList.setOnClickListener(this);
		btnCollect.setOnClickListener(this);
		btnComment.setOnClickListener(this);

	}

	private int seqReadNews;

	/**
	 * 初始化WebView
	 */
	private void initweb() {
		mWebview = (WebView) findViewById(R.id.mv_news);
		mWebSettings = mWebview.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// mWebview.setWebChromeClient(new WebChromeClienter());
		mWebview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			};
		});

		if (!MTools.isStringEmpty(newsUrl)) {
			mWebview.loadUrl(newsUrl);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			this.finish();
			break;
		case R.id.btn_comment_list:
			// commentReq();
			Intent mIntent = new Intent(this, CommentListActivity.class);
			mIntent.putExtra("newsId", newsId);
			startActivity(mIntent);
			break;
		case R.id.btn_news_collect:
			collectNewsReq();
			break;
		case R.id.btn_comment:
			commentReq();
			break;
		}
	}

	// -----------------------------------------------//
	/*
	 * 阅读资讯请求
	 */
	private void newsReadReq() {
		IReadNewsReq req = new MsgInncDef.IReadNewsReq();
		req.iUserId = MyApplication.userId;
		req.inewsid = newsId;
		seqReadNews = MyApplication.SequenceNo++;
		byte[] bData = HandleNetSendMsg.HandleReadNewsPro(req, seqReadNews);
		HouseSocketConn.pushtoList(bData);

	}

	/*
	 * 收藏资讯请求
	 */
	private int seqCollectNews;

	private void collectNewsReq() {
		ICollectNewsReq req = new MsgInncDef.ICollectNewsReq();
		req.inewsid = newsId;
		req.collectType = eMSG_OPER_PRO.EN_ADD_PRO;
		req.iUserId = MyApplication.userId;
		seqCollectNews = MyApplication.SequenceNo++;
		byte[] bData = HandleNetSendMsg.HandleCollectNewsPro(req, seqCollectNews);
		HouseSocketConn.pushtoList(bData);
	}

	private int sequserComment;

	/*
	 * 用户发表评论请求
	 */
	private void commentReq() {
		IUserCommentReq req = new MsgInncDef.IUserCommentReq();
		req.iUserId = MyApplication.userId;
		req.inewsid = newsId;
		req.szComment = etComment.getText().toString();
		sequserComment = MyApplication.SequenceNo++;
		byte[] bData = HandleNetSendMsg.HandleUserCommentPro(req, sequserComment);
		HouseSocketConn.pushtoList(bData);
	}

	/*
	 * 评论响应
	 */
	private void commentResp(Long recTime) {
		UserCommentResp resp = (UserCommentResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recTime);
		if (resp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			ToastUtils.show(this, "评论成功 ", 0);
			etComment.setText("");

		}
	}

	/*
	 * 收藏响应
	 */
	private void collectResp(Long recTime) {
		CollectNewsResp resp = (CollectNewsResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recTime);
		if (resp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			ToastUtils.show(this, "收藏成功 ", 0);

		}
	}

	/*
	 * 广播接收从socket中得到的值
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqCollectNews) {
					// 收藏处理
					collectResp(recvTime);
				}
				if (iSequence == sequserComment) {
					// 资讯处理
					commentResp(recvTime);
				}

			}

		}
	};

}
