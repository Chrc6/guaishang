package com.houwei.guaishang.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String name = "_itopic.db";
	private int version;
	
	private static final String listname_dynamic = "table_comment";
	public static String _TABLE_COMMENT;
	
	private static final String listname_newfans = "table_newfans";
	public static String _TABLE_NEWFANS;
	
	
	public DBHelper(Context context, int version, String account) {
		super(context, account + name, null, version);
		_TABLE_COMMENT = listname_dynamic ;
		_TABLE_NEWFANS = listname_newfans ;
		this.version = version;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ver1(db);
		if (version > 1) {
			for (int i = 2; i <= version; i++) {
				a: switch (i) {
				case 2:
					ver2(db);
					break a;
				}
			}
		}
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= 1) {
			for (int i = oldVersion + 1; i <= newVersion; i++) {
				a: switch (i) {
				case 2:
					ver2(db);
					break a;

				}
			}
		}
	}

	// 1 == 未读 == true
	public final void ver1(SQLiteDatabase db) {		
		db.execSQL("create table if not exists " + _TABLE_COMMENT + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "commentMemberId text,"
				+ "commentMemberRealName text,"
				+ "commentMemberAvatar text,"
				+ "commentContent text,"
				+ "updatedAt text,"
				+ "content text,"
				+ "pushType integer,"
				+ "isUnReaded integer" + ")");
		
		db.execSQL("create table if not exists " + _TABLE_NEWFANS + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "fansId text,"
				+ "fansName text,"
				+ "fansAvatar text,"
				+ "isUnReaded integer" + ")");
	}
	

	private void ver2(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}


}
