package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;
import com.houwei.guaishang.easemob.EaseEmojicon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class EmotionEditText extends EditText {

	public EmotionEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public EmotionEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	
	public EmotionEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	 /**
     * 表情写入
     */
	public void insertEmotion(Context context,EaseEmojicon emojicon){
		int selectionStart = getSelectionStart();// 获取光标的位置
		
		Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), emojicon.getIcon());
		
		int edittext_emojicon_size = (int)context.getResources().getDimension(R.dimen.edittext_emojicon_size);
		
		if (bitmap != null) {
			int rawHeigh = bitmap.getHeight();
			int rawWidth = bitmap.getHeight();
			int newHeight = edittext_emojicon_size;
			int newWidth = edittext_emojicon_size;
			// 计算缩放因子
			float heightScale = ((float) newHeight) / rawHeigh;
			float widthScale = ((float) newWidth) / rawWidth;
			// 新建立矩阵
			Matrix matrix = new Matrix();
			matrix.postScale(heightScale, widthScale);

			Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					rawWidth, rawHeigh, matrix, true);
			
			ImageSpan imageSpan = new ImageSpan(context,newBitmap);
			bitmap.recycle();
			
			String emojiStr = emojicon.getEmojiText();
			SpannableString spannableString = new SpannableString(emojiStr);
			spannableString.setSpan(imageSpan, emojiStr.indexOf('['),
					emojiStr.indexOf(']') + 1,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			getEditableText().insert(
					selectionStart > 0 ? selectionStart : 0,spannableString);
		}
	}
	
	 /**
     * 表情删除
     */
    public void onEmojiconDeleteEvent(){
    	int selection = getSelectionStart();
		String text = getText().toString();
		if (selection > 0) {
			String tempStartString = text.substring(0, selection);
			if (tempStartString.endsWith("]")) {
				int start = tempStartString.lastIndexOf("[");
				int end = selection;
				getText().delete(start, end);
			} else {
				getText().delete(selection - 1,selection);
			}
		}
    }
}
