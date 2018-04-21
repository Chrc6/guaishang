package com.houwei.guaishang.layout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/3/22 0022.
 * http://blog.csdn.net/l_l_b/article/details/50518763
 */

public class DialogUtils {

    /**
     * 标题+内容+2个按钮（取消，确认）
     * 标题在上方左侧，有分割线
     */
    public static NormalDialog getNormalDialog(Context mcontext, String titleString, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        NormalDialog normalDialog = new NormalDialog(mcontext);
        //设置dialog点击空白处不消失
        normalDialog.setCanceledOnTouchOutside(false);
        normalDialog.title(titleString)
                .content(content)
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
        return normalDialog;
    }

    /**
     * 标题+内容+2个按钮（取消，确认）
     * 标题在上方中间，没有分割线
     * 内容在正中间
     */
    public static NormalDialog getDialogMiddle(Context mcontext, String titleString, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        NormalDialog normalDialog = new NormalDialog(mcontext);
        normalDialog.title(titleString).style(NormalDialog.STYLE_TWO)
                .content(content)
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
        normalDialog.setCanceledOnTouchOutside(false);
        return normalDialog;
    }

    /**
     * 黑色背景
     * 是否退出
     * 取消 确定
     */
    public static NormalDialog getBlakeDialog(Context mcontext, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        NormalDialog normalDialog = new NormalDialog(mcontext);
        normalDialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content(content)//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        return normalDialog;
    }

    /**
     * 标题+内容+1个按钮（btn文字自己定）
     * 标题在上方左侧，有分割线
     * 内容在中间左侧
     */
    public static NormalDialog getNormalDialogOneBtn(Context mcontext, String titleString, String content, String btnStr) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        NormalDialog normalDialog = new NormalDialog(mcontext);
        normalDialog.content(content)
                .title(titleString)
                .btnNum(1)
                .btnText(btnStr)//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        return normalDialog;
    }

    /**
     * 标题+内容+3个按钮（btn文字自己定）
     * 标题在上方中间，没有分割线
     * 内容在中间
     */
    public static NormalDialog getNormalDialogThreeBtn(Context mcontext, String titleString, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        NormalDialog normalDialog = new NormalDialog(mcontext);
        normalDialog.title(titleString)
                .content(content)//
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(3)
                .btnText("取消", "确定", "继续逛逛")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        return normalDialog;
    }

    /**
     * 标题+内容+3个按钮（btn文字自己定）
     * 标题在上方中间，没有分割线
     * 内容在中间
     */
    public static NormalDialog getNormalDialogOneBtn(Context mcontext, String titleString, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        NormalDialog normalDialog = new NormalDialog(mcontext);
        normalDialog.title(titleString)
                .content(content)//
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(1)
                .btnText("确定")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut);//
        //.show();
        return normalDialog;
    }

    /**
     * MD风格
     * 标题+内容+2个按钮
     * 标题在上方左侧，没有分割线
     * 内容在中间
     * 按钮在右下角
     */
    public static MaterialDialog getMaterialDialogTwoBtn(Context mcontext, String titleString, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        final MaterialDialog dialog = new MaterialDialog(mcontext);
        dialog.title(titleString)
                .content(content)
                .btnText("取消", "确定")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        return dialog;
    }

    /**
     * MD风格
     * 内容+3个按钮
     * 内容在中间
     * 按钮在右下角
     */
    public static MaterialDialog getMaterialDialogThreeBtns(Context mcontext, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        final MaterialDialog dialog = new MaterialDialog(mcontext);
        dialog.isTitleShow(false)//
                .btnNum(3)
                .content(content)//
                .btnText("确定", "取消", "知道了")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        return dialog;
    }

    /**
     * MD风格
     * 内容+1个按钮
     * 内容在中间
     * 按钮在右下角
     */
    public static MaterialDialog getMaterialDialogOneBtn(Context mcontext, String content) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();
        final MaterialDialog dialog = new MaterialDialog(mcontext);
        dialog.btnNum(1)
                .content(content)//
                .btnText("确定")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut);
        //.show();
        return dialog;
    }

    public static Dialog getCustomDialog(Context context,
                                         int resLayoutId) {
        Dialog dialog = new Dialog(context);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog的位置
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消dialog的默认标题
        View view = LayoutInflater.from(context).inflate(resLayoutId,null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);//点击其他位置可以取消dialog
        return dialog;

    }

    /**
     * 内容是集合
     * 内容item在左侧,带图片
     */
    public static NormalListDialog getNormalListDialog(Context mcontext, String title, ArrayList<DialogMenuItem> mMenuItems) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();

