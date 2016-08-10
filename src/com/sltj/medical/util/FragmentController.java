package com.sltj.medical.util;

import java.util.ArrayList;

import com.sltj.medical.HomepageFragment;
import com.sltj.medical.HelathManagerFragment;
import com.sltj.medical.NewsFragment;
import com.sltj.medical.PrivateDoctorFragment;
import com.sltj.medical.ProductFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentController {

	private int containerId;
	private FragmentManager fm;
	private ArrayList<Fragment> fragments;

	private static FragmentController controller;

	public static FragmentController getInstance(FragmentActivity activity, int containerId) {
		if (controller == null) {
			controller = new FragmentController(activity, containerId);
		}
		return controller;
	}

	public static void onDestroy() {
		controller = null;
	}

	private FragmentController(FragmentActivity activity, int containerId) {
		this.containerId = containerId;
		fm = activity.getSupportFragmentManager();
		initFragment();
	}

	private void initFragment() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new HomepageFragment());
		fragments.add(new NewsFragment());
		fragments.add(new PrivateDoctorFragment());
		fragments.add(new ProductFragment());
		fragments.add(new HelathManagerFragment());
		// hide与replace切换时 注释掉下面这段代码
		// ------------------------//
		FragmentTransaction ft = fm.beginTransaction();
		for (Fragment fragment : fragments) {
			ft.add(containerId, fragment);
		}
		ft.commitAllowingStateLoss();
		// ------------------------//
	}

	public void showFragment(int position) {
		// 在此处切换hide与replace;
		hideFragments();
		// replaceFragment(position);
		Fragment fragment = fragments.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(fragment);
		ft.commitAllowingStateLoss();
	}

	public void hideFragments() {
		FragmentTransaction ft = fm.beginTransaction();
		for (Fragment fragment : fragments) {
			if (fragment != null) {
				ft.hide(fragment);
			}
		}
		ft.commitAllowingStateLoss();
	}

	public Fragment getFragment(int position) {
		return fragments.get(position);
	}

	public void replaceFragment(int position) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(containerId, fragments.get(position));
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

}