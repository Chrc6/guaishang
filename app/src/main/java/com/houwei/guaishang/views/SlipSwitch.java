package com.houwei.guaishang.views;



import com.houwei.guaishang.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SlipSwitch extends View implements OnTouchListener {

	private Bitmap switch_on_Bkg, switch_off_Bkg, slip_Btn;
	private Rect on_Rect, off_Rect;

	// 鏄惁姝ｅ湪婊戝姩
	private boolean isSlipping = false;

	private boolean isSwitchOn = false;

	// 鎵嬫寚鎸変笅鏃剁殑姘村钩鍧愭爣X锛屽綋鍓嶇殑姘村钩鍧愭爣X
	private float previousX, currentX;

	private OnSwitchListener onSwitchListener;
	// 鏄惁璁剧疆浜嗗紑鍏崇洃鍚櫒
	private boolean isSwitchListenerOn = false;
	// 婊戝潡鐨刴arginTop
	private float slip_top;

	public SlipSwitch(Context context) {
		super(context);
		init();
	}

	public SlipSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setImageResource(R.drawable.togglebutton_no,
				R.drawable.togglebutton_off, R.drawable.toggle_btn);
		setOnTouchListener(this);
	}

	protected void setImageResource(int switchOnBkg, int switchOffBkg,
			int slipBtn) {
		switch_on_Bkg = BitmapFactory.decodeResource(getResources(),
				switchOnBkg);
		switch_off_Bkg = BitmapFactory.decodeResource(getResources(),
				switchOffBkg);
		slip_Btn = BitmapFactory.decodeResource(getResources(), slipBtn);

		// 鍙冲崐杈筊ect锛屽嵆婊戝姩鎸夐挳鍦ㄥ彸鍗婅竟鏃惰〃绀哄紑鍏冲紑锟�
		on_Rect = new Rect(switch_off_Bkg.getWidth() - slip_Btn.getWidth(), 0,
				switch_off_Bkg.getWidth(), slip_Btn.getHeight());
		// 宸﹀崐杈筊ect锛屽嵆婊戝姩鎸夐挳鍦ㄥ乏鍗婅竟鏃惰〃绀哄紑鍏冲叧锟�
		off_Rect = new Rect(0, 0, slip_Btn.getWidth(), slip_Btn.getHeight());

		slip_top = (switch_on_Bkg.getHeight() - slip_Btn.getHeight()) / 2;
	}

	public void setSwitchState(boolean switchState) {
		isSwitchOn = switchState;
	}

	public boolean getSwitchState() {
		return isSwitchOn;
	}

	protected void updateSwitchState(boolean switchState) {
		isSwitchOn = switchState;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		// 婊戝姩鎸夐挳鐨勫乏杈瑰潗锟�
		float left_SlipBtn;

		// 鎵嬫寚婊戝姩鍒板乏鍗婅竟鐨勬椂鍊欒〃绀哄紑鍏充负鍏抽棴鐘讹拷?锛屾粦鍔ㄥ埌鍙冲崐杈圭殑鏃讹拷?琛ㄧず锟�锟斤拷涓哄紑鍚姸锟�

		// 鍒ゆ柇褰撳墠鏄惁姝ｅ湪婊戝姩
		if (isSlipping) {
			if (currentX > switch_on_Bkg.getWidth()) {
				left_SlipBtn = switch_on_Bkg.getWidth() - slip_Btn.getWidth();
			} else {
				left_SlipBtn = currentX - slip_Btn.getWidth() / 2;
			}
			if (currentX < (switch_on_Bkg.getWidth() / 2)) {
				canvas.drawBitmap(switch_off_Bkg, matrix, paint);
			} else {
				canvas.drawBitmap(switch_on_Bkg, matrix, paint);
			}
		} else {
			// 鏍规嵁褰撳墠鐨勫紑鍏崇姸鎬佽缃粦鍔ㄦ寜閽殑浣嶇疆
			if (isSwitchOn) {
				left_SlipBtn = on_Rect.left;
				canvas.drawBitmap(switch_on_Bkg, matrix, paint);
			} else {
				left_SlipBtn = off_Rect.left;
				canvas.drawBitmap(switch_off_Bkg, matrix, paint);
			}
		}

		// 瀵规粦鍔ㄦ寜閽殑浣嶇疆杩涜寮傚父鍒ゆ柇
		if (left_SlipBtn < 0) {
			left_SlipBtn = 0;
		} else if (left_SlipBtn > switch_on_Bkg.getWidth()
				- slip_Btn.getWidth()) {
			left_SlipBtn = switch_on_Bkg.getWidth() - slip_Btn.getWidth();
		}

		canvas.drawBitmap(slip_Btn, left_SlipBtn, slip_top, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(switch_on_Bkg.getWidth(),
				switch_on_Bkg.getHeight());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		// 婊戝姩
		case MotionEvent.ACTION_MOVE:
			currentX = event.getX();
			break;

		// 鎸変笅
		case MotionEvent.ACTION_DOWN:
			if (event.getX() > switch_on_Bkg.getWidth()
					|| event.getY() > switch_on_Bkg.getHeight()) {
				return true;
			}

			isSlipping = true;
			previousX = event.getX();
			currentX = previousX;
			break;

		// 鏉惧紑
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			isSlipping = false;
			// 鏉惧紑鍓嶅紑鍏崇殑鐘讹拷?
			boolean previousSwitchState = isSwitchOn;

			if (event.getX() >= (switch_on_Bkg.getWidth() / 2)) {
				isSwitchOn = true;
			} else {
				isSwitchOn = false;
			}
			
			// 濡傛灉璁剧疆浜嗙洃鍚櫒锛屽垯璋冪敤姝ゆ柟锟�
			if (isSwitchListenerOn && (previousSwitchState != isSwitchOn)) {
				onSwitchListener.onSwitched(isSwitchOn);
			}
			break;

		default:
			break;
		}

		// 閲嶆柊缁樺埗鎺т欢
		invalidate();
		return true;
	}

	public void setOnSwitchListener(OnSwitchListener listener) {
		onSwitchListener = listener;
		isSwitchListenerOn = true;
	}

	public interface OnSwitchListener {
		abstract void onSwitched(boolean isSwitchOn);
	}

	public void setSwitchOn(boolean isSwitchOn) {
		this.isSwitchOn = isSwitchOn;
		invalidate();
	}

	public void triggerSwittch() {

		isSwitchOn = !isSwitchOn;
		isSlipping  = false;
		invalidate();
		final OnSwitchListener listener = getOnSwitchListener();
		if (listener != null) {
			onSwitchListener.onSwitched(isSwitchOn);
		}
	}

	public OnSwitchListener getOnSwitchListener() {
		// TODO Auto-generated method stub
		return onSwitchListener;
	}
}
