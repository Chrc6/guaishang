package com.houwei.guaishang.activity;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.houwei.guaishang.R;
import com.houwei.guaishang.adapter.GridAdapter;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.layout.PhotoPopupWindow;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.manager.MyUserBeanManager.EditInfoListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.tools.BitmapUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ImageCompress;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.view.CircleImageView;
import com.houwei.guaishang.views.UnScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * 我的 个人资料 界面
 * UserStateChangeListener，当前账号状态监听
 * EditInfoListener，修改我的某项资料的网络请求监听
 */
public class MinePersonalInfoDetailActivity extends  BaseActivity implements OnClickListener, UserStateChangeListener, EditInfoListener , PhotoPopupWindow.SelectPhotoListener {
	private UserBean userBean ;
	private MyUserBeanManager myUserBeanManager;
//	private InfoPersonalMineLayout infoPersonalMineLayout;
	private CircleImageView user_icon;

	public final static int PICTURE_UPDATE_ICON = R.drawable.picture_update_icon;
	/**
	 * 最多选择图片的个数
	 */
	private int MAX_DEFAULT_NUM = 9;
	private UnScrollGridView gridView;
	private ImageView gridViewLisencce;
	//调用拍照，拍出来的原图的url
	private String camera_pic_path;

	// 选择的原图路径
	private ArrayList<String> selectedPicture = new ArrayList<String>();

	// 压缩后的临时路径
	public ArrayList<String> thumbPictures = new ArrayList<String>();

	// 压缩后的临时路径
	public ArrayList<String> thumbPictures1 = new ArrayList<String>();
	// 压缩后的临时路径
	public ArrayList<String> thumbPictures0 = new ArrayList<String>();

	private static final int REQUEST_PICK = 0;
	private PhotoReleaseGridAdapter gridAdapter;
	private RxPermissions rxPermissions;

	private MyHandler handler = new MyHandler(this);
	private String userid;

	private static class MyHandler extends Handler {

		private WeakReference<Context> reference;

