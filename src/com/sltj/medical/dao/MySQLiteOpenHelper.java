package com.sltj.medical.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 自定义helper类继承daoMaster类中的内部类openHelper来做现数库的升级
 * Author linan 
 * E-mail: fengyunzhinan@163.com 
 * Date：2016年6月3日 上午9:28:46
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

	public MySQLiteOpenHelper(Context context, String name, CursorFactory factory) {
		super(context, name, factory);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 做数据库升级(防止数据全部丢失)
		if(newVersion>oldVersion){
//			 MigrationHelper.getInstance().migrate(db, BusLineDao.class);
		}
	}

}
