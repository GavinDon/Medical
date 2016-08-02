package com.sltj.medical.wedgit;
import com.sltj.medical.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadingDialog extends Dialog {
	private TextView tv;

	public LoadingDialog(Context context) {
		super(context, R.style.loadingDialogStyle);
	}

	private LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		tv = (TextView)this.findViewById(R.id.tv);
		tv.setText("正在加载...");
		LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
		linearLayout.getBackground().setAlpha(210);
		
		this.setCanceledOnTouchOutside(false);
	}
}