package com.houwei.guaishang.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.easemob.EaseEmojicon;
import com.houwei.guaishang.easemob.EaseEmojiconGroupEntity;
import com.houwei.guaishang.easemob.EaseEmojicon.Type;
import com.houwei.guaishang.tools.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class FaceManager {
	public static final String DELETE_KEY = "em_delete_delete_expression";
	   
	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
//	public static final int NUM_PAGE = 5;// 总共有多少页
//	public static int NUM = 20;// 每页20个表情,还有最后一个删除button
	private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private ITopicApplication mApp;
	private ArrayList<String> faceKeys;

	private List<EaseEmojicon> emojiconList;//输入框表情(小)面板专用
	private List<EaseEmojicon> gifTuzkiList;//输入框gif表情(兔斯基)面板专用
	private List<EaseEmojicon> gifPaopaobingList;//输入框gif表情(泡泡兵)面板专用
	private List<EaseEmojicon> gifBaozouList;//输入框gif表情(暴走漫画)面板专用
	private List<EaseEmojicon> gifWorkList;//输入框gif表情(工作)面板专用
	
	
	public FaceManager(ITopicApplication mApp) {
		this.mApp = mApp;
	}

	public Map<String, Integer> getFaceMap() {
		return mFaceMap;
	}

	public ArrayList<String> getFaceKeys() {
		return faceKeys;
	}

	public void initFaceMap() {
		// TODO Auto-generated method stub
		mFaceMap.put("[微笑]", R.drawable.expression_1);
		mFaceMap.put("[撇嘴]", R.drawable.expression_2);
		mFaceMap.put("[色]", R.drawable.expression_3);
		mFaceMap.put("[发呆]", R.drawable.expression_4);
		mFaceMap.put("[得意]", R.drawable.expression_5);
		mFaceMap.put("[流泪]", R.drawable.expression_6);
		mFaceMap.put("[害羞]", R.drawable.expression_7);
		mFaceMap.put("[闭嘴]", R.drawable.expression_8);
		mFaceMap.put("[睡]", R.drawable.expression_9);
		mFaceMap.put("[大哭]", R.drawable.expression_10);
		mFaceMap.put("[尴尬]", R.drawable.expression_11);
		mFaceMap.put("[发怒]", R.drawable.expression_12);
		mFaceMap.put("[调皮]", R.drawable.expression_13);
		mFaceMap.put("[呲牙]", R.drawable.expression_14);
		mFaceMap.put("[惊讶]", R.drawable.expression_15);
		mFaceMap.put("[难过]", R.drawable.expression_16);
		mFaceMap.put("[酷]", R.drawable.expression_17);
		mFaceMap.put("[冷汗]", R.drawable.expression_18);
		mFaceMap.put("[抓狂]", R.drawable.expression_19);
		mFaceMap.put("[吐]", R.drawable.expression_20);
		mFaceMap.put("[偷笑]", R.drawable.expression_21);
		mFaceMap.put("[白眼]", R.drawable.expression_23);
		mFaceMap.put("[傲慢]", R.drawable.expression_24);
		mFaceMap.put("[饥饿]", R.drawable.expression_25);
		mFaceMap.put("[困]", R.drawable.expression_26);
		mFaceMap.put("[惊恐]", R.drawable.expression_27);
		mFaceMap.put("[流汗]", R.drawable.expression_28);
		mFaceMap.put("[憨笑]", R.drawable.expression_29);
		mFaceMap.put("[悠闲]", R.drawable.expression_30);
		mFaceMap.put("[奋斗]", R.drawable.expression_31);
		mFaceMap.put("[咒骂]", R.drawable.expression_32);
		mFaceMap.put("[疑问]", R.drawable.expression_33);
		mFaceMap.put("[嘘]", R.drawable.expression_34);
		mFaceMap.put("[晕]", R.drawable.expression_35);
		mFaceMap.put("[疯了]", R.drawable.expression_36);
		mFaceMap.put("[衰]", R.drawable.expression_37);
		mFaceMap.put("[骷髅]", R.drawable.expression_38);
		mFaceMap.put("[敲打]", R.drawable.expression_39);
		mFaceMap.put("[再见]", R.drawable.expression_40);
		mFaceMap.put("[擦汗]", R.drawable.expression_41);
		mFaceMap.put("[抠鼻]", R.drawable.expression_42);
		mFaceMap.put("[鼓掌]", R.drawable.expression_43);
		mFaceMap.put("[坏笑]", R.drawable.expression_45);
		mFaceMap.put("[糗大了]", R.drawable.expression_44);
		mFaceMap.put("[左哼哼]", R.drawable.expression_46);
		mFaceMap.put("[右哼哼]", R.drawable.expression_47);
		mFaceMap.put("[哈欠]", R.drawable.expression_48);
		mFaceMap.put("[鄙视]", R.drawable.expression_49);
		mFaceMap.put("[委屈]", R.drawable.expression_50);
		mFaceMap.put("[快哭了]", R.drawable.expression_51);
		mFaceMap.put("[阴险]", R.drawable.expression_52);
		mFaceMap.put("[亲亲]", R.drawable.expression_53);
		mFaceMap.put("[吓]", R.drawable.expression_54);
		mFaceMap.put("[可怜]", R.drawable.expression_55);
		mFaceMap.put("[菜刀]", R.drawable.expression_56);
		mFaceMap.put("[西瓜]", R.drawable.expression_57);
		mFaceMap.put("[啤酒]", R.drawable.expression_58);
		mFaceMap.put("[篮球]", R.drawable.expression_59);
		mFaceMap.put("[乒乓]", R.drawable.expression_60);
		mFaceMap.put("[咖啡]", R.drawable.expression_61);
		mFaceMap.put("[饭]", R.drawable.expression_62);
		mFaceMap.put("[猪头]", R.drawable.expression_63);
		mFaceMap.put("[玫瑰]", R.drawable.expression_64);
		mFaceMap.put("[凋谢]", R.drawable.expression_65);
		mFaceMap.put("[嘴唇]", R.drawable.expression_66);
		mFaceMap.put("[爱心]", R.drawable.expression_67);
		mFaceMap.put("[心碎]", R.drawable.expression_68);
		mFaceMap.put("[蛋糕]", R.drawable.expression_69);
		mFaceMap.put("[闪电]", R.drawable.expression_70);
		mFaceMap.put("[炸弹]", R.drawable.expression_71);
		mFaceMap.put("[刀]", R.drawable.expression_72);
		mFaceMap.put("[足球]", R.drawable.expression_73);
		mFaceMap.put("[瓢虫]", R.drawable.expression_74);
		mFaceMap.put("[便便]", R.drawable.expression_75);
		mFaceMap.put("[月亮]", R.drawable.expression_76);
		mFaceMap.put("[太阳]", R.drawable.expression_77);
		mFaceMap.put("[礼物]", R.drawable.expression_78);
		mFaceMap.put("[拥抱]", R.drawable.expression_79);
		mFaceMap.put("[强]", R.drawable.expression_80);
		mFaceMap.put("[弱]", R.drawable.expression_81);
		mFaceMap.put("[握手]", R.drawable.expression_82);
		mFaceMap.put("[胜利]", R.drawable.expression_83);
		mFaceMap.put("[抱拳]", R.drawable.expression_84);
		mFaceMap.put("[勾引]", R.drawable.expression_85);
		mFaceMap.put("[拳头]", R.drawable.expression_86);
		mFaceMap.put("[差劲]", R.drawable.expression_87);
		mFaceMap.put("[爱你]", R.drawable.expression_88);
		mFaceMap.put("[NO]", R.drawable.expression_89);
		mFaceMap.put("[OK]", R.drawable.expression_90);
		mFaceMap.put("[爱情]", R.drawable.expression_91);
		mFaceMap.put("[飞吻]", R.drawable.expression_92);
		mFaceMap.put("[跳跳]", R.drawable.expression_93);
		mFaceMap.put("[发抖]", R.drawable.expression_94);
		mFaceMap.put("[怄火]", R.drawable.expression_95);
		mFaceMap.put("[转圈]", R.drawable.expression_96);
		mFaceMap.put("[磕头]", R.drawable.expression_97);
		mFaceMap.put("[回头]", R.drawable.expression_98);
		mFaceMap.put("[跳绳]", R.drawable.expression_99);
		mFaceMap.put("[投降]", R.drawable.expression_100);
		
		Set<String> keySet = getFaceMap().keySet();
		faceKeys = new ArrayList<String>();
		faceKeys.addAll(keySet);
	}
	
	/**
	 * DQ 2016-03-09 新版本小表情list
	 */
	private List<EaseEmojicon> initEmojiconList(){
		List<EaseEmojicon> emojiconList = new ArrayList<EaseEmojicon>();
		for (String emojiconKeyString : faceKeys) {
			EaseEmojicon emojicon =new EaseEmojicon(mFaceMap.get(emojiconKeyString),emojiconKeyString, Type.NORMAL);
			emojiconList.add(emojicon);
		}
		return emojiconList;
	}
	
	/**
	 * 输入框表情(小)面板专用
	 */
	public List<EaseEmojicon> getEmojiconList() {
		if (emojiconList!=null) {
			return emojiconList;
		}else{
			emojiconList = initEmojiconList();
			return emojiconList;
		}
	}
	
	/************************************************************************************
	 * DQ 2016-03-09 gif表情兔斯基  start
	 */
	private List<EaseEmojicon> initGifTuzkiList(){
		List<EaseEmojicon> emojiconList = new ArrayList<EaseEmojicon>();
		String packageName = mApp.getPackageName();
		for (int i = 0; i < 16; i++) {
			int static_drawableid = mApp.getResources().getIdentifier("tuzki" + i +"_static", "drawable", packageName);
			int gif_drawableid = mApp.getResources().getIdentifier("tuzki" + i, "drawable", packageName);
			EaseEmojicon gifemojicon = new EaseEmojicon(static_drawableid, null, Type.BIG_EXPRESSION);
			gifemojicon.setBigIcon(gif_drawableid);
			gifemojicon.setName("");
			gifemojicon.setIdentityCode("tuzki" + i);
			emojiconList.add(gifemojicon);
		}
		return emojiconList;
	}
	
	//输入框gif表情(兔斯基)面板专用
	private List<EaseEmojicon> getGifTuzkiList() {
		if (gifTuzkiList!=null) {
			return gifTuzkiList;
		}else{
			gifTuzkiList = initGifTuzkiList();
			return gifTuzkiList;
		}
	}
	
	//输入框gif表情(兔斯基)面板专用，最外面一层
	public EaseEmojiconGroupEntity gifTuzkiGroupEntity() {
		EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
	    emojiconGroupEntity.setEmojiconList(getGifTuzkiList());
	    emojiconGroupEntity.setIcon(R.drawable.emotionbar_tuzki_logo);
	    emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
	    return emojiconGroupEntity;
	}
	
	private String getTuzkiNameByIndex(int index){
		switch (index) {
		case 0: return "";
		case 1: return "";
		case 2: return "";
		case 3: return "";
		case 4: return "";
		case 5: return "";
		case 6: return "";
		case 7: return "";
		case 8: return "";
		case 9: return "";
		case 10: return "";
		case 11: return "";
		case 12: return "";
		case 13: return "";
		case 14: return "";
		case 15: return "";
		default: return "";
		}
	}
	
	/**
	 * DQ 2016-03-09 gif表情兔斯基  end
	 * ************************************************************************************/

	
	
	
	/************************************************************************************
	 * DQ 2016-03-11 gif表情 泡泡兵  start
	 */
	private List<EaseEmojicon> initGifPaopaobingList(){
		List<EaseEmojicon> emojiconList = new ArrayList<EaseEmojicon>();
		String packageName = mApp.getPackageName();
		for (int i = 0; i < 16; i++) {
			int static_drawableid = mApp.getResources().getIdentifier("paopaobing" + i +"_static", "drawable", packageName);
			int gif_drawableid = mApp.getResources().getIdentifier("paopaobing" + i, "drawable", packageName);
			EaseEmojicon gifemojicon = new EaseEmojicon(static_drawableid, null, Type.BIG_EXPRESSION);
			gifemojicon.setBigIcon(gif_drawableid);
			gifemojicon.setName(getPaopaobingNameByIndex(i));
			gifemojicon.setIdentityCode("paopaobing" + i);
			emojiconList.add(gifemojicon);
		}
		return emojiconList;
	}
	
	//输入框gif表情(兔斯基)面板专用
	private List<EaseEmojicon> getGifPaopaobingList() {
		if (gifPaopaobingList!=null) {
			return gifPaopaobingList;
		}else{
			gifPaopaobingList = initGifPaopaobingList();
			return gifPaopaobingList;
		}
	}
	
	//输入框gif表情(兔斯基)面板专用，最外面一层
	public EaseEmojiconGroupEntity gifPaopaobingGroupEntity() {
		EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
	    emojiconGroupEntity.setEmojiconList(getGifPaopaobingList());
	    emojiconGroupEntity.setIcon(R.drawable.emotionbar_paopaobing_logo);
	    emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
	    return emojiconGroupEntity;
	}
	
	private String getPaopaobingNameByIndex(int index){
		switch (index) {
		case 0: return "棒棒哒";
		case 1: return "YesSir";
		case 2: return "拜拜";
		case 3: return "哇哈哈";
		case 4: return "打飞机";
		case 5: return "小憩";
		case 6: return "让我静静";
		case 7: return "抠鼻子";
		case 8: return "冒个泡";
		case 9: return "怒了";
		case 10: return "么么哒";
		case 11: return "我来了";
		case 12: return "谢谢";
		case 13: return "药别停";
		case 14: return "做鬼脸";
		case 15: return "吓尿了";
		default: return "";
		}
	}
	
	/**
	 * DQ 2016-03-11 gif表情 泡泡兵   end
	 * ************************************************************************************/
	
	
	
	/************************************************************************************
	 * DQ 2016-03-09 gif表情 暴走漫画 start
	 */
	private List<EaseEmojicon> initGifBaozouList(){
		List<EaseEmojicon> emojiconList = new ArrayList<EaseEmojicon>();
		String packageName = mApp.getPackageName();
		for (int i = 0; i < 16; i++) {
			int static_drawableid = mApp.getResources().getIdentifier("baozou" + i +"_static", "drawable", packageName);
			int gif_drawableid = mApp.getResources().getIdentifier("baozou" + i, "drawable", packageName);
			EaseEmojicon gifemojicon = new EaseEmojicon(static_drawableid, null, Type.BIG_EXPRESSION);
			gifemojicon.setBigIcon(gif_drawableid);
			gifemojicon.setName(getBaozouNameByIndex(i));
			gifemojicon.setIdentityCode("baozou" + i);
			emojiconList.add(gifemojicon);
		}
		return emojiconList;
	}
	
	//输入框gif表情(暴走漫画)面板专用
	private List<EaseEmojicon> getGifBaozouList() {
		if (gifBaozouList!=null) {
			return gifBaozouList;
		}else{
			gifBaozouList = initGifBaozouList();
			return gifBaozouList;
		}
	}
	
	//输入框gif表情(暴走漫画)面板专用，最外面一层
	public EaseEmojiconGroupEntity gifBaozouGroupEntity() {
		EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
	    emojiconGroupEntity.setEmojiconList(getGifBaozouList());
	    emojiconGroupEntity.setIcon(R.drawable.emotionbar_baozou_logo);
	    emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
	    return emojiconGroupEntity;
	}
	
	private String getBaozouNameByIndex(int index){
		switch (index) {
		case 0: return "抠鼻";
		case 1: return "逗我吗";
		case 2: return "内牛满面";
		case 3: return "你咬我啊";
		case 4: return "容朕想想";
		case 5: return "嗨森";
		case 6: return "操场等你";
		case 7: return "呵呵";
		case 8: return "听你的";
		case 9: return "求不骗";
		case 10: return "害羞";
		case 11: return "注定孤独";
		case 12: return "说好幸福";
		case 13: return "有杀气";
		case 14: return "警告你";
		case 15: return "放肆";
		default: return "";
		}
	}
	
	/**
	 * DQ 2016-03-09 gif表情 暴走漫画  end
	 * ************************************************************************************/
	
	
	
	
	
	/************************************************************************************
	 * DQ 2016-03-09 gif表情工作  start
	 */
	private List<EaseEmojicon> initGifWorkList(){
		List<EaseEmojicon> emojiconList = new ArrayList<EaseEmojicon>();
		String packageName = mApp.getPackageName();
		for (int i = 0; i < 16; i++) {
			int static_drawableid = mApp.getResources().getIdentifier("work" + i +"_static", "drawable", packageName);
			int gif_drawableid = mApp.getResources().getIdentifier("work" + i, "drawable", packageName);
			EaseEmojicon gifemojicon = new EaseEmojicon(static_drawableid, null, Type.BIG_EXPRESSION);
			gifemojicon.setBigIcon(gif_drawableid);
			gifemojicon.setName(getWorkNameByIndex(i));
			gifemojicon.setIdentityCode("work" + i);
			emojiconList.add(gifemojicon);
		}
		return emojiconList;
	}
	
	//输入框gif表情(工作)面板专用
	private List<EaseEmojicon> getGifWorkList() {
		if (gifWorkList!=null) {
			return gifWorkList;
		}else{
			gifWorkList = initGifWorkList();
			return gifWorkList;
		}
	}
	
	//输入框gif表情(工作)面板专用，最外面一层
	public EaseEmojiconGroupEntity gifWorkGroupEntity() {
		EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
	    emojiconGroupEntity.setEmojiconList(getGifWorkList());
	    emojiconGroupEntity.setIcon(R.drawable.emotionbar_tuzkiworker_logo);
	    emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
	    return emojiconGroupEntity;
	}
	
	private String getWorkNameByIndex(int index){
		switch (index) {
		case 0: return "再睡会";
		case 1: return "又是周一";
		case 2: return "挤扁了";
		case 3: return "老板歇会";
		case 4: return "别烦我";
		case 5: return "呵呵";
		case 6: return "回去重做";
		case 7: return "给你钱";
		case 8: return "YesSir";
		case 9: return "神烦";
		case 10: return "好困";
		case 11: return "啦啦啦";
		case 12: return "倒霉";
		case 13: return "忙成狗";
		case 14: return "又加班";
		case 15: return "有钱啦";
		default: return "";
		}
	}
	
	/**
	 * DQ 2016-03-09 gif表情工作  end
	 * ************************************************************************************/

	
	
	public CharSequence convertNormalStringToSpannableString(Context mContext,String message) {
		return this.convertNormalStringToSpannableString(mContext, message, 0);
	}
	
	/**
	 * 另外一种方法解析表情
	 * 
	 * @param message
	 *            传入的需要处理的String
	 * @return
	 */
	public CharSequence convertNormalStringToSpannableString(Context mContext,
			String message,int face_item_size) {
		// TODO Auto-generated method stub
		String hackTxt;
		if (message.startsWith("[") && message.endsWith("]")) {
			hackTxt = message + " ";
		} else {
			hackTxt = message;
		}
		SpannableString value = SpannableString.valueOf(hackTxt);

		Matcher localMatcher = EMOTION_URL.matcher(value);
		while (localMatcher.find()) {
			String str2 = localMatcher.group(0);
			int k = localMatcher.start();
			int m = localMatcher.end();
			if (m - k < 8) {
				if (getFaceMap().containsKey(str2)) {
					int face = getFaceMap().get(str2);
					
					if(face_item_size == 0){ //0 表示表情按drawable大小去设置
						
						ImageSpan imageSpan = new ImageSpan(mContext, face);
						String emojiStr = str2;
						SpannableString spannableString = new SpannableString(
								emojiStr);
						spannableString.setSpan(imageSpan, emojiStr.indexOf('['),
							emojiStr.indexOf(']') + 1,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						ImageSpan localImageSpan2 = new ImageSpan(mContext, face);
						value.setSpan(localImageSpan2, k, m,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					} else {
						Bitmap bitmap = BitmapFactory.decodeResource(
								mContext.getResources(), face);
						if (bitmap != null) {
							int rawHeigh = bitmap.getHeight();
							int rawWidth = bitmap.getHeight();
							int newHeight = face_item_size;
							int newWidth = face_item_size;
							// 计算缩放因子
							float heightScale = ((float) newHeight) / rawHeigh;
							float widthScale = ((float) newWidth) / rawWidth;
							// 新建立矩阵
							Matrix matrix = new Matrix();
							matrix.postScale(heightScale, widthScale);

							Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
									rawWidth, rawHeigh, matrix, true);
							
							ImageSpan imageSpan = new ImageSpan(mContext,
									newBitmap);
							bitmap.recycle();

							value.setSpan(imageSpan, k, m,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}
			}
		}
		return value;
	}


	public static void extractMention2Link(TextView v) {

		Pattern mentionsPattern = Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
	
		Linkify.addLinks(v, mentionsPattern, "http://", null,null);

		Pattern trendsPattern = Pattern.compile("\\d{3}-\\d{8}|\\d{3}-\\d{7}|\\d{4}-\\d{8}|\\d{4}-\\d{7}|1+[358]+\\d{9}|\\d{8}|\\d{7}");
	
		Linkify.addLinks(v, trendsPattern, "tel:", null,null);

	}

}
