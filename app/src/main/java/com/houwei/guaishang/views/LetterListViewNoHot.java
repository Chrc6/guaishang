package com.houwei.guaishang.views;

import com.houwei.guaishang.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class LetterListViewNoHot extends View {
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private String[] b = { "热", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBkg = false;
	private int letter_textsize ;
	public LetterListViewNoHot(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LetterListViewNoHot(Context context, AttributeSet attrs) {
		super(context, attrs);
		letter_textsize = (int) context.getResources().getDimension(R.dimen.letter_textsize);
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.Letter);
		if (!a.getBoolean(R.styleable.Letter_hasHot, true)) {
			b[0] = "";
		}
		a.recycle();
	}

	public LetterListViewNoHot(Context context) {
		super(context);
		letter_textsize = (int) context.getResources().getDimension(R.dimen.letter_textsize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int height = getHeight();
		int width = getWidth();
		height = height - height / b.length;
		int singleHeight = height / b.length;
	
		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#333333"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(letter_textsize);
			if (i == choose) {
				paint.setColor(Color.parseColor("#2BA9DC"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight / 4 + singleHeight;

			canvas.drawText(b[i], xPos, yPos, paint);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < b.length) {
					listener.onTouchingLetterChanged(b[c]);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < b.length) {
					listener.onTouchingLetterChanged(b[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}
}
