package com.houwei.guaishang.data;

import java.util.ArrayList;

import com.houwei.guaishang.bean.BasePushResult;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.JsonUtil;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBReq {
	private DBHelper databaseHelper;

	public static int version = 1;

	private static DBReq instence;
	private boolean inited = false;

	private void init(ITopicApplication context, String account) {
		// mApp = app;
		getVersion(context);
		databaseHelper = new DBHelper(context, version, account);
		databaseHelper.getWritableDatabase();

		inited = true;
	}

	private void init(Context context, String account) {
		// mApp = app;
		getVersion(context);
		databaseHelper = new DBHelper(context, version, account);
		databaseHelper.getWritableDatabase();

		inited = true;
	}

	public boolean isInited() {
		return inited;
	}

	public synchronized static DBReq getInstence(ITopicApplication context) {
		if (instence == null || !instence.isInited()) {
			instence = new DBReq();
			instence.init(context, context.getMyUserBeanManager().getInstance()
					.getUserid());
		}
		return instence;
	}

	/*
	 * 推送来的
	 */
	public synchronized static DBReq getInstence(Context context) {
		if (instence == null || !instence.isInited()) {
			instence = new DBReq();
			instence.init(context, MyUserBeanManager.getMineUserID(context));
		}
		return instence;
	}

	private int getVersion(Context con) {
		try {
			PackageManager manager = con.getPackageManager();
			PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
			version = info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;
	}


	/******************************************************************************************
	 * 
	 */

	public ArrayList<CommentPushBean> getCommentPushBean() {
		synchronized (databaseHelper) {
			ArrayList<CommentPushBean> list = new ArrayList<CommentPushBean>();
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * from " + DBHelper._TABLE_COMMENT
					+ " where isUnReaded = 1", null);
			while (cursor.moveToNext()) {
				CommentPushBean scheduleBean = new CommentPushBean();
				scheduleBean.set_id(cursor.getInt(0));
				scheduleBean.setCommentMemberId(cursor.getString(1));
				scheduleBean.setCommentMemberName(cursor.getString(2));
				scheduleBean.setCommentMemberAvatar(JsonParser.getAvatarBean(cursor.getString(3)));
				scheduleBean.setCommentContent(cursor.getString(4));
				scheduleBean.setCreatedAt(cursor.getString(5));
				scheduleBean.setContent(cursor.getString(6));
				scheduleBean.setPushType(cursor.getInt(7));
				scheduleBean.setUnReaded(cursor.getInt(8) == BasePushResult.PushStateUnRead ? true: false);
				scheduleBean.initContentBean();
				list.add(scheduleBean);
			}
			cursor.close();
			db.close();
			return list;
		}
	}
	
	public void addComment(CommentPushBean scheduleBean) {
		synchronized (databaseHelper) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("commentMemberId", scheduleBean.getCommentMemberId());
			values.put("commentMemberRealName", scheduleBean.getCommentMemberName());
			values.put("commentMemberAvatar",  JsonUtil.getJson(scheduleBean.getCommentMemberAvatar()));
			values.put("commentContent", scheduleBean.getCommentContent());
			values.put("updatedAt", scheduleBean.getCreatedAt());
			values.put("content", scheduleBean.getContent());
			values.put("pushType", scheduleBean.getPushType());
			values.put("isUnReaded", scheduleBean.isUnReaded() ? BasePushResult.PushStateUnRead : BasePushResult.PushStateHadRead);
			db.insert(DBHelper._TABLE_COMMENT, null, values);
			db.close();
		}
	}

	public int getLastCommentID() {
		int _id = 0;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper._TABLE_COMMENT,
				new String[] { "_id" }, null, null, null, null, null, null);
		if (cursor.moveToLast() == true) {
			_id = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return _id;
	}

	public int getTotalUnReadCommentCount() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(1) from "
				+ DBHelper._TABLE_COMMENT + " where isUnReaded = 1", null);
		try {
			while (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				return count;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			cursor.close();
			db.close();
		}
		return 0;
	}
	
	// 未读-已读
	private void updateCommentHadRead() {
		synchronized (databaseHelper) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("isUnReaded", BasePushResult.PushStateHadRead);
			db.update(DBHelper._TABLE_COMMENT, values,null,null);
			db.close();
		}
	}
	
	public void deleteCommentHadRead() {
		synchronized (databaseHelper) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.delete(DBHelper._TABLE_COMMENT, null,null);
			db.close();
		}
	}
	
	/**
	 * 
	 ******************************************************************************************/
	
	
	
	
	
	
	/******************************************************************************************
	 * FANs
	 */
	public ArrayList<FansPushBean> getFansPushBean() {
		synchronized (databaseHelper) {
			ArrayList<FansPushBean> list = new ArrayList<FansPushBean>();
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * from " + DBHelper._TABLE_NEWFANS
					+ " where isUnReaded = 1", null);
			while (cursor.moveToNext()) {
				FansPushBean scheduleBean = new FansPushBean();
				scheduleBean.set_id(cursor.getInt(0));
				scheduleBean.setFansId(cursor.getString(1));
				scheduleBean.setFansName(cursor.getString(2));
				scheduleBean.setFansAvatar(cursor.getString(3));
				scheduleBean.setUnReaded(cursor.getInt(4) == BasePushResult.PushStateUnRead ? true: false);
				list.add(scheduleBean);
			}
			cursor.close();
			db.close();
			return list;
		}
	}
	
	public void addFansPushBean(FansPushBean scheduleBean) {
		synchronized (databaseHelper) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("fansId", scheduleBean.getFansId());
			values.put("fansName", scheduleBean.getFansName());
			values.put("fansAvatar", scheduleBean.getFansAvatar());
			values.put("isUnReaded", scheduleBean.isUnReaded() ? BasePushResult.PushStateUnRead : BasePushResult.PushStateHadRead);
			db.insert(DBHelper._TABLE_NEWFANS, null, values);
			db.close();
		}
	}


	public int getTotalUnReadFansCount() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(1) from "
				+ DBHelper._TABLE_NEWFANS + " where isUnReaded = 1", null);
		try {
			while (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				return count;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			cursor.close();
			db.close();
		}
		return 0;
	}
	
	
	public void deleteFansHadRead() {
		synchronized (databaseHelper) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.delete(DBHelper._TABLE_NEWFANS, null,null);
			db.close();
		}
	}
	
	public boolean CheckFansBeanExist(String fansID) {
		boolean exited = false;
		synchronized (databaseHelper) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			Cursor cursor = db.query(DBHelper._TABLE_NEWFANS,
					new String[] { "fansId" }, null, null, null, null,
					null, null);
			if (cursor.moveToLast() == true) {
				do {
					if (cursor.getString(0) != null
							&& cursor.getString(0).equals(fansID)) {
						exited = true;
						break;
					}
				} while (cursor.moveToPrevious());
			}
			cursor.close();
			db.close();
		}
		return exited;
	}
	/**
	 * fans end
	 ******************************************************************************************/
	
	
	
	
	
	
	
	public synchronized void close() {
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}
		instence.inited = false;
		instence = null;
	}


	public boolean ExecuteSQL(String sql) {
		synchronized (databaseHelper) {
			try {
				SQLiteDatabase db = databaseHelper.getWritableDatabase();
				boolean ret = SafeDBExecute(db, sql);
				db.close();
				return ret;
			} catch (Exception e) {
				return false;
			}
		}
	}

	/**
	 * ��ѯ
	 * 
	 * @param sql
	 *            ���
	 * @return ��ѯ ���
	 */
	public synchronized ArrayList<String[]> QuerySQL(String sql) {
		synchronized (databaseHelper) {
			ArrayList<String[]> list = new ArrayList<String[]>();

			try {

				SQLiteDatabase db = databaseHelper.getReadableDatabase();

				Cursor cursor = db.rawQuery(sql, null);
				while (cursor.moveToNext()) {
					int col = cursor.getColumnCount();
					String[] row = new String[col];
					for (int i = 0; i < col; i++) {
						row[i] = cursor.getString(i);
					}
					list.add(row);
				}
				cursor.close();
				db.close();
				return list;
			} catch (Exception e) {
				
			}
			return list;
		}

	}


	private boolean SafeDBExecute(SQLiteDatabase db, String sql) {
		try {
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
