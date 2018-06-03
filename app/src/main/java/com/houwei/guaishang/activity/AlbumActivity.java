/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.houwei.guaishang.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BasePhotoGridActivity.PhotoReleaseGridAdapter;
import com.houwei.guaishang.bean.AlbumBean;
import com.houwei.guaishang.bean.AlbumListResponse;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.tools.BitmapUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ImageCompress;
import com.houwei.guaishang.tools.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class AlbumActivity extends BaseActivity {

	private final static int ALBUM_DELECT = 0x27;
	private static final int REQUEST_PICK = 0;

	private GridView gridview;
	private ProgressBar loadingPB;
	private String currentUserid;

	private MemberGridAdapter adapter;

	private List<AlbumBean> memberlist;

	private MyHandler handler = new MyHandler(this);

	private static class MyHandler extends Handler {

		private WeakReference<Context> reference;

		public MyHandler(Context context) {
			reference = new WeakReference<Context>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			final AlbumActivity activity = (AlbumActivity) reference.get();
			if (activity == null) {
				return;
			}
			activity.progress.dismiss();
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				// 访问我们自己的服务器，返回用户信息
				activity.loadingPB.setVisibility(View.GONE);
				AlbumListResponse response = (AlbumListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.memberlist = response.getData().getItems();
					activity.adapter = activity.new MemberGridAdapter(
							activity.memberlist, activity);
					activity.gridview.setAdapter(activity.adapter);
				} else {
					activity.showErrorToast(response.getMessage());
				}

				break;

			case BaseActivity.NETWORK_OTHER:
				AlbumListResponse newresponse = (AlbumListResponse) msg.obj;
				if (newresponse.isSuccess()) {
					activity.memberlist.addAll(newresponse.getData().getItems());
					activity.adapter.notifyDataSetChanged();
				} else {
					activity.showErrorToast(newresponse.getMessage());
				}
				break;
				
			case ALBUM_DELECT:
				StringResponse topicResponse = (StringResponse) msg.obj;
				if (topicResponse.isSuccess() && activity.memberlist != null) {
					for (AlbumBean dynamicBean : activity.memberlist) {
						if (topicResponse.getTag().equals(dynamicBean.getId())) {
							activity.memberlist.remove(dynamicBean);
							activity.adapter.notifyDataSetChanged();
							break;
						}
					}
				} else {
					activity.showErrorToast(topicResponse.getMessage());
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		initProgressDialog();
		BackButtonListener();

		currentUserid = getIntent().getStringExtra("userId");

		gridview = (GridView) findViewById(R.id.albumgridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (position < memberlist.size()) {

					Intent intent = new Intent(AlbumActivity.this,
							GalleryActivity.class);
					ArrayList<String> urls = new ArrayList<String>();
					for (AlbumBean bean : memberlist) {
						urls.add(bean.getPicture().findOriginalUrl());
					}
					intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
					intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
					startActivity(intent);
				} else {
					// 进入选人页面
					Intent i = new Intent(AlbumActivity.this,
							SelectPictureActivity.class);
					i.putExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE,
							new ArrayList<String>());
					startActivityForResult(i, REQUEST_PICK);
				}
			}
		});

		new Thread(run).start();
	}

	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			AlbumListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", "1");
				data.put("pagesize", "100");
				response = JsonParser.getAlbumListResponse(HttpUtil
						.getMsg(HttpUtil.IP + "album/getlist" + "?"
								+ HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						NETWORK_SUCCESS_DATA_RIGHT, response));
			} else {
				handler.sendEmptyMessage(NETWORK_FAIL);
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			default:// 从相册选择图片集合
				ArrayList<String> selectedPicture = (ArrayList<String>) data
						.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);

				progress.show();
				// 在子线程中处理，并展示
				new Thread(new CompressRun(selectedPicture)).start();
				break;
			}
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
//			try {
				// 不要在子线程里修改全局变量！所以这里用临时变量
				final ArrayList<String> tempThumbPictures = new ArrayList<String>();

				// 原图必须要先压缩，重新保存，thumbnailPictures压缩后的图片路径
				for (String orgPath : orgPictures) {

					if (orgPath.endsWith(".gif")) {
						tempThumbPictures.add(orgPath);
					} else {
						// 从picturePath取出图片 + 第一步压缩 + 旋转
						ImageCompress compress = new ImageCompress();
						ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
						options.filePath = orgPath;
						Bitmap mBitmap = compress.compressFromUri(
								AlbumActivity.this, options);
						Bitmap mBitmap1 = compress.compressFromUriNoCut(
								AlbumActivity.this, options);
						if (mBitmap == null) {// 跳过错误的图片
							continue;
						}
						// 将上一步的图片再次压缩到100K左右（和微信微博一致） 并保存到自己的新目录下，返回新的全路径
						String newPicturePath = BitmapUtil
								.saveMyBitmapWithCompress(orgPath, mBitmap, 80);
						String newPicturePath1 = BitmapUtil
								.saveMyBitmapWithCompress(orgPath, mBitmap1, 80);
						tempThumbPictures.add(newPicturePath);
						tempThumbPictures.add(newPicturePath1);
					}

				}

				// 上传到服务器
				AlbumListResponse response = null;
				try {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("userid", getUserID());

					// 一次http请求将所有图片+参数上传
					response = JsonParser.getAlbumListResponse(HttpUtil.upload(
							data, tempThumbPictures, HttpUtil.IP
									+ "album/release"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (response != null) {
					handler.sendMessage(handler.obtainMessage(
							BaseActivity.NETWORK_OTHER, response));
				} else {
					handler.sendEmptyMessage(BaseActivity.NETWORK_SUCCESS_DATA_ERROR);
				}

				// 处理完毕，回到主线程显示在界面上
				AlbumActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 清除上次的 压缩图
						// 删除临时的100K左右的图片
						for (String thumbnailPath : tempThumbPictures) {
							File thumbnailPhoto = new File(thumbnailPath);
							if (thumbnailPhoto.exists()) {
								thumbnailPhoto.delete();
							}
						}
						tempThumbPictures.clear();
					}
				});

