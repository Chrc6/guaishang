package com.houwei.guaishang.easemob;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.houwei.guaishang.R;
import com.houwei.guaishang.easemob.EaseEmojicon.Type;
import com.houwei.guaishang.manager.FaceManager;

public class EmojiconGridAdapter extends ArrayAdapter<EaseEmojicon>{

    private Type emojiconType;
    private LayoutInflater inflater;

    public EmojiconGridAdapter(Context context, int textViewResourceId, List<EaseEmojicon> objects, EaseEmojicon.Type emojiconType) {
        super(context, textViewResourceId, objects);
        this.emojiconType = emojiconType;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            if(emojiconType == Type.BIG_EXPRESSION){
                convertView = View.inflate(getContext(), R.layout.ease_row_big_expression, null);
            }else{
                convertView = inflater.inflate(R.layout.ease_row_expression,  parent, false);
            }
        }
        
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
        EaseEmojicon emojicon = getItem(position);
        if(textView != null && emojicon.getName() != null){
            textView.setText(emojicon.getName());
        }
        if(FaceManager.DELETE_KEY.equals(emojicon.getEmojiText())){
            imageView.setImageResource(R.drawable.emotion_del_selector);
        }else{
            if(emojicon.getIcon() != 0){
                imageView.setImageResource(emojicon.getIcon());
            }else if(emojicon.getIconPath() != null){
                Glide.with(getContext()).load(emojicon.getIconPath()).into(imageView);
            }
        }
        
        
        return convertView;
    }
    
}
