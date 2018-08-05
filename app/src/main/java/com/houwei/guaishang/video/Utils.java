package com.houwei.guaishang.video;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Nathen
 * On 2016/02/21 12:25
 */
public class Utils {

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 获取视频文件截图
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */
    public  static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return  media.getFrameAtTime();
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        }
        return bitmap;
    }

    /**

     * Bitmap保存成File

     *

     * @param bitmap input bitmap

     * @param name output file's name

     * @return String output file's path

     */

    public  static String bitmap2File(Bitmap bitmap, String name) {
        File parent = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/videoCover");
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File f = new File(parent, name + ".jpg");

        FileOutputStream fOut = null;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            return null;
        }
        return f.getAbsolutePath();
    }

    public static long getVideoTotalTime(String videoPath) {

        File file = new File(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer();
        long time = 0;
        try {
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            time = mediaPlayer.getDuration();//获得了视频的时长（以毫秒为单位）
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

}
