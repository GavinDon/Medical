package com.sltj.medical;

import com.sltj.medical.base.BaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PwdLoginActivity extends BaseActivity implements OnClickListener {
	// 忘记密码
	private TextView tvForgetPwd;
	// 快速登录
	private TextView tvQuikLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwd_login);
		initialize();
	}

	@Override
	public void initView() {
		tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
		tvQuikLogin = (TextView) findViewById(R.id.tv_quck_login);

	}

	@Override
	public void setupData() {
		tvForgetPwd.setOnClickListener(this);
		tvQuikLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_forget_pwd:

			intent.setClass(this, ForgetPwdActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.tv_quck_login:
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			this.finish();

		default:
			break;
		}

	}
}