//        ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();
//        mMenuItems.add(new DialogMenuItem("收藏", R.mipmap.ic_launcher));
//        mMenuItems.add(new DialogMenuItem("下载", R.mipmap.ic_launcher));
//        mMenuItems.add(new DialogMenuItem("分享", R.mipmap.ic_launcher));
//        mMenuItems.add(new DialogMenuItem("删除", R.mipmap.ic_launcher));
//        mMenuItems.add(new DialogMenuItem("歌手", R.mipmap.ic_launcher));
//        mMenuItems.add(new DialogMenuItem("专辑", R.mipmap.ic_launcher));

        final NormalListDialog dialog = new NormalListDialog(mcontext, mMenuItems);
        dialog.title(title)
                .titleTextSize_SP(18)
                .titleBgColor(Color.parseColor("#FF4323"))
                .itemPressColor(Color.parseColor("#cccccc"))
                .itemTextColor(Color.parseColor("#333333"))
                .itemTextSize(16)//
                .cornerRadius(5)//
                .widthScale(0.75f)
                .show();
        return dialog;
    }


    /**
     * 内容是集合
     * 内容item在左侧,不带图片
     */
    public static NormalListDialog getNormalListDialogStringArr(Context mcontext, String title, final String[] mStringItems) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();

        // String[] mStringItems = {"收藏", "下载", "分享", "删除", "歌手", "专辑"};
        final NormalListDialog dialog = new NormalListDialog(mcontext, mStringItems);
        dialog.title(title)//
                .titleTextSize_SP(20)
                .titleTextColor(Color.parseColor("#ffffff"))
                .titleBgColor(Color.parseColor("#93D209"))
                .itemPressColor(Color.parseColor("#e6e6e6"))//
                .itemTextColor(Color.parseColor("#333333"))//
                .itemTextSize(16)//
                .cornerRadius(3)//
                .widthScale(0.8f)//
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
        return dialog;
    }

    /**
     * MD风格
     * bottomsheet
     * 内容在中间
     */
    public static ActionSheetDialog getBottomSheetDialog(Context mcontext, String title, String[] stringItems) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();

//        String[] stringItems = {"接收消息并提醒", "接收消息但不提醒", "收进群助手且不提醒", "屏蔽群消息"};
//        ActionSheetDialog dialog = new ActionSheetDialog(mcontext, stringItems, null);
//        dialog.title("选择群消息提醒方式\r\n(该群在电脑的设置:接收消息并提醒)")//
//                .titleTextSize_SP(14.5f)//
//                .show();

        //  String[] stringItems = {"接收消息并提醒", "接收消息但不提醒", "收进群助手且不提醒", "屏蔽群消息"};
        ActionSheetDialog dialog = new ActionSheetDialog(mcontext, stringItems, null);
        dialog.title(title)//
                .titleTextSize_SP(14.5f)//
                .show();
        return dialog;
    }

    /**
     * MD风格
     * bottomsheet  没有标题
     */
    public static ActionSheetDialog getBottomSheetDialogNoTitle(Context mcontext, String[] stringItems) {
        BounceTopEnter mBasIn = new BounceTopEnter();
        SlideBottomExit mBasOut = new SlideBottomExit();


        ActionSheetDialog dialog = new ActionSheetDialog(mcontext, stringItems, null);
        dialog.isTitleShow(false).show();
        return dialog;
    }

//    /**
//     * MD风格
//     * 带编辑框的dialog
//     */
//    public static CustomEditDialog getEditDialog(Context mcontext) {
//        final CustomEditDialog dialog = new CustomEditDialog(mcontext);
//        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
//
//        return dialog;
//    }


    static ProgressDialog progressDlg = null;

    /**
     * 启动进度条
     *
     * @param strMessage 进度条显示的信息
     * @param //         当前的activity
     */
    public static void showProgressDlg(Context ctx, String strMessage) {
        if (ctx == null) return;
        progressDlg = new ProgressDialog(ctx);
        //设置进度条样式
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //提示的消息
        progressDlg.setMessage(strMessage);
        progressDlg.setIndeterminate(false);
        progressDlg.setCancelable(true);
        if (progressDlg.isShowing()) {
            return;
        }
        progressDlg.show();
    }

    /**
     * 结束进度条
     */
    public static void stopProgressDlg() {
        if (null != progressDlg && progressDlg.isShowing()) {
            progressDlg.dismiss();
            progressDlg = null;
        }
    }

}
