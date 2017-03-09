package com.yxd.greendaotest;

import android.database.sqlite.SQLiteDatabase;

import com.yxd.greendaotest.gen.DaoMaster;
import com.yxd.greendaotest.gen.DaoSession;
import com.yxd.greendaotest.gen.UserDao;

/**
 * Created by a on 2017/2/15.
 */

public class GreenDaoManager {

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private SQLiteDatabase db;
    private UserDao mUserDao;
    private static GreenDaoManager mInstance;

    private GreenDaoManager(){
        if (mInstance == null){
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MyApplication.getContext(), "user1-db", null);
            db = helper.getWritableDatabase();
            mDaoMaster = new DaoMaster(db);
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public static GreenDaoManager getInstance(){
        if (mInstance == null){
            synchronized (GreenDaoManager.class){
                if (mInstance == null){
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    public DaoMaster getDaoMaster(){
        return mDaoMaster;
    }

    public DaoSession getSession(){
        return mDaoSession;
    }

    public DaoSession getNewSession(){
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

    public SQLiteDatabase getDb(){
        return db;
    }
}