		public MyHandler(Context context) {
			reference = new WeakReference<Context>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			MinePersonalInfoDetailActivity activity = (MinePersonalInfoDetailActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			switch (msg.what) {
				case NETWORK_SUCCESS_DATA_RIGHT:
					StringResponse retMap = (StringResponse) msg.obj;
					Log.d("CCC","上传："+retMap);
//					if (retMap.isSuccess()) {
//						// showErrorToast("上传成功");
//						// 删除临时的100K左右的图片
//						String thumbnailPath = retMap.getTag();
//						File thumbnailPhoto = new File(thumbnailPath);
//						if (thumbnailPhoto.exists()) {
//							thumbnailPhoto.delete();
//						}
//
//						activity.onPhotoUploadSuccess(retMap.getData(), retMap.getTag(),
//								activity.currentImageView);
//					} else {
//						activity.showErrorToast(retMap.getMessage());
//					}
					break;
				case NETWORK_SUCCESS_DATA_ERROR:
//					activity.onPhotoUploadFail(activity.currentImageView);
					break;
				default:
					activity.showErrorToast();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_info_personal);
		userid=getITopicApplication().getMyUserBeanManager().getUserId();
		rxPermissions=new RxPermissions(this);
		if (savedInstanceState != null) {
			camera_pic_path = savedInstanceState.getString("camera_pic_path");
		}
		initView();
		initListener();
	}

	protected void initView() {
		initProgressDialog();
		myUserBeanManager =  getITopicApplication().getMyUserBeanManager();
		userBean = myUserBeanManager.getInstance();
		user_icon = (CircleImageView) findViewById(R.id.user_head);
		gridViewLisencce = (ImageView) findViewById(R.id.gridView_lisence);
		ImageLoader.getInstance().displayImage(userBean.getAvatar().findOriginalUrl(), user_icon);
		ScrollView  scrollview = (ScrollView) findViewById(R.id.scrollview);
		onUserInfoChanged(userBean);
		thumbPictures.add("" + PICTURE_UPDATE_ICON);
		thumbPictures1.add("" + PICTURE_UPDATE_ICON);
		gridView = (UnScrollGridView) findViewById(R.id.gridView);
		gridAdapter=new MinePersonalInfoDetailActivity.PhotoReleaseGridAdapter(thumbPictures, this, new CallAdapterInter() {
			@Override
			public void call(int position) {
				File thumbnailPhoto = new File(thumbPictures.get(position));
				if (thumbnailPhoto.exists()) {
					thumbnailPhoto.delete();
				}
				thumbPictures.remove(position);
				gridAdapter.notifyDataSetChanged();
			}
		});
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(@NonNull Boolean aBoolean) throws Exception {
								if(aBoolean){
									getType=2;
									if (thumbPictures.get(arg2).equals(""+PICTURE_UPDATE_ICON)) {
										MAX_DEFAULT_NUM = 9;
										showBottomPopupWin();
									} else {
										Intent intent = new Intent(MinePersonalInfoDetailActivity.this, GalleryActivity.class);
										ArrayList<String> urls = new ArrayList<String>();
										for (String string : thumbPictures) {
											urls .add("file://"+string);
										}
										intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
										intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, arg2);
										startActivity(intent);
									}
								}
							}
						});

			}
		});
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		myUserBeanManager.addOnUserStateChangeListener(this);
		findViewById(R.id.user_head).setOnClickListener(this);
		findViewById(R.id.gridView_lisence).setOnClickListener(this);

	}

	private  int getType=0;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i;
		switch (v.getId()) {
		case R.id.user_head:
			hideKeyboard();
			getType=0;
			MAX_DEFAULT_NUM=1;
			showBottomPopupWin();

			break;
		case R.id.gridView_lisence:
			rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
					.subscribe(new Consumer<Boolean>() {
						@Override
						public void accept(@NonNull Boolean aBoolean) throws Exception {
							if(aBoolean){
								hideKeyboard();
								getType=1;
								MAX_DEFAULT_NUM=1;
								showBottomPopupWin();
							}
						}
					});

			break;
		
		default:
			break;
		}
	}

	public int getMaxLimit(){
		return MAX_DEFAULT_NUM;
	}

	private void clearCurreantThumbList() {
		// 删除临时的100K左右的图片
		for (String thumbnailPath : thumbPictures) {
			File thumbnailPhoto = new File(thumbnailPath);
			if (thumbnailPhoto.exists()) {
				thumbnailPhoto.delete();
			}
		}
		thumbPictures.clear();
		thumbPictures.add("" + PICTURE_UPDATE_ICON);
	}

	//全部清空除了第一张之外的图片，目前只由子类调用该方法
	public void clearSelectPicturesWithoutFirst(){
		if (thumbPictures.size() > 2) {
			//如果当前选择的图大于1张（还有一个1是 +按钮）
			for (int i = thumbPictures.size() - 1 ; i > 0 ; i--) {
				File thumbnailPhoto = new File(thumbPictures.get(i));
				if (thumbnailPhoto.exists()) {
					thumbnailPhoto.delete();
				}
				thumbPictures.remove(i);
			}
			thumbPictures.add("" + PICTURE_UPDATE_ICON);
		}

		if (selectedPicture.size() > 1) {
			for (int i = selectedPicture.size() - 1 ; i > 0 ; i--) {
				selectedPicture.remove(i);
			}
		}
		//刷新gridView
		resetAdapter();
	}

	//子类也会调用这个方法
	public void resetAdapter(){
		gridView.setAdapter(new MinePersonalInfoDetailActivity.PhotoReleaseGridAdapter(thumbPictures,
				MinePersonalInfoDetailActivity.this,null));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("camera_pic_path", camera_pic_path);
		super.onSaveInstanceState(outState);
	}

	public class PhotoReleaseGridAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		private ArrayList<String> thumbPictures;
		private DisplayImageOptions options;
		private CallAdapterInter inter;

		public PhotoReleaseGridAdapter(ArrayList<String> thumbPictures,Context mContext, CallAdapterInter inter) {
			this.thumbPictures = thumbPictures;
			this.mLayoutInflater = LayoutInflater.from(mContext);
			this.options = getITopicApplication().getOtherManage().getRectDisplayImageOptions();
			this.inter=inter;
		}

		@Override
		public int getCount() {
			return thumbPictures.size() > getMaxLimit() ? getMaxLimit()
					: thumbPictures.size();
		}

		@Override
		public String getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			MinePersonalInfoDetailActivity.MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MinePersonalInfoDetailActivity.MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.griditem_picture_delete,
						parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewHolder.delete_btn = convertView
						.findViewById(R.id.delete_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MinePersonalInfoDetailActivity.MyGridViewHolder) convertView.getTag();
			}

				String photoRecourse = thumbPictures.get(position).equals(
						"" + BasePhotoGridActivity.PICTURE_UPDATE_ICON) ? "drawable://" : "file://";

				//加号图片 去掉删除按钮
				viewHolder.delete_btn.setVisibility((thumbPictures.get(position).equals(
						"" + BasePhotoGridActivity.PICTURE_UPDATE_ICON))?View.GONE:View.VISIBLE);

				viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

