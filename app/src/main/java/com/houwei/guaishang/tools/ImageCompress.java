package com.houwei.guaishang.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.easemob.util.DensityUtil;
import com.facebook.stetho.common.android.ViewUtil;
import com.houwei.guaishang.util.DeviceUtils;

public class ImageCompress {
	 public static final String CONTENT = "content";
	    public static final String FILE = "file";
	 
	    /**
	     * 图片压缩参数
	     * 
	     * @author Administrator
	     * 
	     */
	    public static class CompressOptions {
	        public static final int DEFAULT_WIDTH = 640;
	        public static final int DEFAULT_HEIGHT = 1136;
	 
	        
	        public int maxWidth = DEFAULT_WIDTH;
	        public int maxHeight = DEFAULT_HEIGHT;
	        /**
	         * 压缩后图片保存的文件
	         */
	        public File destFile;
	        /**
	         * 图片压缩格式,默认为jpg格式
	         */
	        public CompressFormat imgFormat = CompressFormat.JPEG;
	 
	        /**
	         * 图片压缩比例 默认为30
	         */
	        public int quality = 80;
	 
	        public  String filePath;
	    }
	 
	    /**
	     * 图片尺寸缩小为640 *1136
	     * @param context
	     * @param compressOptions
	     * @return
	     */
	    public Bitmap compressFromUri(Context context,
	            CompressOptions compressOptions) {

	        // uri指向的文件路径
	        String filePath = compressOptions.filePath;
	         
	        if (null == filePath) {
	            return null;
	        }
	 
	    	int orientation = BitmapUtil.readPictureDegree(filePath);

	    	if (orientation!=0) {
	    		compressOptions.maxWidth = CompressOptions.DEFAULT_HEIGHT;
	    		compressOptions.maxHeight = CompressOptions.DEFAULT_WIDTH;
			}
	    	
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	 
	        Bitmap temp = BitmapFactory.decodeFile(filePath, options);
	 
	        int actualWidth = options.outWidth;
	        int actualHeight = options.outHeight;

	        int desiredWidth = DeviceUtils.getScreenWid(context);
	        int desiredHeight = DeviceUtils.dip2px(context,110);
	        options.inJustDecodeBounds = false;
	        options.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
	                desiredWidth, desiredHeight);

	        Bitmap bitmap = null;
	 
	        Bitmap destBitmap = BitmapFactory.decodeFile(filePath, options);
	 
	        if(destBitmap == null)
	        	return null;

			// If necessary, scale down to the maximal acceptable size.
			if (destBitmap.getWidth() > desiredWidth
	                || destBitmap.getHeight() > desiredHeight) {
				int height = destBitmap.getHeight();
				int width = destBitmap.getWidth();
				int retY;
				if (height > desiredHeight){
					 retY = (height-desiredHeight)/2;
				}else {
					retY = height;
					desiredHeight = height;
				}

				bitmap = Bitmap.createBitmap(destBitmap,0,retY,width,desiredHeight,null,true);
	            destBitmap.recycle();
	        } else {
	            bitmap = destBitmap;
	        }
	 
	        //特别小的图片，可以将其放大
	        if (destBitmap.getWidth() < desiredWidth
	                && destBitmap.getHeight() < desiredHeight) {
	        	 bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth,
		                    desiredHeight, true);
		        destBitmap.recycle();
			}
	        
	    	if (orientation != 0) {
	    		int degress = Integer.valueOf(orientation);
	    		bitmap = BitmapUtil.adjustPhotoRotation(bitmap, degress);
			}
	    	
	        // compress file if need
	        if (null != compressOptions.destFile) {
	            compressFile(compressOptions, bitmap);
	        }
	        Log.d("lei","处理完图片的大小：宽度"+bitmap.getWidth()+"高度是："+bitmap.getHeight());
	        return bitmap;
	    }
	/**
	 * 图片尺寸缩小为640 *1136
	 * @param context
	 * @param compressOptions
	 * @return
	 */
	public Bitmap compressFromUriNoCut(Context context,
								  CompressOptions compressOptions) {

		// uri指向的文件路径
		String filePath = compressOptions.filePath;

		if (null == filePath) {
			return null;
		}

		int orientation = BitmapUtil.readPictureDegree(filePath);

		if (orientation!=0) {
			compressOptions.maxWidth = CompressOptions.DEFAULT_HEIGHT;
			compressOptions.maxHeight = CompressOptions.DEFAULT_WIDTH;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Bitmap temp = BitmapFactory.decodeFile(filePath, options);

		int actualWidth = options.outWidth;
		int actualHeight = options.outHeight;

		int desiredWidth = getResizedDimension(compressOptions.maxWidth,
				compressOptions.maxHeight, actualWidth, actualHeight);
		int desiredHeight = getResizedDimension(compressOptions.maxHeight,
				compressOptions.maxWidth, actualHeight, actualWidth);

		options.inJustDecodeBounds = false;
		options.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
				desiredWidth, desiredHeight);

		Bitmap bitmap = null;

		Bitmap destBitmap = BitmapFactory.decodeFile(filePath, options);

		if(destBitmap == null)
			return null;

		// If necessary, scale down to the maximal acceptable size.
		if (destBitmap.getWidth() > desiredWidth
				|| destBitmap.getHeight() > desiredHeight) {
			bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth,
					desiredHeight, true);
			destBitmap.recycle();
		} else {
			bitmap = destBitmap;
		}

		//特别小的图片，可以将其放大
