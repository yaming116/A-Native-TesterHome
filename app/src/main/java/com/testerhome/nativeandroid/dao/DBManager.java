package com.testerhome.nativeandroid.dao;

import android.content.Context;

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

    public FavoriteDao getFavoriteDao(){
        return daoSession.getFavoriteDao();
    }

    public UserPraiseHistoryDao getUserPraiseHistory(){
        return daoSession.getUserPraiseHistoryDao();
    }
}