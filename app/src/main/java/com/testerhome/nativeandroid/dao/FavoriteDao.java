package com.testerhome.nativeandroid.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.testerhome.nativeandroid.dao.Favorite;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FAVORITE".
*/
public class FavoriteDao extends AbstractDao<Favorite, String> {

    public static final String TABLENAME = "FAVORITE";

    /**
     * Properties of entity Favorite.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Created_at = new Property(2, String.class, "created_at", false, "CREATED_AT");
        public final static Property NodeName = new Property(3, String.class, "nodeName", false, "NODE_NAME");
        public final static Property UserId = new Property(4, Integer.class, "userId", false, "USER_ID");
        public final static Property UserLogin = new Property(5, String.class, "userLogin", false, "USER_LOGIN");
        public final static Property Username = new Property(6, String.class, "username", false, "USERNAME");
        public final static Property AvatarUrl = new Property(7, String.class, "avatarUrl", false, "AVATAR_URL");
    };


    public FavoriteDao(DaoConfig config) {
        super(config);
    }
    
    public FavoriteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FAVORITE\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"TITLE\" TEXT," + // 1: title
                "\"CREATED_AT\" TEXT," + // 2: created_at
                "\"NODE_NAME\" TEXT," + // 3: nodeName
                "\"USER_ID\" INTEGER," + // 4: userId
                "\"USER_LOGIN\" TEXT," + // 5: userLogin
                "\"USERNAME\" TEXT," + // 6: username
                "\"AVATAR_URL\" TEXT);"); // 7: avatarUrl
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FAVORITE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Favorite entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindString(3, created_at);
        }
 
        String nodeName = entity.getNodeName();
        if (nodeName != null) {
            stmt.bindString(4, nodeName);
        }
 
        Integer userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(5, userId);
        }
 
        String userLogin = entity.getUserLogin();
        if (userLogin != null) {
            stmt.bindString(6, userLogin);
        }
 
        String username = entity.getUsername();
        if (username != null) {
            stmt.bindString(7, username);
        }
 
        String avatarUrl = entity.getAvatarUrl();
        if (avatarUrl != null) {
            stmt.bindString(8, avatarUrl);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Favorite readEntity(Cursor cursor, int offset) {
        Favorite entity = new Favorite( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // created_at
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // nodeName
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // userId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // userLogin
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // username
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // avatarUrl
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Favorite entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreated_at(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNodeName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUserId(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setUserLogin(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUsername(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAvatarUrl(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Favorite entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Favorite entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