//	        if (destBitmap.getWidth() < desiredWidth
//	                && destBitmap.getHeight() < desiredHeight) {
//	        	 bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth,
//		                    desiredHeight, true);
//		        destBitmap.recycle();
//			}

		if (orientation != 0) {
			int degress = Integer.valueOf(orientation);
			bitmap = BitmapUtil.adjustPhotoRotation(bitmap, degress);
		}

		// compress file if need
		if (null != compressOptions.destFile) {
			compressFile(compressOptions, bitmap);
		}

		return bitmap;
	}
	    /**
	     * compress file from bitmap with compressOptions
	     * 
	     * @param compressOptions
	     * @param bitmap
	     */
	    private void compressFile(CompressOptions compressOptions, Bitmap bitmap) {
	        OutputStream stream = null;
	        try {
	            stream = new FileOutputStream(compressOptions.destFile);
	        } catch (FileNotFoundException e) {
	            Log.e("ImageCompress", e.getMessage());
	        }
	 
	        bitmap.compress(compressOptions.imgFormat, compressOptions.quality,
	                stream);
	    }
	 
	    private static int findBestSampleSize(int actualWidth, int actualHeight,
	            int desiredWidth, int desiredHeight) {
	        double wr = (double) actualWidth / desiredWidth;
	        double hr = (double) actualHeight / desiredHeight;
	        double ratio = Math.min(wr, hr);
	        float n = 1.0f;
	        while ((n * 2) <= ratio) {
	            n *= 2;
	        }
	 
	        return (int) n;
	    }
	 
	    private static int getResizedDimension(int maxPrimary, int maxSecondary,
	            int actualPrimary, int actualSecondary) {
	        // If no dominant value at all, just return the actual.
	        if (maxPrimary == 0 && maxSecondary == 0) {
	            return actualPrimary;
	        }
	 
	        // If primary is unspecified, scale primary to match secondary's scaling
	        // ratio.
	        if (maxPrimary == 0) {
	            double ratio = (double) maxSecondary / (double) actualSecondary;
	            return (int) (actualPrimary * ratio);
	        }
	 
	        if (maxSecondary == 0) {
	            return maxPrimary;
	        }
	 
	        double ratio = (double) actualSecondary / (double) actualPrimary;
	        int resized = maxPrimary;
	        if (resized * ratio > maxSecondary) {
	            resized = (int) (maxSecondary / ratio);
	        }
	        return resized;
	    }
	 
	    /**
	     * 获取文件的路径
	     *

	     * @return
	     */
	    private String getFilePath(Context context, Uri uri) {
	 
	        String filePath = null;
	 
	        if (CONTENT.equalsIgnoreCase(uri.getScheme())) {
	 
	            Cursor cursor = context.getContentResolver().query(uri,
	                    new String[] { Images.Media.DATA }, null, null, null);
	 
	            if (null == cursor) {
	                return null;
	            }
	 
	            try {
	                if (cursor.moveToNext()) {
	                    filePath = cursor.getString(cursor
	                            .getColumnIndex(Images.Media.DATA));
	                }
	            } finally {
	                cursor.close();
	            }
	        }
	 
	        // 从文件中选择
	        if (FILE.equalsIgnoreCase(uri.getScheme())) {
	            filePath = uri.getPath();
	        }
	 
	        return filePath;
	    }
}
