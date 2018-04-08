package com.houwei.guaishang.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.manager.FollowManager;


public class ValueUtil {

	
	//不限
	public static final String UNLIMIT_ID = "-1";
	public static final int UNLIMIT_ID_INT = -1;
	//未填
	public static final String EMPTY_ID = "0";
	public static final int EMPTY_ID_INT = 0;
	
	private static final int DAY = 24 * 60 * 60;// 天
	private static final int HOUR = 60 * 60;// 小时
	private static final int MINUTE = 60;// 分钟

	public static final int SEX_NONE = 0;// 性别未填
	public static final int SEX_MALE = 1;//  男
	public static final int SEX_FEMALE = 2;// 女
	 
	
	public static String getRelationTypeString(int relationType) {
		String relationStr = "关注"; //0 未关注
		switch (relationType) {
		case FollowManager.MY_FANS: //2 TA关注我
			relationStr = "TA关注我";
			break;
		case FollowManager.MY_FOLLOWING: //1 已关注
			relationStr = "已关注";
			break;
		case FollowManager.EACH: //3 互相关注
			relationStr = "互相关注";
			break;
		default:
			break;
		}
		return relationStr;
	}

	public static int getRelationTypeDrawable(int relationType) {
		int drawable = R.drawable.orange_round_background;
		switch (relationType) {
		case FollowManager.MY_FANS:
			 drawable = R.drawable.orange_round_background;
			break;
		case FollowManager.MY_FOLLOWING:
			 drawable = R.drawable.orange_round_background;
			break;
		case FollowManager.EACH:
			drawable = R.drawable.orange_round_background;
			break;
		default:
			break;
		}
		return drawable;
	}

	public static int getRelationTypeColor(Context mContext,int relationType) {
		int color = mContext.getResources().getColor(R.color.blue_dark_color);
		switch (relationType) {
		
		case FollowManager.MY_FOLLOWING:
			
		case FollowManager.EACH:
			color = mContext.getResources().getColor(R.color.text_gray_color);
		default:
			break;
		}
		return color;
	}

	
	
	
	public static String getRelationTypeStringSimple(int relationType) {
		String relationStr = ""; //0 未关注
		switch (relationType) {
		case FollowManager.MY_FANS: //2 TA关注我
			relationStr = "";
			break;
		case FollowManager.MY_FOLLOWING: //1 已关注
			relationStr = "";
			break;
		case FollowManager.EACH: //3 互相关注
			relationStr = "";
			break;
		default:
			break;
		}
		return relationStr;
	}

	public static int getRelationTypeDrawableSimple(int relationType) {
		int drawable = R.mipmap.attention_un1;
		switch (relationType) {
		case FollowManager.MY_FANS:
			 drawable = R.mipmap.attention_un1;
			break;
		case FollowManager.MY_FOLLOWING:
			 drawable = R.mipmap.attenttion1;
			break;
		case FollowManager.EACH:
			drawable =  R.mipmap.attenttion1;
			break;
		default:
			break;
		}
		return drawable;
	}
	
