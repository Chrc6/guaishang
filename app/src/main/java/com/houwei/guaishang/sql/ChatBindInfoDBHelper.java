package com.houwei.guaishang.sql;

import com.houwei.guaishang.database.DaoHelper;
import com.houwei.guaishang.database.entity.ChatInfoData;
import com.houwei.guaishang.database.entity.ChatInfoDataDao;

import java.util.List;

/**
 * Created by lenovo on 2018/4/18.
 * 该数据库是操作 对话框绑定的订单信息
 */

public class ChatBindInfoDBHelper {

    private static class  SingletonHolder{
        private static ChatBindInfoDBHelper Instance = new ChatBindInfoDBHelper();
    }
    public static ChatBindInfoDBHelper g(){
        return SingletonHolder.Instance;
    }

    private ChatInfoDataDao chatBindInfoDataDao;


    private ChatBindInfoDBHelper(){
        if (chatBindInfoDataDao == null){
            chatBindInfoDataDao =  DaoHelper.getDaoHelper().getChatInfoDataDao();
        }
    }

    public void add(String name,String price,String time,String mobile){
        deleteByName(name);
        if (chatBindInfoDataDao != null){
            ChatInfoData data = new ChatInfoData();
            data.setId(System.currentTimeMillis());
            data.setName(name);
            data.setPrice(price);
            data.setTime(time);
            data.setMobile(mobile);
            chatBindInfoDataDao.insert(data);
        }
    }

    private void deleteByName(String name){
        if (chatBindInfoDataDao != null){
            List<ChatInfoData> list = chatBindInfoDataDao.queryBuilder().where(ChatInfoDataDao.Properties.Name.eq(name)).list();
           if (list != null && list.size() > 0){
               int size = list.size();
               for (int i = 0; i < size; i++) {
                   ChatInfoData data = list.get(i);
                   chatBindInfoDataDao.delete(data);
               }
            }
        }
    }

    public ChatInfoData queryByKey(String name){
        if (chatBindInfoDataDao != null){
            List<ChatInfoData> list = chatBindInfoDataDao.queryBuilder().where(ChatInfoDataDao.Properties.Name.eq(name)).list();

            if (list.size() > 0){
                return list.get(0);
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
