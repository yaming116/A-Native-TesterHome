package com.testerhome.nativeandroid.db;

import android.content.Context;

import greendao.DaoMaster;
import greendao.DaoSession;

/**
 * Created by cvtpc on 2015/4/6.
 */
public class DBManager {
    private static Context sContext;
    private static volatile DBManager sInstance = null;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private DBManager() {
        if (sContext == null)
            throw new IllegalAccessError("should initial DbManager before calling this constructor.");
    }

    public static DBManager getInstance() {
        if (sInstance == null)
            sInstance = new DBManager();
        return sInstance;
    }

    public static void init(Context paramContext) {
        sContext = paramContext;
    }

    public DaoMaster getDaoMaster() {
        if (this.daoMaster == null)
            this.daoMaster = new DaoMaster(new DaoMaster.DevOpenHelper(sContext, "testerhome.db", null).getWritableDatabase());
        return this.daoMaster;
    }

    public DaoSession getDaoSession() {
        if (this.daoSession == null) {
            if (this.daoMaster == null)
                this.daoMaster = getDaoMaster();
            this.daoSession = this.daoMaster.newSession();
        }
        return this.daoSession;
    }
}