//						File thumbnailPhoto = new File(thumbPictures.get(position));
//						if (thumbnailPhoto.exists()) {
//							thumbnailPhoto.delete();
//						}
//						thumbPictures.remove(position);
						Log.d("CCC","size:"+thumbPictures.size());
						inter.call(position);
//						selectedPicture.remove(position); //删除掉(但不是删除原图)，这样再次进入选择图片，这张图不会被勾
//						PhotoReleaseGridAdapter.this.notifyDataSetChanged();
//						gridView.setAdapter(new PhotoReleaseGridAdapter(thumbPictures,
//								MinePersonalInfoDetailActivity.this));
					}
				});

				ImageLoader.getInstance().displayImage(
						photoRecourse + thumbPictures.get(position),
						viewHolder.imageView,options);

			return convertView;
		}
	}
	public class PhotoReleaseGridAdapter1 extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		private ArrayList<String> thumbPictures;
		private DisplayImageOptions options;
		private CallAdapterInter inter;

		public PhotoReleaseGridAdapter1(ArrayList<String> thumbPictures,Context mContext, CallAdapterInter inter) {
			this.thumbPictures = thumbPictures;
			this.mLayoutInflater = LayoutInflater.from(mContext);
			this.options = getITopicApplication().getOtherManage().getRectDisplayImageOptions();
			this.inter=inter;
		}

		@Override
		public int getCount() {
			return thumbPictures.size() > getMaxLimit() ? getMaxLimit()
					: thumbPictures.size();
		}

		@Override
		public String getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			MinePersonalInfoDetailActivity.MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MinePersonalInfoDetailActivity.MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.griditem_picture_delete,
						parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewHolder.delete_btn = convertView
						.findViewById(R.id.delete_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MinePersonalInfoDetailActivity.MyGridViewHolder) convertView.getTag();
			}

				String photoRecourse = thumbPictures.get(position).equals(
						"" + BasePhotoGridActivity.PICTURE_UPDATE_ICON) ? "drawable://" : "file://";

				//加号图片 去掉删除按钮
				viewHolder.delete_btn.setVisibility((thumbPictures.get(position).equals(
						"" + BasePhotoGridActivity.PICTURE_UPDATE_ICON))?View.GONE:View.VISIBLE);

				viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

//						File thumbnailPhoto = new File(thumbPictures.get(position));
//						if (thumbnailPhoto.exists()) {
//							thumbnailPhoto.delete();
//						}
//						thumbPictures.remove(position);
						Log.d("CCC","size:"+thumbPictures.size());
						inter.call(position);
