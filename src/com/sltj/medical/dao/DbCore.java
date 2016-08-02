package com.sltj.medical.dao;

import com.sltj.medical.dao.DaoMaster.OpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Author linan Date：2016年6月28日 上午8:51:46
 */
public class DbCore {
	// DaoMaster保存了数据库对象和管理DAO类的class
	public static DaoMaster daoMaster;
	// 管理所有可用的DAO对象，可以通过getter方法获得
	public static DaoSession daoSession;
	// 数据库名称
	private static String DB_NAME = "publicBus.db";
	private static Context mContext;
	public static void init(Context context) {
        init(context, DB_NAME);
    }
	public static void init(Context context,String dbName) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null");
		}
		mContext = context.getApplicationContext();
		DB_NAME = dbName;

	}

	public static DaoMaster getDaoMaster() {
		if (daoMaster == null) {
			OpenHelper helper = new MySQLiteOpenHelper(mContext, DB_NAME, null);
			SQLiteDatabase db = helper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
		}
		return daoMaster;
	}

	public static DaoSession getDaoSession() {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster();
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public static void enableQueryBuilderLog() {

		QueryBuilder.LOG_SQL = true;
		QueryBuilder.LOG_VALUES = true;
	}

}
