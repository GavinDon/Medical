package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.AdapterCommentList;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.base.ViewHolder;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.ICommenListReq;
import com.sltj.medical.publicMsg.MsgReceiveDef.CommentListResp;
import com.sltj.medical.socketutil.HouseSocketConn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CommentListActivity extends BaseActivity implements OnItemClickListener {
	private int newsId;
	private List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
	private AdapterCommentList mAdapter;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_list);
		newsId = getIntent().getIntExtra("newsId", 1);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		initialize();
		initBar();
	}

	@Override
	public void initView() {
		mListView = (ListView) findViewById(R.id.lv_comment);
		commentListReq();
	}

	@Override
	public void setupData() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	/*
	 * 用户评论列表请求
	 */
	private int seqCommentList;

	private void commentListReq() {
		ICommenListReq req = new MsgInncDef.ICommenListReq();
		req.iUserId = MyApplication.userId;
		req.inewsid = newsId;
		seqCommentList = MyApplication.SequenceNo++;
		byte[] bData = HandleNetSendMsg.HandleUserCommentListPro(req, seqCommentList);
		HouseSocketConn.pushtoList(bData);
	}

	/*
	 * 评论响应
	 */
	private void commentListResp(Long recTime) {
		CommentListResp resp = (CommentListResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recTime);
		if (resp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			for (int i = 0; i < resp.info.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("commentPhone", resp.info.get(i).getPhone());
				map.put("commentContent", resp.info.get(i).getContent());
				map.put("commentLikeNum", resp.info.get(i).getILikedNum());
				map.put("commentIcon", resp.info.get(i).getIHeadImage());
				map.put("commentId", resp.info.get(i).getId());
				lst.add(map);
			}

			mAdapter = new AdapterCommentList(this, lst);
			mListView.setAdapter(mAdapter);
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
				if (iSequence == seqCommentList) {
					// 评论列表
					commentListResp(recvTime);
				}

			}

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		parent.getAdapter().getItem(position);
		ViewHolder vh = (ViewHolder) view.getTag();
		ImageView ivZAN=vh.getView(ImageView.class, R.id.iv_zan);
		ivZAN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	/*
	 * 初始化标题栏
	 */
	private void initBar() {
		LinearLayout llback = (LinearLayout) findViewById(R.id.ll_back);
		TextView iv = (TextView) findViewById(R.id.tv_title);
		iv.setText("评论列表");
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommentListActivity.this.finish();
			}
		});

	}

}
