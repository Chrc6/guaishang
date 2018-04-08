package com.houwei.guaishang.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.easemob.EaseEmojicon;
import com.houwei.guaishang.easemob.EaseEmojiconGroupEntity;
import com.houwei.guaishang.easemob.EaseEmojiconMenu;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.manager.MyLocationManager.LocationListener;
import com.houwei.guaishang.tools.FileSizeUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.view.SelectDialog;
import com.houwei.guaishang.views.AnimationYoYo;
import com.houwei.guaishang.views.InputControlEditText;
import com.houwei.guaishang.views.InputControlEditText.GetInputLengthListener;
import com.houwei.guaishang.views.InputControlEditText.InputLengthHintListener;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

//发布动态
public class VideoReleaseActivity extends BaseActivity implements
		InputLengthHintListener, GetInputLengthListener, LocationListener   {

	public static final String SAVEPATH = Environment
			.getExternalStorageDirectory().getPath() + "/" + "video";

	/** 从媒体库选择视频 */
	public static final int VIDEO_PICKED_WITH_DATA = 0x105;

	/** 现录视频 */
	public static final int VIDEO_RECORD_WITH_DATA = 0x106;

	public final static int RELEASE_SUCCESS = 0x23;
	private InputControlEditText content_et;

	private ImageView imageview, play_imageview;
	private TextView showlength_tv, video_remark_tv;
	private final int maxlength = 300;

	private CheckBox location_checkbox;
	private LocationBean currentLocationBean; //经纬度位置信息
	
	private EditText price_et;
	
	private String coverImagePath, videoPath;
	private Button delete_btn;

	private ImageView face_btn;
	private FrameLayout emojiconMenuContainer;
	private MyHandler handler = new MyHandler(this);
	private RecyclerView recyclerView;

	public static final int IMAGE_ITEM_ADD = -1;
	public static final int REQUEST_CODE_SELECT = 100;
	public static final int REQUEST_CODE_PREVIEW = 101;

	private ArrayList<ImageItem> selImageList; //当前选择的所有图片
	private int maxImgCount = 9;
	ArrayList<ImageItem> images = null;//允许选择图片最大数
	private String userid;

	private static class MyHandler extends Handler {

		private WeakReference<Context> reference;

		public MyHandler(Context context) {
			reference = new WeakReference<Context>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			final VideoReleaseActivity activity = (VideoReleaseActivity) reference.get();
			if (activity == null) {
				return;
			}
			activity.progress.dismiss();

			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				//上传封面成功
				StringResponse retMap = (StringResponse) msg.obj;
				if (retMap.isSuccess()) {
					activity.uploadVideo(retMap.getTag(), retMap.getData());
				} else {
					activity.showErrorToast(retMap.getMessage());
				}
				break;
			default:
				activity.showErrorToast("发布失败");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_release);
		userid=getITopicApplication().getMyUserBeanManager().getUserId();
		initView();
		initListener();
		int type=getIntent().getIntExtra("type",1);
		startPhoto(type);
	}

	protected void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		location_checkbox = (CheckBox)findViewById(R.id.location_checkbox);
		recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
		content_et = (InputControlEditText) findViewById(R.id.content_et);
		showlength_tv = (TextView) findViewById(R.id.showLength);
		video_remark_tv = (TextView) findViewById(R.id.video_remark_tv);
		imageview = (ImageView) findViewById(R.id.imageview);
		play_imageview = (ImageView) findViewById(R.id.play_imageview);
		delete_btn = (Button) findViewById(R.id.delete_btn);
		price_et = (EditText) findViewById(R.id.price_et);
		face_btn = (ImageView) findViewById(R.id.face_btn);

		content_et.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					// doneClick();
					return true;
				}
				return false;
			}
		});

		content_et.setOnMaxInputListener(maxlength, this);

		content_et.setOnGetInputLengthListener(this);

		setShowLength(maxlength);

		emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
		// 表情栏，只添加小表情
		EaseEmojiconMenu emojiconMenu = (EaseEmojiconMenu) LayoutInflater.from(
				this).inflate(R.layout.ease_layout_emojicon_menu, null);
		List<EaseEmojiconGroupEntity> emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();

		emojiconGroupList.add(new EaseEmojiconGroupEntity(
				R.drawable.expression_1, getITopicApplication()
						.getFaceManager().getEmojiconList()));
		((EaseEmojiconMenu) emojiconMenu).init(emojiconGroupList);
		emojiconMenuContainer.addView(emojiconMenu);

		emojiconMenu
				.setEmojiconMenuListener(new EaseEmojiconMenu.EaseEmojiconMenuListener() {

					@Override
					public void onExpressionClicked(EaseEmojicon emojicon) {
						// TODO Auto-generated method stub
						content_et.insertEmotion(VideoReleaseActivity.this,
								emojicon);
					}

					@Override
					public void onDeleteImageClicked() {
						// TODO Auto-generated method stub
						content_et.onEmojiconDeleteEvent();
					}
				});

		videoPath = getIntent().getStringExtra("videoPath");
		coverImagePath = getIntent().getStringExtra("coverImagePath");
		if (videoPath != null && coverImagePath != null) {
			ImageLoader.getInstance().displayImage("file://" + coverImagePath,imageview);
			play_imageview.setVisibility(View.VISIBLE);
			delete_btn.setVisibility(View.VISIBLE);

			video_remark_tv.setText("当前选择视频大小："
					+ FileSizeUtil.getAutoFileOrFilesSize(videoPath));

		}

		Handler hander = new Handler();
		hander.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showKeyboard(content_et);
			}
		}, 150);
		initRecy();
	}

	private void initRecy() {
//		selImageList = new ArrayList<>();
//		adapter = new ImagePickerTwoAdapter(this, selImageList, maxImgCount);
//		adapter.setOnItemClickListener(this);
//
//		recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//		recyclerView.setHasFixedSize(true);
//		recyclerView.setAdapter(adapter);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		getITopicApplication().getLocationManager().addLocationListener(this);
		getITopicApplication().getLocationManager().startLoction(false);
		
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});

		findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkLogined()) {
							doneClick();
						}
					}
				});

		delete_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				videoPath = null;

				if (coverImagePath != null) {
					File thumbnailPhoto = new File(coverImagePath);
					if (thumbnailPhoto.exists()) {
						thumbnailPhoto.delete();
					}
					coverImagePath = null;
				}

				imageview.setImageResource(R.drawable.picture_update_icon);
				play_imageview.setVisibility(View.GONE);
				delete_btn.setVisibility(View.GONE);

				video_remark_tv.setText("最少且只能上传一个视频");
			}
		});

		face_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				if (emojiconMenuContainer.getVisibility() != View.VISIBLE) {
					//为了让content_et出现在屏幕中
					content_et.requestFocus();
					
					//隐藏键盘
					hideKeyboard();
					
					
			           handler.postDelayed(new Runnable() {
			                public void run() {
			                	emojiconMenuContainer.setVisibility(View.VISIBLE);
								((ImageView) v)
										.setImageResource(R.drawable.compose_keyboardbutton_background_highlighted);
			                }
			            }, 30);
//					
				} else {
					showKeyboard(content_et);
					emojiconMenuContainer.setVisibility(View.GONE);
					((ImageView) v)
							.setImageResource(R.drawable.compose_emoticonbutton_background_highlighted);
				}
			}
		});

		imageview.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (videoPath != null) {
					// 当前选了视频
					Uri uri = Uri.parse(videoPath);
					// 调用系统自带的播放器
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, "video/mp4");
					startActivity(intent);
					return;
				}

				MenuTwoButtonDialog dialog = new MenuTwoButtonDialog(
						VideoReleaseActivity.this,
						new MenuTwoButtonDialog.ButtonClick() {

							@Override
							public void onSureButtonClick(int index) {
								// TODO Auto-generated method stub

								switch (index) {
								case 0:
									Intent intent = new Intent();
									intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
									// intent.addCategory("android.intent.category.DEFAULT");

									String name = DateFormat.format(
											"yyyyMMddhhmmss",
											Calendar.getInstance(Locale.CHINA))
											+ ".mp4";

									File file = new File(SAVEPATH);
									if (!file.exists()) {
										file.mkdirs();
									}
									videoPath = SAVEPATH + "/" + name;

									File mCurrentVideoFile = new File(videoPath);
									Uri uri = Uri.fromFile(mCurrentVideoFile);
									intent.putExtra(MediaStore.EXTRA_OUTPUT,
											uri);

									startActivityForResult(intent,
											VIDEO_RECORD_WITH_DATA);

									break;

								default:
									Intent pickintent = new Intent(
											Intent.ACTION_GET_CONTENT);
									pickintent.setType("video/mp4");
									pickintent.putExtra("return-data", true);
									startActivityForResult(pickintent,
											VIDEO_PICKED_WITH_DATA);
									break;
								}
							}
						});
				dialog.title_tv.setText("现在拍摄");
				dialog.tv2.setText("从相册里选取");
				dialog.show();

			}
		});

		content_et.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				showKeyboard(content_et);
				emojiconMenuContainer.setVisibility(View.GONE);
				face_btn.setImageResource(R.drawable.compose_emoticonbutton_background_highlighted);
				return false;
			}
		});
		
		price_et.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				showKeyboard(price_et);
				emojiconMenuContainer.setVisibility(View.GONE);
				face_btn.setImageResource(R.drawable.compose_emoticonbutton_background_highlighted);
				return false;
			}
		});
		
		
		findViewById(R.id.scrollview).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				emojiconMenuContainer.setVisibility(View.GONE);
				face_btn.setImageResource(R.drawable.compose_emoticonbutton_background_highlighted);
				return false;
			}
		});
	}

	private void doneClick() {
		if (videoPath == null) {
			AnimationYoYo.shakeView(imageview);
			return;
		}
		if (price_et.getText().toString().trim().equals("")) {
			AnimationYoYo.shakeView(findViewById(R.id.price_ll));
			return;
		}
		try {
			float price = Float.parseFloat(price_et.getText().toString().trim());
			if (price<=0) {
				showErrorToast("不能为0元");
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		progress.show();
		new Thread(new UpdateStringRun(coverImagePath, videoPath)).start();
	}
	private String port = "topic/release";
	private class upLoadImagesRunnable implements Runnable {
		Map<String, String> paramsMap;
		List<String> list;
		String urlstring;


		@Override
		public void run() {
			StringResponse retMap = null;
			try {
				String url = HttpUtil.IP + port;
				// 如果不是切割的upLoadBitmap就很大,在这里压缩
				retMap = JsonParser.getStringResponse2(HttpUtil.upload(paramsMap,list,url));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (retMap != null) {
//				retMap.setTag(newPicturePath);
				handler.sendMessage(handler.obtainMessage(
						NETWORK_SUCCESS_DATA_RIGHT, retMap));
			} else {
				handler.sendMessage(handler
						.obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
			}
		}
	}
/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case VIDEO_PICKED_WITH_DATA:// 从相册选择视频
				if (data != null) {
					Uri uri = data.getData();
					String picturePathFromGallery = BasePhotoActivity.getPath(
							this, uri);
					if (null != picturePathFromGallery) {
						Bitmap bitmap = getVideoThumbnail(picturePathFromGallery);

						// 将上一步的图片压缩到100K左右 并保存到自己的新目录下，返回新的全路径
						coverImagePath = BitmapUtil.saveMyBitmapWithCompress(
								null, bitmap, 90);

						// 释放掉视频缩略图的bitmap
						bitmap.recycle();
						bitmap = null;

						ImageLoader.getInstance().displayImage(
								"file://" + coverImagePath, imageview);

						play_imageview.setVisibility(View.VISIBLE);
						delete_btn.setVisibility(View.VISIBLE);

						videoPath = picturePathFromGallery;
					}
				}
				break;
			case VIDEO_RECORD_WITH_DATA:// 拍摄视频

				if (videoPath == null || videoPath.equals("")) {
					break;
				}
				File file = new File(videoPath);
				if (!file.exists()) {
					return;
				}
				Bitmap bitmap = VideoReleaseActivity.getVideoThumbnail(videoPath);
				
				// 将上一步的图片压缩到100K左右 并保存到自己的新目录下，返回新的全路径
				coverImagePath = BitmapUtil.saveMyBitmapWithCompress(null,
						bitmap, 90);

				// 释放掉视频缩略图的bitmap
				bitmap.recycle();
				bitmap = null;

				ImageLoader.getInstance().displayImage(
						"file://" + coverImagePath, imageview);

				play_imageview.setVisibility(View.VISIBLE);
				delete_btn.setVisibility(View.VISIBLE);

				break;
			}

			video_remark_tv.setText("当前选择视频大小："
					+ FileSizeUtil.getAutoFileOrFilesSize(videoPath));

		}
	}
*/

	// 先上传封面
	private class UpdateStringRun implements Runnable {
		private File upLoadBitmapFile;
		private String videoPath;
		private String newPicturePath;

		public UpdateStringRun(String newPicturePath, String videoPath) {
			this.newPicturePath = newPicturePath;
			this.videoPath = videoPath;
			this.upLoadBitmapFile = new File(newPicturePath);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String url = HttpUtil.IP + "user/upload";
				// 如果不是切割的upLoadBitmap就很大,在这里压缩
				final StringResponse retMap = JsonParser
						.getStringResponse2(HttpUtil.uploadFile(url,
								upLoadBitmapFile,userid));

				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", VideoReleaseActivity.this.getUserID());
				final StringResponse response = JsonParser
						.getStringResponse2(HttpUtil.getMsg(HttpUtil.IP
								+ "video/uploadtoken?" + HttpUtil.getData(data)));

				runOnUiThread(new Runnable() {

					public void run() {
						// 上传封面成功
						// 删除临时的100K左右的图片

						File thumbnailPhoto = new File(newPicturePath);
						if (thumbnailPhoto.exists()) {
							thumbnailPhoto.delete();
						}

						if (response != null && retMap != null) {
							response.setTag(retMap.getData());
							handler.sendMessage(handler.obtainMessage(
									BaseActivity.NETWORK_SUCCESS_DATA_RIGHT,
									response));
						} else {
							handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
						}
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void uploadVideo(String coverUrl, String token) {
		progress.show();
		UploadManager uploadManager = new UploadManager();
		String data = videoPath; // <File对象、或 文件路径、或 字节数组>;
		String key = null;// <指定七牛服务上的文件名，或 null>
		Map<String, String> params = new HashMap<String, String>();

		params.put("x:content", content_et.getText().toString());
		params.put("x:userid", getUserID());
		params.put("x:cover", coverUrl);
		params.put("x:price", price_et.getText().toString());
		
		params.put("x:address", location_checkbox.isChecked()?location_checkbox.getText().toString(): "来自 上海市");
		if(location_checkbox.isChecked() && currentLocationBean!=null){
			//用户愿意上传经纬度，该动态会出现在 附近的动态 里
			params.put("x:latitude", ""+currentLocationBean.getLatitude());
			params.put("x:longitude", ""+currentLocationBean.getLongitude());
		}
		
		final NumberFormat nFromat = NumberFormat.getPercentInstance();
		UploadOptions opinion = new UploadOptions(params, null, false, new UpProgressHandler() {
			
			@Override
			public void progress(String key, double percent) {
				// TODO Auto-generated method stub
				
				String rates = nFromat.format(percent);
				
				progress.setMessage("进度："+rates);
			}
		},null);
		
		uploadManager.put(data, key, token, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject res) {
				// res 包含hash、key等信息，具体字段取决于上传策略的设置。
				progress.dismiss();
				
				try {
					String code = res.getString("code");
					
					if ("1".endsWith(code)) {
						hideKeyboard();
						setResult(RELEASE_SUCCESS);
						finish();
					}else{
						showErrorToast(""+res.getString("messsage"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showErrorToast("解析异常，请稍后再试");
					e.printStackTrace();
				}
			}
		}, opinion);
	}

	private void setShowLength(int requestCode) {
		int currentLength = content_et.getText().length();
		setShowTextLength(currentLength, maxlength);
	}

	@Override
	public void getInputLength(int length) {
		setShowTextLength(length, maxlength);
	}

	private void setShowTextLength(int currentLength, int maxLength) {
		showlength_tv.setText(currentLength + "/" + maxLength + " 字");
	}

	@Override
	public void onOverFlowHint() {
		// TODO Auto-generated method stub
		AnimationYoYo.shakeView(showlength_tv);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getLocationManager().removeLocationListener(this);
		if (coverImagePath != null) {
			File thumbnailPhoto = new File(coverImagePath);
			if (thumbnailPhoto.exists()) {
				thumbnailPhoto.delete();
			}
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		hideKeyboard();
		emojiconMenuContainer.setVisibility(View.GONE);
		super.onPause();
	}


	@Override
	public void onLocationFail() {
		// TODO Auto-generated method stub
		location_checkbox.setText("定位失败");
	}

	@Override
	public void onLocationSuccess(final LocationBean currentLocationBean) {
		// TODO Auto-generated method stub
		location_checkbox.setEnabled(true);
		location_checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				// TODO Auto-generated method stub
				location_checkbox.setText(checked?currentLocationBean.getAddress():"不显示位置");
			}
		});
		location_checkbox.setChecked(true);
		this.currentLocationBean = currentLocationBean;
	}

	
	/**  
     * 根据指定的图像路径和大小来获取缩略图  
     * 此方法有两点好处：  
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，  
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。  
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使  
     *        用这个工具生成的图像不会被拉伸。  
     * @param imagePath 图像的路径  
     * @param width 指定输出图像的宽度  
     * @param height 指定输出图像的高度  
     * @return 生成的缩略图  
     */    
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {    
        Bitmap bitmap = null;    
        BitmapFactory.Options options = new BitmapFactory.Options();    
        options.inJustDecodeBounds = true;    
        // 获取这个图片的宽和高，注意此处的bitmap为null    
        bitmap = BitmapFactory.decodeFile(imagePath, options);    
        options.inJustDecodeBounds = false; // 设为 false    
        // 计算缩放比    
        int h = options.outHeight;    
        int w = options.outWidth;    
        int beWidth = w / width;    
        int beHeight = h / height;    
        int be = 1;    
        if (beWidth < beHeight) {    
            be = beWidth;    
        } else {    
            be = beHeight;    
        }    
        if (be <= 0) {    
            be = 1;    
        }    
        options.inSampleSize = be;    
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false    
        bitmap = BitmapFactory.decodeFile(imagePath, options);    
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象    
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,    
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);    
        return bitmap;    
    }    
    
    /**  
     * 获取视频的缩略图  
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。  
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     */    
    public static Bitmap getVideoThumbnail(String videoPath) {    
    	
    	MediaMetadataRetriever retr = new MediaMetadataRetriever();  
		retr.setDataSource(videoPath);  
		int height = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度  
		int width = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度 
    	
		int orientation = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)); // 视频旋转方向  
		
//		int orientation = 0;
		
        Bitmap bitmap = null;    
        // 获取视频的缩略图    
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);    
        
        if (orientation != 0) {
        	bitmap = ThumbnailUtils.extractThumbnail(bitmap,  height,    width,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        }else{
        	bitmap = ThumbnailUtils.extractThumbnail(bitmap,   width,height,   
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        }
        
//    	if (orientation != 0) {
//    		int degress = Integer.valueOf(orientation);
//    		bitmap = BitmapUtil.adjustPhotoRotation(bitmap, degress);
//		}
        
        return bitmap;    
    }

	public void startPhoto(int position){
		switch (position) {
			case 0: // 直接调起相机
				/**
				 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
				 *
				 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
				 *
				 * 如果实在有所需要，请直接下载源码引用。
				 */
				//打开选择,本次允许选择的数量
				ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
				ImagePicker.getInstance().setCrop(false);
				ImagePicker.getInstance().setImageLoader(new com.lzy.imagepicker.loader.ImageLoader() {
					@Override
					public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
						Glide.with(activity).load(path).into(imageView);
					}
					@Override
					public void clearMemoryCache() {
					}
				});
				Intent intent = new Intent(VideoReleaseActivity.this, ImageGridActivity.class);
				intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
				startActivityForResult(intent, REQUEST_CODE_SELECT);
				break;
			case 1:
				//打开选择,本次允许选择的数量
				ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
				ImagePicker.getInstance().setCrop(false);        //允许裁剪（单选才有效）
				ImagePicker.getInstance().setSaveRectangle(true); //是否按矩形区域保存
				ImagePicker.getInstance().setFocusWidth(600);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
				ImagePicker.getInstance().setFocusHeight(600);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
				ImagePicker.getInstance().setOutPutX(300);//保存文件的宽度。单位像素
				ImagePicker.getInstance().setOutPutY(300);//保存文件的高度。单位像素
				Intent intent1 = new Intent(VideoReleaseActivity.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
				startActivityForResult(intent1, REQUEST_CODE_SELECT);
				break;
			default:
				break;
		}
	}

	/**
	 * 底部弹出选择拍照还是图库
	 */
	private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
		SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
				listener, names);
		if (!this.isFinishing()) {
			dialog.show();
		}
		return dialog;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
			//添加图片返回
			if (data != null && requestCode == REQUEST_CODE_SELECT) {
				images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
				if (images != null) {
					selImageList.addAll(images);

				}
			}
		} else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
			//预览图片返回
			if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
				images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
				if (images != null) {
					selImageList.clear();
					selImageList.addAll(images);

				}
			}
		}
	}
}
