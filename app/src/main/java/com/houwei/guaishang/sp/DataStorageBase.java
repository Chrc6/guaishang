package com.houwei.guaishang.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.houwei.guaishang.manager.ITopicApplication;

import java.util.Set;


/**
 * 主要目的简化对sharedPreference的操作，避免key重复，对android2.3以上使用apply取代commit，并支持指定配置文件名<br>
 * 
 * 
 * 所有对sharepreference的读写都写在这里<br>
 * 使用说明<br>
 * 1、定义常量key<br>
 * 2、使用提供好的putInt、getInt等进行操作<br>
 * 3、变量跟用户有关的使用putInt4UUID、getInt4UUID等进行操作<br>
 * 
 * 
 * @author alexlong
 */
public class DataStorageBase {

	// ========== 以下是具体对sharedpreference封装的实现 =================

	/**
	 * SharedPreferences的文件名<br>
	 * 全量设置，即和uuid不相关。即切换帐号后设置不变
	 */
	public static final String PREFS_NAME_FORMAX_SETTING = "formax_setting";

	/**
	 * 跟uuid相关的设置
	 */
	private final static String PREFS_NAME_4_UIN_DEFAULT = "preference";

	/**
	 * 封装Editor，主要目的是在Android2.3以上系统调用apply()替换commit()
	 * 
	 * 
	 */
	private static class ConfigEditor implements Editor {
		private Editor mEditor;

		public ConfigEditor(Editor editor) {
			mEditor = editor;
		}

		@Override
		public void apply() {
			commit();
		}

		@Override
		public Editor clear() {
			mEditor.clear();
			return this;
		}

		@Override
		public boolean commit() {
			mEditor.apply();
			return true;
		}

		@Override
		public Editor putBoolean(String key, boolean value) {
			mEditor.putBoolean(key, value);
			return this;
		}

		@Override
		public Editor putFloat(String key, float value) {
			mEditor.putFloat(key, value);
			return this;
		}

		@Override
		public Editor putInt(String key, int value) {
			mEditor.putInt(key, value);
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			mEditor.putLong(key, value);
			return this;
		}

		@Override
		public Editor putString(String key, String value) {
			mEditor.putString(key, value);
			return this;
		}

		@Override
		public Editor putStringSet(String key, Set<String> values) {
			mEditor.putStringSet(key, values);
			return this;
		}

		@Override
		public Editor remove(String key) {
			mEditor.remove(key);
			return this;
		}

	}

	private static Context getContext() {
		return ITopicApplication.getContext();
	}

	// ------------------------------------------------------------------------------------------

	/**
	 * 获取存储器 update: 设为private，增强封装性；支持制定preference名字
	 */
	private static SharedPreferences getPreferences(String preferenceName) {
		return getContext().getSharedPreferences(preferenceName,
				Context.MODE_MULTI_PROCESS); // multi process
												// communication
												// must be set
	}

	private static SharedPreferences getPreferences() {
		return getPreferences(PREFS_NAME_FORMAX_SETTING);
	}

	private static Editor edit() {
		return edit(PREFS_NAME_FORMAX_SETTING);
	}

	// 此处设为private增强封装性
	private static Editor edit(String prefName) {
		return new ConfigEditor(getPreferences(prefName).edit());
	}

