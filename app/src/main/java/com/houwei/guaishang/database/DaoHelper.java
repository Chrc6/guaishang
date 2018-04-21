package com.houwei.guaishang.database;

import com.easemob.util.PathUtil;
import com.houwei.guaishang.database.entity.ChatInfoDataDao;
import com.houwei.guaishang.database.entity.DaoMaster;
import com.houwei.guaishang.database.entity.DaoSession;
import com.houwei.guaishang.database.entity.HomeTopicCacheDataDao;
import com.houwei.guaishang.tools.ApplicationProvider;

/**
 * Created by *** on 2018/4/6.
 */

public class DaoHelper {

    private static DaoHelper instance;
    private static DaoSession daoSession;


    public static DaoHelper getDaoHelper() {
        if (instance == null) {
            synchronized (DaoHelper.class) {
                if (instance == null) {
                    instance = new DaoHelper();
                    DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(ApplicationProvider.privode().getApplicationContext(), "houwei_guaishang.db", null);
                    DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
                    daoSession = daoMaster.newSession();
                }
            }
        }
        return instance;
    }

    public HomeTopicCacheDataDao getHomeTopicCacheDataDao() {
        return daoSession.getHomeTopicCacheDataDao();
    }

    public ChatInfoDataDao getChatInfoDataDao(){
        return daoSession.getChatInfoDataDao();
    }
}
