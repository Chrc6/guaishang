package com.houwei.guaishang.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.layout.PhotoPopupWindow;
import com.houwei.guaishang.layout.PhotoPopupWindow.SelectPhotoListener;
import com.houwei.guaishang.tools.BitmapUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ImageCompress;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.view.cropimage.CropImageActivity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 带有修改头像功能的界面，继承此类
 * 子类只需要调用showBottomPopupWin方法，即可在底部弹出 拍照/相册 框
 * 然后子类 监听（重写）3个方法：
 * onPhotoSelectSuccess 选择成功（自动上传）
 * onPhotoUploadSuccess 上传成功
 * onPhotoUploadFail 上传失败
 * 
 * @author dongjin
 *
 */
public class BasePhotoActivity extends BaseActivity implements
		SelectPhotoListener {
	private final int USER_ICON_IMAGEVIEW_ID = R.id.user_head;
	/** 从媒体库选择图片 */
	private static final int PHOTO_PICKED_WITH_DATA = 0x10;
	/** 照相选择图片 */
	public static final int PHOTO_CAMERA_WITH_DATA = 0x17;
	/** 剪切图片 */
	private static final int PHOTO_CROP_PATH = 0x18;
	public static final String SAVEPATH = Environment
			.getExternalStorageDirectory().getPath() + "/" + "picture";
	private String camera_pic_path;
	private String port = "user/upload";
	private ImageView currentImageView;
	private PhotoPopupWindow mPopupWin;
	private boolean cropImage;
	private String userid;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
		
	    @Override
		public void handleMessage(Message msg) {
			BasePhotoActivity activity = (BasePhotoActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				StringResponse retMap = (StringResponse) msg.obj;
				if (retMap.isSuccess()) {
					// showErrorToast("上传成功");
					// 删除临时的100K左右的图片
					String thumbnailPath = retMap.getTag();
					File thumbnailPhoto = new File(thumbnailPath);
					if (thumbnailPhoto.exists()) {
						thumbnailPhoto.delete();
					}

					activity.onPhotoUploadSuccess(retMap.getData(), retMap.getTag(),
							activity.currentImageView);
				} else {
					activity.showErrorToast(retMap.getMessage());
				}
				break;
			case NETWORK_SUCCESS_DATA_ERROR:
				activity.onPhotoUploadFail(activity.currentImageView);
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
		userid=getITopicApplication().getMyUserBeanManager().getUserId();
		if (savedInstanceState != null) {
			camera_pic_path = savedInstanceState.getString("camera_pic_path");
			cropImage = savedInstanceState.getBoolean("cropImage", false);
		}
	}

	/**
	 * @param currentImageView 头像ImageView。可以为空，为空的话，不剪裁图片
	 * @param port 服务器的上传图片接口 类似 "upload/image"
	 */
	public void showBottomPopupWin(ImageView currentImageView, String port) {
		this.port = port;
		this.currentImageView = currentImageView;
		if (currentImageView != null
				&& currentImageView.getId() == USER_ICON_IMAGEVIEW_ID) {
			this.cropImage = true;
		}

		LayoutInflater mLayoutInfalter = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout mPopView = (LinearLayout) mLayoutInfalter.inflate(
				R.layout.bottom_photo_select_popupwindow, null);
		mPopupWin = new PhotoPopupWindow(this, mPopView);
		mPopupWin.setAnimationStyle(R.style.BottomPopupAnimation);
		mPopupWin.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0,
				0);
		mPopupWin.setOnSelectPhotoListener(this);
	}

	// 如果不是切割的upLoadBitmap就很大
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
				String url = HttpUtil.IP + port;
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

	/** 子类设置为true，选取图片后将进入切图模式 */
	protected void setNeedCropImage(boolean cropImage) {
		this.cropImage = cropImage;
	}
	
	/** startActivityForResult方式选择图片，onActivityResult接收返回的图片文件 */
	protected void pickPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/jpeg");
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}

	/** startActivityForResult方式选择图片，onActivityResult接收返回的图片文件 */
	private void pickPhotoFromCamera() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			showErrorToast("请插入sd卡");
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String name = DateFormat.format("yyyyMMddhhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".jpg";

		File file = new File(SAVEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		camera_pic_path = SAVEPATH + "/" + name;
		File mCurrentPhotoFile = new File(camera_pic_path);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT,
//				Uri.fromFile(mCurrentPhotoFile));
//		startActivityForResult(intent, PHOTO_CAMERA_WITH_DATA);

		ContentValues contentValues = new ContentValues(1);
		contentValues.put(MediaStore.Images.Media.DATA,  mCurrentPhotoFile.getAbsolutePath());
		Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri), BasePhotoActivity.PHOTO_CAMERA_WITH_DATA);


	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: // 调用媒体库
			if (data != null) {
				Uri uri = data.getData();
				String picturePathFromGallery = getPath(this, uri);
				if (null != picturePathFromGallery) {
					Crop_OR_Rar(picturePathFromGallery, data);
				}
			}
			break;
		case PHOTO_CAMERA_WITH_DATA:
			String picturePath = camera_pic_path;

			if (picturePath == null || picturePath.equals("")) {
				break;
			}
			File file = new File(picturePath);
			if (!file.exists()) {
				break;
			}
			Crop_OR_Rar(picturePath, data);
			break;

		case PHOTO_CROP_PATH:
			if (data != null) {
				String newPicturePath = data.getStringExtra("newPicturePath");
				if (newPicturePath != null) {
					onPhotoSelectSuccess(newPicturePath, currentImageView);
					upLoadPicture(newPicturePath);
				} else {
					showErrorToast("图片保存异常");
				}
			}
			break;
		}
	}

	/**
	 * 如果是头像icon 就调用裁剪 别的就直接压缩,压缩完了就upload
	 * 
	 * @param picturePath
	 * @param data
	 */
	private void Crop_OR_Rar(String picturePath, Intent data) {
		if (cropImage) {
			// 进入切图模式，传入picturePath到CropActivity，之后picturePath将被丢弃
			cropPhoto(picturePath);
		} else if(picturePath.endsWith(".gif")){
			// 回调1280*720的新图片路径
			onPhotoSelectSuccess(picturePath, currentImageView);

			// 上传图片 ，新路径，原始Bitmap（很大）
			upLoadPicture(picturePath);
		} else {
			// 从picturePath取出图片 + android系统的方法压缩 + 旋转
			ImageCompress compress = new ImageCompress();
			ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
			options.filePath = picturePath;
			Bitmap mBitmap = compress.compressFromUri(this, options);

			if (mBitmap == null) {
				return;
			}

			// 将上一步的图片压缩到100K左右 并保存到自己的新目录下，返回新的全路径
			String newPicturePath = BitmapUtil.saveMyBitmapWithCompress(
					picturePath, mBitmap, 80);

			// 释放掉1280*720的bitmap
			recycleBitmap(mBitmap);

			// 回调1280*720的新图片路径
			onPhotoSelectSuccess(newPicturePath, currentImageView);

			// 上传图片 ，新路径，原始Bitmap（很大）
			upLoadPicture(newPicturePath);
		}
	}

	// 如果不是切割的upLoadBitmap就很大
	private void upLoadPicture(String newPicturePath) {
		progress.show();
		new Thread(new UpdateStringRun(newPicturePath)).start();
	}

	private void cropPhoto(String photoPath) {
		// 调用CropImage类对图片进行剪切
		Intent intent = new Intent(this, CropImageActivity.class);
		intent.putExtra("photoPath", photoPath);
		startActivityForResult(intent, PHOTO_CROP_PATH);
	}

	@Override
	public void onGallery(View v) {
		// TODO Auto-generated method stub
		pickPhotoFromGallery();
	}

	@Override
	public void onCamera(View v) {
		// TODO Auto-generated method stub
		pickPhotoFromCamera();

	}

	public void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 选取图片成功
	 * 
	 * @param
	 * @param currentImageView
	 */
	public void onPhotoSelectSuccess(String picturePath,
			ImageView currentImageView) {
	}

	/**
	 * 上传成功
	 * 
	 * @param imageUrl
	 * @param currentImageView
	 */
	public void onPhotoUploadSuccess(String imageUrl, String picturePath,
			ImageView currentImageView) {
	}

	/**
	 * 上传失败
	 * 
	 * @param
	 * @param currentImageView
	 */
	public void onPhotoUploadFail(ImageView currentImageView) {
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("camera_pic_path", camera_pic_path);
		outState.putBoolean("cropImage", cropImage);
		super.onSaveInstanceState(outState);
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}
}
