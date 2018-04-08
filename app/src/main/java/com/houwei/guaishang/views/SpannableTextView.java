package com.houwei.guaishang.views;


import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentBean;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SpannableTextView extends TextView {
	private boolean dontConsumeNonUrlClicks = true;
	private boolean linkHit;

	public SpannableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}
	
	public void setCommentItem(CommentBean commentBean,MemberClickListener onMemberClickListener,MemberClickListener onReviewMemberClickListener){
		setText("");
		append(initCellSpanString(0xff5D7CA9,
				""+commentBean.getMemberName(),commentBean,onMemberClickListener));
		if (commentBean.getToMemberId() != null && !commentBean.getToMemberId().equals("0")) {
			append("回复");
			//有回复的人
			append(initCellSpanString(0xff5D7CA9,
					""+commentBean.getToMemberName(),commentBean,onReviewMemberClickListener));
			
		}
		append("：");
		setMovementMethod(new LocalLinkMovementMethod(null));
	}

	public void setCommentText(CommentBean commentBean,MemberClickListener onMemberClickListener){
		setText("");
		if (commentBean.getToMemberId() != null && !commentBean.getToMemberId().equals("0")
				&& !commentBean.getToMemberId().equals("")) {
			append("回复");
			//有回复的人
			append(initCellSpanString(getResources().getColor(R.color.blue_color),
					"@"+commentBean.getToMemberName(),commentBean,onMemberClickListener));
			append("：");
		}
		setMovementMethod(new LocalLinkMovementMethod(null));

	}
	
	public void setPraiseText(List<CommentBean> praiseList, MemberClickListener onMemberClickListener){
		setText("");
		if(praiseList == null){
			return;
		}
		int size = praiseList.size();
		for (int i = 0; i < size; i++) {
			CommentBean commentBean  = praiseList.get(i);
			if (commentBean.getMemberName() == null) {
				continue;
			}
			append(initCellSpanString(getResources().getColor(R.color.blue_color),
					commentBean.getMemberName(),commentBean,onMemberClickListener));
			if (i != size-1) {
				append("，");
			}
		}
		setMovementMethod(new LocalLinkMovementMethod(null));
	}
	


	private SpannableString initCellSpanString(final int color, String content,
			final CommentBean commentBean,
			final MemberClickListener onMemberClickListener) {
		SpannableString spanText = new SpannableString(content);

		CharacterStyle span;
		if (onMemberClickListener == null) {
			span = new ForegroundColorSpan(color);
		} else {
			span = new ClickableSpan() {

				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					onMemberClickListener.onMemberClick(commentBean);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					// TODO Auto-generated method stub
					ds.setColor(color);
					ds.setUnderlineText(false);
					ds.setFakeBoldText(false);
				}
			};
		}
		spanText.setSpan(span, 0, content.length() ,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanText;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		linkHit = false;
		boolean res = super.onTouchEvent(event);

		if (dontConsumeNonUrlClicks)
			return linkHit;
		return res;

	}

	public class LocalLinkMovementMethod extends LinkMovementMethod {
	
		private  BodyClickListener onBodyClickListener;
		public  LocalLinkMovementMethod (BodyClickListener onBodyClickListener) {
			this.onBodyClickListener = onBodyClickListener;
		}

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer,
				MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off,
						ClickableSpan.class);
				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}

					if (widget instanceof SpannableTextView) {
						((SpannableTextView) widget).linkHit = true;
					}
					return true;
				} else {
					Selection.removeSelection(buffer);
					Touch.onTouchEvent(widget, buffer, event);
					if (action == MotionEvent.ACTION_UP && onBodyClickListener!=null) {
						onBodyClickListener.onBodyClick();
					}
					return false;
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}
	
	public interface BodyClickListener{
		public void  onBodyClick();
	}
	
	public interface MemberClickListener{
		public void  onMemberClick(CommentBean commentBean);
	}
}