//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	/**
	 * 群组成员gridadapter
	 */
	public class MemberGridAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		private List<AlbumBean> memberList;
		private boolean isOwner;

		public MemberGridAdapter(List<AlbumBean> memberList, Context mContext) {
			this.memberList = memberList;
			this.isOwner = getUserID().equals(currentUserid);
			this.mLayoutInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// 如果不是创建者或者没有相应权限，不提供加减人按钮
			return memberList.size() + (isOwner ? 1 : 0);
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.griditem_album_delete, null);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewHolder.delete_btn = convertView
						.findViewById(R.id.delete_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyGridViewHolder) convertView.getTag();
			}
			if (position < memberList.size()) {
				// 这里面都是安全的 用户bean
				final AlbumBean memberBean = memberList.get(position);

				if (isOwner) {
					// 删除
					viewHolder.delete_btn.setVisibility(View.VISIBLE);

					viewHolder.delete_btn
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									SureOrCancelDialog followDialog = new SureOrCancelDialog(
											AlbumActivity.this,
											"删除掉该张图片",
											"好",
											new SureOrCancelDialog.SureButtonClick() {

												@Override
												public void onSureButtonClick() {
													// TODO Auto-generated
													progress.show();
													deleteAlbum(memberBean.getId(), getUserID());
												}
											});
									followDialog.show();
								}
							});
				} else {
					// 去掉删除按钮
					viewHolder.delete_btn.setVisibility(View.GONE);
				}

				ImageLoader.getInstance().displayImage(
						memberBean.getPicture().findSmallUrl(),
						viewHolder.imageView);

			} else {
				// 加号图片 去掉删除按钮
				viewHolder.delete_btn.setVisibility(View.GONE);

				viewHolder.imageView
						.setImageResource(R.drawable.group_edit_member_add);
			}
			return convertView;
		}
	}

	private static class MyGridViewHolder {
		private ImageView imageView;
		private View delete_btn;
	}

	// 删除某条动态
	private void deleteAlbum(String albumID, String uid) {
		new Thread(new DeleteAlbumRun(albumID, uid)).start();
	}

	private class DeleteAlbumRun implements Runnable {
		private String albumID;
		private String uid;

		public DeleteAlbumRun(String albumID, String uid) {
			this.albumID = albumID;
			this.uid = uid;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", uid);
				data.put("id", albumID);
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "album/delete"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new StringResponse();
				response.setMessage("网络访问失败");
			}
			response.setTag(albumID);
			handler.sendMessage(handler.obtainMessage(ALBUM_DELECT, response));
		}
	};
}