//						selectedPicture.remove(position); //删除掉(但不是删除原图)，这样再次进入选择图片，这张图不会被勾
//						PhotoReleaseGridAdapter.this.notifyDataSetChanged();
//						gridView.setAdapter(new PhotoReleaseGridAdapter(thumbPictures,
//								MinePersonalInfoDetailActivity.this));
					}
				});

				ImageLoader.getInstance().displayImage(
						photoRecourse + thumbPictures.get(position),
						viewHolder.imageView,options);

			return convertView;
		}
	}

	interface CallAdapterInter{
		void call(int  position);
	}

	private static class MyGridViewHolder {
		private ImageView imageView;
		private View delete_btn;
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case BasePhotoActivity.PHOTO_CAMERA_WITH_DATA://拍照成功
					selectedPicture.add(camera_pic_path);
					break;

				default://从相册选择图片集合
					selectedPicture = (ArrayList<String>) data
							.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
					break;
			}
			progress.show();
			// 在子线程中处理，并展示
			new Thread(new MinePersonalInfoDetailActivity.CompressRun(selectedPicture)).start();
		}
	}


	// 采用android官方处理bitmap方式，完美解决OOM问题
	// 在子线程中 将选择的大图 -> 压缩 + 旋转 + 释放bitmap + 重新存储为临时图片
	private class CompressRun implements Runnable {
		private ArrayList<String> orgPictures;

		// orgPictures 是 刚刚选择的List<原图路径>
		public CompressRun(ArrayList<String> orgPictures) {
			this.orgPictures = orgPictures;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				//不要在子线程里修改全局变量！所以这里用临时变量
				final  ArrayList<String> tempThumbPictures = new ArrayList<String>();

				//原图必须要先压缩，重新保存，thumbnailPictures压缩后的图片路径
				for (String orgPath : orgPictures) {

					if (orgPath.endsWith(".gif")) {
						tempThumbPictures.add(orgPath);
					} else {
						// 从picturePath取出图片 + 第一步压缩 + 旋转
						ImageCompress compress = new ImageCompress();
						ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
						options.filePath = orgPath;
						Bitmap mBitmap = compress.compressFromUri(
								MinePersonalInfoDetailActivity.this, options);
						if (mBitmap == null) {// 跳过错误的图片
							continue;
						}
						// 将上一步的图片再次压缩到100K左右（和微信微博一致） 并保存到自己的新目录下，返回新的全路径
						String newPicturePath = BitmapUtil
								.saveMyBitmapWithCompress(orgPath, mBitmap, 80);
						tempThumbPictures.add(newPicturePath);
					}

				}
				// 处理完毕，回到主线程显示在界面上
				MinePersonalInfoDetailActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.dismiss();
						//清除上次的 压缩图
						clearCurreantThumbList();
//						//重新显示这次选择的
//						thumbPictures.addAll(0, tempThumbPictures);

						switch (getType){
							case 0:

								thumbPictures0.addAll(0, tempThumbPictures);
								Glide.with(MinePersonalInfoDetailActivity.this).load(thumbPictures0.get(0)).into(user_icon);
								break;
							case 1:

								thumbPictures1.addAll(0, tempThumbPictures);
								Glide.with(MinePersonalInfoDetailActivity.this).load(thumbPictures1.get(0)).into(gridViewLisencce);
								upLoadPicture(thumbPictures1.get(0));
								break;
							case 2:
								thumbPictures.addAll(0, tempThumbPictures);
								gridAdapter.notifyDataSetChanged();
								break;
						}
						tempThumbPictures.clear();
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void showBottomPopupWin() {
		hideKeyboard();
		LayoutInflater mLayoutInfalter = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout mPopView = (LinearLayout) mLayoutInfalter.inflate(
				R.layout.bottom_photo_select_popupwindow, null);
		PhotoPopupWindow mPopupWin = new PhotoPopupWindow(this, mPopView);
		mPopupWin.setAnimationStyle(R.style.BottomPopupAnimation);
		mPopupWin.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0,
				0);
		mPopupWin.setOnSelectPhotoListener(this);
	}

	/** startActivityForResult方式选择图片，onActivityResult接收返回的图片文件 */
	public void pickPhotoFromCamera() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			showErrorToast("请插入sd卡");
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String name = DateFormat.format("yyyyMMddhhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".jpg";

		File file = new File(BasePhotoActivity.SAVEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		camera_pic_path = BasePhotoActivity.SAVEPATH + "/" + name;
		File mCurrentPhotoFile = new File(camera_pic_path);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(mCurrentPhotoFile));
//		startActivityForResult(intent, BasePhotoActivity.PHOTO_CAMERA_WITH_DATA);
//
		ContentValues contentValues = new ContentValues(1);
		contentValues.put(MediaStore.Images.Media.DATA,  mCurrentPhotoFile.getAbsolutePath());
		Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri), BasePhotoActivity.PHOTO_CAMERA_WITH_DATA);
	}


	//从相册选择图片
	@Override
	public void onGallery(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(MinePersonalInfoDetailActivity.this, SelectPictureActivity.class);
		selectedPicture.clear();
		i.putExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE, selectedPicture);
		i.putExtra("MaxLimit", getMaxLimit());
		startActivityForResult(i,REQUEST_PICK);
	}

	//调用系统拍照
	@Override
	public void onCamera(View v) {
		// TODO Auto-generated method stub
		pickPhotoFromCamera();
	}

	// 如果不是切割的upLoadBitmap就很大
	private void upLoadPicture(String newPicturePath) {
		progress.show();
		new Thread(new UpdateStringRun(newPicturePath)).start();
	}


	// 如果不是切割的upLoadBitmap就很大,上传单张图片
	private class UpdateStringRun implements Runnable {
		private File upLoadBitmapFile;
		private String newPicturePath;

		public UpdateStringRun(String newPicturePath) {
			this.newPicturePath = newPicturePath;
			this.upLoadBitmapFile = new File(newPicturePath);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			StringResponse retMap = null;
			try {
				String url = HttpUtil.IP + "user/id_card";
				// 如果不是切割的upLoadBitmap就很大,在这里压缩
				retMap = JsonParser.getStringResponse2(HttpUtil.uploadFile(url,
						upLoadBitmapFile,userid));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (retMap != null) {
				retMap.setTag(newPicturePath);
				handler.sendMessage(handler.obtainMessage(
						NETWORK_SUCCESS_DATA_RIGHT, retMap));
			} else {
				handler.sendMessage(handler
						.obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
			}
		}
	}

	private void doneClick() {
		progress.show();
		new Thread(new UpdateImagesRun(thumbPictures)).start();
	}

	// 一次HTTP请求上传多张图片 + 各种参数
	private class UpdateImagesRun implements Runnable {
		private ArrayList<String> thumbPictures;

		// thumbPictures 是 List<压缩图路径>
		public UpdateImagesRun(ArrayList<String> thumbPictures) {
			this.thumbPictures = new ArrayList<String>();
			for (String string : thumbPictures) {
				if (!string.equals(""+BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
					//去掉最后一个 +图片
					this.thumbPictures.add(string);
				}
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			BaseResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", MinePersonalInfoDetailActivity.this.getUserID());
				// 一次http请求将所有图片+参数上传
				response = JsonParser.getBaseResponse(HttpUtil.upload(data, thumbPictures,HttpUtil.IP + "topic/release"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	}
















	private void jumpToEditTextActivity(String title,String content,String eventName,int maxLength,int inputType,boolean canEmpty ) {
		Intent i = new Intent(MinePersonalInfoDetailActivity.this, EditTextActivity.class);
		i.putExtra(EditTextActivity.TITLE_KEY, title);
		i.putExtra(EditTextActivity.CONTENT, content);
		i.putExtra(EditTextActivity.EVENT_NAME, eventName);
		i.putExtra(EditTextActivity.MAX_LENGTH_KEY, maxLength);
		i.putExtra(EditTextActivity.CAN_EMPTY, canEmpty);
		if (inputType!=InputType.TYPE_NULL) {			
			i.putExtra(EditTextActivity.INPUT_TYPE, inputType);
		}
		startActivity(i);
	}


















	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
		// 我的资料界面。不登录是不可能进入这个界面的，所以这个ub肯定不是null
		userBean = ub;
//		infoPersonalMineLayout.initView(this,ub);
	}




	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onEditFail(String message) {
		// TODO Auto-generated method stub
		progress.dismiss();
		showFailTips(message);
	}

	@Override
	public void onEditSuccess() {
		// TODO Auto-generated method stub
		progress.dismiss();
		showSuccessTips("修改成功");
	}


	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		myUserBeanManager.removeUserStateChangeListener(this);
		super.onDestroy();
	}


	
}
