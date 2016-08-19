package com.sltj.medical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sltj.medical.R.drawable;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Tijian_RecoderInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.PhysicalDetailResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.MTools;
import com.sltj.medical.util.ToastUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class RecordDetailActivity extends BaseActivity implements OnClickListener {
	private Map<String, String> map;
	private TextView tvdetail;
	private TextView tvTime;
	private PhotoView ivRecord;
	private Button BtnSave;
	private String recordIndex;
	private ImageLoader loader;
	private PhotoViewAttacher mAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_detail);
		map = (Map<String, String>) getIntent().getSerializableExtra("recordDetail");
		recordIndex = map.get("index");

		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReciver, filter);

		initialize();
		initBar();
	}

	@Override
	public void initView() {
		loader = ImageLoader.getInstance();
		tvdetail = (TextView) findViewById(R.id.tv_detail);
		tvTime = (TextView) findViewById(R.id.tv_detail_time);
		ivRecord = (PhotoView) findViewById(R.id.iv_physical_record);
		BtnSave = (Button) findViewById(R.id.btn_savetophone);
		tvdetail.setText(map.get("title"));
		tvTime.setText(map.get("date"));
		physicalDetailReq();
	}

	@Override
	public void setupData() {
		BtnSave.setOnClickListener(this);

	}

	private int seqPhysicalDetail;

	private void physicalDetailReq() {
		MsgInncDef.IPhsicalRecordDetailReq req = new MsgInncDef.IPhsicalRecordDetailReq();
		req.IUserId = MyApplication.userId;
		req.iRecordIndex = Integer.parseInt(recordIndex);
		seqPhysicalDetail = MyApplication.SequenceNo++;
		byte[] bData = HandleNetSendMsg.HandlePhysicalRecordDetailPro(req, seqPhysicalDetail);
		HouseSocketConn.pushtoList(bData);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReciver);
	}

	private void physicalDetailResp(Long recvTime) {
		MsgReceiveDef.PhysicalDetailResp resp = (PhysicalDetailResp) HandleMsgDistribute.getInstance()
				.queryCompleteMsg(recvTime);
		Net_Tijian_RecoderInfo_PRO info = resp.info;
		String url = resp.Szurl;
		loader.displayImage(url, ivRecord);
		// mAttacher = new PhotoViewAttacher(ivRecord);
		// mAttacher.update();
	}

	BroadcastReceiver mReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqPhysicalDetail) {
					physicalDetailResp(recvTime);
				}

			}
		}
	};

	/*
	 * 初始化标题栏
	 */
	private void initBar() {
		LinearLayout llback = (LinearLayout) findViewById(R.id.ll_back);
		TextView iv = (TextView) findViewById(R.id.tv_title);
		iv.setText(map.get("title"));
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecordDetailActivity.this.finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_savetophone:
			Drawable drawable = ivRecord.getDrawable();
			BitmapDrawable bd = (BitmapDrawable) drawable;
			Bitmap bm = bd.getBitmap();
			if (MTools.isSDCardExists()) {
				saveImageToGallery(this, bm);// 保存图片至相册

			} else {
				ToastUtils.show(this, "保存失败", 0);
			}
		}

	}

	/*
	 * 保存图片至相册
	 */
	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片

		File appDir = new File(Environment.getExternalStorageDirectory(), "诺贝尔");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			ToastUtils.show(context, "保存成功", 0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ToastUtils.show(context, "保存失败", 0);
		} catch (IOException e) {
			e.printStackTrace();
			ToastUtils.show(context, "保存失败", 0);
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(
				new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

	}

}
