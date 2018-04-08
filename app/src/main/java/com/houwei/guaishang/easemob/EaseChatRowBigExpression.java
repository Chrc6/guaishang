package com.houwei.guaishang.easemob;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.chat.EMMessage;
import com.houwei.guaishang.R;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.LogUtil;

/**
 * 大表情(动态表情)
 *
 */
public class EaseChatRowBigExpression extends EaseChatRowText{

    private GifImageView imageView;


    public EaseChatRowBigExpression(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }
    
    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? 
                R.layout.row_received_bigexpression : R.layout.row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (GifImageView) findViewById(R.id.image);
    }


    @Override
    public void onSetUpView() {
    	//emojiconId 是gif图片名，比如paopaobing8
        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
       	int gif_drawableid = context.getResources().getIdentifier(emojiconId, "drawable", context.getPackageName());
       	if (gif_drawableid!=0) {
       		//本地res drawable里有这个gif
//       		Glide.with(activity).load(gif_drawableid).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.ease_default_expression).into(imageView);
       		
    		try {
    			GifDrawable gifDrawable = new GifDrawable(getResources(), gif_drawableid);
    			imageView.setImageDrawable(gifDrawable);
    		} catch (NotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
       	
       	}else{
   			//从服务器下载
   			Glide.with(activity).load(HttpUtil.IP_NOAPI+"gifemoji?gifname="+emojiconId+".gif").into(imageView);
   		}
         
      
        handleTextMessage();
    }
}