	public static int getRelationTextColorSimple(int relationType) {
		int relationStr = 0xffffffff; //0 未关注
		switch (relationType) {
		case FollowManager.MY_FANS: //2 TA关注我
			relationStr = 0xffffffff;
			break;
		case FollowManager.MY_FOLLOWING: //1 已关注
			relationStr = 0xff5B5B5B;
			break;
		case FollowManager.EACH: //3 互相关注
			relationStr = 0xff5B5B5B;
			break;
		default:
			break;
		}
		return relationStr;
	}
	
	
	public static String getNamesFromNameIDBeanList(List<NameIDBean> totalList){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < totalList.size(); i++) {
			sb.append(totalList.get(i).getName());
			if (i != totalList.size() - 1) {
				sb.append("，");
			}
		}
		return sb.toString();
	}
	
	public static String getIdsFromNameIDBeanList(List<NameIDBean> totalList){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < totalList.size(); i++) {
			sb.append(totalList.get(i).getId());
			if (i != totalList.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static List<String> getIdListFromNameIDBeanList(List<NameIDBean> totalList){
		 List<String> idlist = new ArrayList<String>();
		for (int i = 0; i < totalList.size(); i++) {
			idlist.add(totalList.get(i).getId());
		}
		return idlist;
	}
	

	public static List<String> StringToArrayList(String str) {
		ArrayList<String> investorFoucusDirectionList = new ArrayList<String>();
		if (str != null) {
			String[] zu = str.split("\\,");
			for (int i = 0; i < zu.length; i++) {
				if (zu[i].equals("")) {
					continue;
				}
				investorFoucusDirectionList.add(zu[i]);
			}
		}
		return investorFoucusDirectionList;
	}
	
	public static String ArrayListToString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i != list.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static long getTimeLong(String sTime) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(sTime);

		return date.getTime();
	}

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * 
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getTimeStringFromNow(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > DAY) {// 1天以上
			timeStr = getSimpleDate(new Date(timestamp));
		} else if (timeGap > HOUR) {// 1小时-24小时
			timeStr = timeGap / HOUR + "小时前";
		} else if (timeGap > MINUTE) {// 1分钟-59分钟
			timeStr = timeGap / MINUTE + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public static String getTimeStringFromEndTime(long endtime) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (endtime - currentTime) / 1000;// 与现在时间相差秒数
		String timeStr = null;
		 if (timeGap > DAY) {// 1天以上
			timeStr = ""+timeGap / DAY ;
		}  else {// 1秒钟-59秒钟
			timeStr = "1";
		}
		return timeStr;
	}
	
	public static String getSimpleDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(date);
		return time;
	}

	
	/**
	 * 得到格式化后的系统时间 精确到秒
	 * 
	 * @param date
	 * @return
	 */
	public static String getSimpleTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(date);
		return time;
	}

	/**
	 * 得到格式化后的系统时间 精确到秒
	 * 
	 * @param date
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Date getSimpleData(String datestr)
			throws java.text.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = sdf.parse(datestr);
		return time;
	}

	
	public static void setSexAndAge(Context mContext,TextView header_sex_age_tv,String sexCode,String age)
	{
		if (age.equals("0")||age.equals("")) {
			header_sex_age_tv.setText("");
		}else{
			header_sex_age_tv.setText(""+age);
		}
		if ("1".equals(sexCode)) {
			Drawable drawable1 = mContext.getResources().getDrawable(
					R.drawable.male_icon);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			
			header_sex_age_tv.setBackgroundResource(R.drawable.male_round);
			header_sex_age_tv.setCompoundDrawables(drawable1, null, null, null);
		}else if ("2".equals(sexCode)) {
			Drawable drawable1 = mContext.getResources().getDrawable(
					R.drawable.female_icon);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			header_sex_age_tv.setBackgroundResource(R.drawable.female_round);
			header_sex_age_tv.setCompoundDrawables(drawable1, null, null, null);
		}else {
			header_sex_age_tv.setBackgroundColor(0x00000000);
			header_sex_age_tv.setCompoundDrawables(null, null, null, null);
		}
		
	}
	
	/**
	 * 性别 start
	 */
	public static String getSexString(String sexCode){
		String sexString = "未填";
		if (UNLIMIT_ID.equals(sexCode)) {
			sexString =  "不限";
		}else if ("1".equals(sexCode)) {
			sexString =  "男";
		}else if ("2".equals(sexCode)) {
			sexString =  "女";
		}
		return sexString;
	}
	
	/**
	 * 性别 start
	 */
	public static String getSexString(String sexCode,String defaultString){
		String sexString = defaultString;
		if ("1".equals(sexCode)) {
			sexString =  "男";
		}else if ("2".equals(sexCode)) {
			sexString =  "女";
		}
		return sexString;
	}
	
	public static  List<NameIDBean> getSexTypeList() {
		List<NameIDBean> sortTypeList = new ArrayList<NameIDBean>();
		NameIDBean bean = new NameIDBean("1", "男");
		sortTypeList.add(bean);
		bean = new NameIDBean("2", "女");
		sortTypeList.add(bean);
		return sortTypeList;
	}
	
	public static  List<NameIDBean> getSexRequireList() {
		List<NameIDBean> sortTypeList = new ArrayList<NameIDBean>();
		NameIDBean bean = new NameIDBean(UNLIMIT_ID, "不限");
		sortTypeList.add(bean);
		bean = new NameIDBean("1", "男");
		sortTypeList.add(bean);
		bean = new NameIDBean("2", "女");
		sortTypeList.add(bean);
		return sortTypeList;
	}
	
	/**
	 * 性别end
	 */
	

	
	
	
	public static ArrayList<String> getPersonalTags(){
		ArrayList<String> tagList = new ArrayList<String>();
		tagList.add("90后");
		tagList.add("80后");
		tagList.add("程序猿");
		tagList.add("大学生");
		tagList.add("设计师");
		tagList.add("技术宅");
		tagList.add("单身");
		tagList.add("CEO");
		return tagList;
	}
	
	
	public static void sendEmail(Context mContext,String hisEmailAddress,String title,String message){
		Intent it = new Intent(Intent.ACTION_SEND);     
		String[] tos={hisEmailAddress};       
		it.putExtra(Intent.EXTRA_EMAIL, tos);       
		it.putExtra(Intent.EXTRA_TEXT, message);     
		it.putExtra(Intent.EXTRA_SUBJECT, title);       
		it.setType("text/plain");     		
		it.setType("message/rfc822");       
		mContext.startActivity(Intent.createChooser(it, "Choose Email Client"));   
	}
	

	
	public static void resetLayoutParams(Context mContext, View iv,
			TopicBean bean) {
		int max_photo_width = (int) mContext.getResources().getDimension(R.dimen.max_photo_width);
		if (bean.getPhotoOriginalHeight() == 0) {
			bean.setPhotoOriginalHeight(max_photo_width);
		}
		if (bean.getPhotoOriginalWidth() == 0) {
			bean.setPhotoOriginalWidth(max_photo_width);
		}
		if (bean.getPhotoOriginalHeight() < bean.getPhotoOriginalWidth()) {
			// 图片很扁
			// 用这句代码，如果图片很小（比MAX框还小），则保持原图大小
//			iv.getLayoutParams().width = bean.getPhotoOriginalWidth() > max_photo_width ? max_photo_width
//					: bean.getPhotoOriginalWidth();
			
			// 用这句代码，如果图片很小（比MAX框还小），则拉伸模糊到MAX框的宽度
			iv.getLayoutParams().width = max_photo_width;
			
			iv.getLayoutParams().height = (bean.getPhotoOriginalHeight() * iv
					.getLayoutParams().width) / bean.getPhotoOriginalWidth();
		} else {
			// 图片很高
			// 用这句代码，如果图片很小（比MAX框还小），则保持原图大小
//			iv.getLayoutParams().height = bean.getPhotoOriginalHeight() > max_photo_width ? max_photo_width
//					: bean.getPhotoOriginalHeight();
			
			// 用这句代码，如果图片很小（比MAX框还小），则拉伸模糊到MAX框的宽度
			iv.getLayoutParams().height = max_photo_width;
			
			iv.getLayoutParams().width = (bean.getPhotoOriginalWidth() * iv
					.getLayoutParams().height) / bean.getPhotoOriginalHeight();
		}
	}

	public static void resetLayoutParams(Activity mContext, View iv){
		WindowManager wm = mContext.getWindowManager();
		//iv.getLayoutParams().height
		int height = wm.getDefaultDisplay().getWidth() - (int)(2* mContext.getResources().getDimension(R.dimen.topic_gridphoto_leftmargin));
		iv.getLayoutParams().height = 36 * height / 65;
	}
	
	public static int getIntFromStringWithCatch(String idString){
		int intvalue = 0;
		try {
			intvalue = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return intvalue;
	}
}