	public static void putBool(String prefName, String key, boolean value) {
		Editor edit = edit(prefName);
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static void putBool(String key, boolean value) {
		putBool(PREFS_NAME_FORMAX_SETTING, key, value);
	}

	public static boolean getBool(String prefName, String key, boolean defValue) {
		return getPreferences(prefName).getBoolean(key, defValue);
	}

	public static boolean getBool(String key, boolean defValue) {
		return getBool(PREFS_NAME_FORMAX_SETTING, key, defValue);
	}

	public static void putInt(String prefName, String key, int value) {
		Editor edit = edit(prefName);
		edit.putInt(key, value);
		edit.commit();
	}

	public static void putInt(String key, int value) {
		putInt(PREFS_NAME_FORMAX_SETTING, key, value);
	}

	public static int getInt(String prefName, String key, int defValue) {
		return getPreferences(prefName).getInt(key, defValue);
	}

	public static int getInt(String key, int defValue) {
		return getInt(PREFS_NAME_FORMAX_SETTING, key, defValue);
	}

	public static void putLong(String prefName, String key, long value) {
		Editor edit = edit(prefName);
		edit.putLong(key, value);
		edit.commit();
	}

	public static void putLong(String key, long value) {
		putLong(PREFS_NAME_FORMAX_SETTING, key, value);
	}

	public static long getLong(String prefName, String key, long defValue) {
		return getPreferences(prefName).getLong(key, defValue);
	}

	public static long getLong(String key, long defValue) {
		return getLong(PREFS_NAME_FORMAX_SETTING, key, defValue);
	}

	public static void putString(String prefName, String key, String value) {
		Editor edit = edit(prefName);
		edit.putString(key, value);
		edit.commit();
	}

	public static void putString(String key, String value) {
		putString(PREFS_NAME_FORMAX_SETTING, key, value);
	}

	public static String getString(String prefName, String key, String defValue) {
		return getPreferences(prefName).getString(key, defValue);
	}

	public static String getString(String key, String defValue) {
		return getString(PREFS_NAME_FORMAX_SETTING, key, defValue);
	}

	public static void remove(String key) {
		Editor edit = edit();
		edit.remove(key);
		edit.commit();
	}

	/**
	 * 删除所有keys对应的preference项，批量操作一次commit
	 * 
	 * @param keys
	 */
	public static void removeAll(String[] keys) {
		if (keys == null || keys.length == 0) {
			return;
		}
		Editor edit = edit();
		for (int i = 0; i < keys.length; ++i) {
			edit.remove(keys[i]);
		}
		edit.commit();
	}

	/**
	 * @param key
	 * @return
	 */
	public static boolean contains(String key) {
		return getPreferences().contains(key);
	}

	/**
	 * 
	 * @param listener
	 */
	public static void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		getPreferences().registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public static void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		getPreferences().unregisterOnSharedPreferenceChangeListener(listener);
	}

	// ------------------------------------------------------------------------------------------

	/**
	 * @return
	 */
	private static SharedPreferences getPreferences4UUID(long uuid) {
		if (uuid == 0) {
			return getPreferences();
		}
		String preferenceName = "_" + PREFS_NAME_4_UIN_DEFAULT;

		return getContext().getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
	}

	private static Editor edit4UUID(long uuid) {
		return new ConfigEditor(getPreferences4UUID(uuid).edit());
	}

	public static void putInt4UUID(String key, int value, long uuid) {
		Editor edit = edit4UUID(uuid);
		edit.putInt(key, value);
		edit.commit();
	}

	public static int getInt4UUID(String key, int defValue, long uuid) {
		return getPreferences4UUID(uuid).getInt(key, defValue);
	}

	public static void putString4UUID(String key, String value, long uuid) {
		Editor edit = edit4UUID(uuid);
		edit.putString(key, value);
		edit.commit();
	}

	public static String getString4UUID(String key, String defValue, long uuid) {
		return getPreferences4UUID(uuid).getString(key, defValue);
	}

	public static void putLong4UUID(String key, long value, long uuid) {
		Editor edit = edit4UUID(uuid);
		edit.putLong(key, value);
		edit.commit();
	}

	public static long getLong4UUID(String key, long defValue, long uuid) {
		return getPreferences4UUID(uuid).getLong(key, defValue);
	}

	public static void remove4UUID(String key, long uuid) {
		Editor edit = edit4UUID(uuid);
		edit.remove(key);
		edit.commit();
	}

	/**
	 * 删除所有keys对应的preference项，批量操作一次commit
	 * 
	 * @param keys
	 * @param uuid
	 */
	public static void removeAll4UUID(String[] keys, long uuid) {
		if (keys == null || keys.length == 0) {
			return;
		}
		Editor edit = edit4UUID(uuid);
		for (int i = 0; i < keys.length; ++i) {
			edit.remove(keys[i]);
		}
		edit.commit();
	}
}
