package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.sltj.medical.R;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_CommentInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.ICommenZanReq;
import com.sltj.medical.publicMsg.MsgInncDef.IReadNewsReq;
import com.sltj.medical.publicMsg.MsgReceiveDef.CommentZanResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.LogUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterCommentList extends MyBaseAdapter<Map<String, Object>> {
	private Context mContect;
	private int seqCommentZan;

	public AdapterCommentList(Context context, List<Map<String, Object>> lst) {
		super(context, lst);
		this.mContect = context;
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[] { R.id.commenlist_user_icon, R.id.tv_comment_phone, R.id.iv_zan, R.id.tv_comment_content,
				R.id.tv_zan_num };
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_comment_list, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		final Map<String, Object> map = this.getItemT(position);
		// lst.get(position).getContent();
		TextView tvContent = vh.getView(TextView.class, R.id.tv_comment_content);
		TextView tvPhone = vh.getView(TextView.class, R.id.tv_comment_phone);
		final TextView tvZanNum = vh.getView(TextView.class, R.id.tv_zan_num);
		ImageView ivZan = vh.getView(ImageView.class, R.id.iv_zan);
		tvContent.setText(String.valueOf(map.get("commentContent")));
		tvPhone.setText(String.valueOf(map.get("commentPhone")));
		tvZanNum.setText(String.valueOf(map.get("commentLikeNum")));
		final int commentId = (Integer) (map.get("commentId"));
		ivZan.setOnClickListener(new OnClickListener() {
			int num=(Integer) map.get("commentLikeNum");
			@Override
			public void onClick(View v) {
				ICommenZanReq req = new MsgInncDef.ICommenZanReq();
				req.iUserId = MyApplication.userId;
				req.iCommentid=commentId;
				LogUtils.i(commentId+"\n");
				seqCommentZan = MyApplication.SequenceNo++;
				byte[] bData = HandleNetSendMsg.HandleCommentZanPro(req, seqCommentZan);
				HouseSocketConn.pushtoList(bData);
				
				tvZanNum.setText(String.valueOf(++num));
			}
			
		});
	}

}
