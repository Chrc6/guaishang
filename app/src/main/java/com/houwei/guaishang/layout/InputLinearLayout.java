package com.houwei.guaishang.layout;

import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.easemob.EaseEmojicon;
import com.houwei.guaishang.easemob.EaseEmojiconGroupEntity;
import com.houwei.guaishang.easemob.EaseEmojiconMenu;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class InputLinearLayout extends LinearLayout {
	private SendClickListener onSendClickListener;
	private EmotionEditText chat_et;
	private BaseActivity context;
	private FrameLayout emojiconMenuContainer;
	  private Handler handler = new Handler();
	
	public InputLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context)
				.inflate(R.layout.review_layout, this, true);
	}

	public InputLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context)
				.inflate(R.layout.review_layout, this, true);
	}

	public void initView(final BaseActivity context, final boolean isChat,
			final SendClickListener onSendClickListener) {
		// TODO Auto-generated method stub
		this.context = context;

		final Button send_btn = (Button) findViewById(R.id.send_btn);
		final ImageButton add_btn = (ImageButton) findViewById(R.id.add_btn);

		if (!isChat) {
			send_btn.setVisibility(View.VISIBLE);
			add_btn.setVisibility(View.GONE);
		}

		final ImageButton face_btn = (ImageButton) findViewById(R.id.face_btn);
		chat_et = (EmotionEditText) findViewById(R.id.chat_et);

		emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
		// 表情栏，只添加小表情
		EaseEmojiconMenu emojiconMenu = (EaseEmojiconMenu) LayoutInflater.from(
				context).inflate(R.layout.ease_layout_emojicon_menu, null);
		List<EaseEmojiconGroupEntity> emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();

		emojiconGroupList.add(new EaseEmojiconGroupEntity(
				R.drawable.expression_1, context.getITopicApplication()
						.getFaceManager().getEmojiconList()));
		((EaseEmojiconMenu) emojiconMenu).init(emojiconGroupList);
		emojiconMenuContainer.addView(emojiconMenu);


		emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenu.EaseEmojiconMenuListener() {
			
			@Override
			public void onExpressionClicked(EaseEmojicon emojicon) {
				// TODO Auto-generated method stub
				chat_et.insertEmotion(context, emojicon);
			}
			
			@Override
			public void onDeleteImageClicked() {
				// TODO Auto-generated method stub
				chat_et.onEmojiconDeleteEvent();
			}
		});
		
		
		// 监听文字框
		chat_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!isChat) {
					return;
				}
				if (!TextUtils.isEmpty(s)) {
					add_btn.setVisibility(View.GONE);
					send_btn.setVisibility(View.VISIBLE);
				} else {
					add_btn.setVisibility(View.VISIBLE);
					send_btn.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		chat_et.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				context.showKeyboard(chat_et);
				emojiconMenuContainer.setVisibility(View.GONE);
				face_btn.setImageResource(R.drawable.message_face);
				return false;
			}
		});

		face_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				if (emojiconMenuContainer.getVisibility() != View.VISIBLE) {
					context.hideKeyboard();
					handler.postDelayed(new Runnable() {
			                public void run() {
			                	emojiconMenuContainer.setVisibility(View.VISIBLE);
								((ImageButton) v)
										.setImageResource(R.drawable.message_keyboard);
			                }
			            }, 90);
				} else {
					context.showKeyboard(chat_et);
					emojiconMenuContainer.setVisibility(View.GONE);
					((ImageButton) v).setImageResource(R.drawable.message_face);
				}
			}
		});
		send_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onSendClickListener != null) {
					onSendClickListener.onSendClick(chat_et.getText()
							.toString());
				}
			}
		});
	}

	public void hideKeyboardAndEmoji() {
		context.hideKeyboard();
		emojiconMenuContainer.setVisibility(View.GONE);
		final ImageButton face_btn = (ImageButton) findViewById(R.id.face_btn);
		face_btn.setImageResource(R.drawable.message_face);
	}

	public void clear() {
		chat_et.setText("");
	}

	public String getInputText() {
		return chat_et.getText().toString();
	}

	public void setHint(String hint) {
		chat_et.setHint(hint);
	}

	public EditText getEditText() {
		return chat_et;
	}

	public void setOnSendClickListener(SendClickListener onSendClickListener) {
		this.onSendClickListener = onSendClickListener;
	}

	public interface SendClickListener {
		public void onSendClick(String content);
	}
}
