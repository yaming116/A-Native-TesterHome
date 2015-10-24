package com.testerhome.nativeandroid.db;

import android.content.Context;

import com.testerhome.nativeandroid.dao.DaoMaster;
import com.testerhome.nativeandroid.dao.DaoSession;
import com.testerhome.nativeandroid.dao.TopicDBDao;
import com.testerhome.nativeandroid.dao.UserDBDao;

/**
 * Created by cvtpc on 2015/4/6.
 */
public class DBManager {

    private static DBManager sInstance = null;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public static synchronized DBManager getInstance(Context ctx) {
        if (sInstance == null)
            sInstance = new DBManager(ctx.getApplicationContext());
        return sInstance;
    }

    private DBManager(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "testerhome.db", null);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public TopicDBDao getTopicDao(){
        return daoSession.getTopicDBDao();
    }

    public UserDBDao getUserDao(){
        return daoSession.getUserDBDao();
    }
}