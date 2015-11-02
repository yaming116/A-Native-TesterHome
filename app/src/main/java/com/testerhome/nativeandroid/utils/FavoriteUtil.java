package com.testerhome.nativeandroid.utils;

import android.content.Context;
import android.widget.Toast;

import com.testerhome.nativeandroid.dao.DBManager;
import com.testerhome.nativeandroid.dao.Favorite;
import com.testerhome.nativeandroid.dao.FavoriteDao;
import com.testerhome.nativeandroid.models.TopicEntity;

import java.util.List;

/**
 * Created by Bin Li on 2015/11/2.
 */
public class FavoriteUtil {

    public static boolean hasFavorite(Context context, String topicId) {
        DBManager dbManager = DBManager.getInstance(context);
        return dbManager.getFavoriteDao().queryBuilder().where(FavoriteDao.Properties.Id.eq(topicId)).count() > 0;
    }

    public static void delFavorite(Context context, String topicId){
        DBManager.getInstance(context).getFavoriteDao().deleteByKey(topicId);
        Toast.makeText(context, "取消收藏！", Toast.LENGTH_SHORT).show();
    }

    public static void addTopicToFavorite(Context context, TopicEntity topicEntity) {
        FavoriteDao favoriteDao = DBManager.getInstance(context).getFavoriteDao();
        favoriteDao.insert(new Favorite(topicEntity.getId(),
                topicEntity.getTitle(), topicEntity.getCreated_at(), topicEntity.getNode_name(), topicEntity.getUser().getId(),
                topicEntity.getUser().getLogin(), topicEntity.getUser().getName(), topicEntity.getUser().getAvatar_url()));

        Toast.makeText(context, "收藏成功！", Toast.LENGTH_SHORT).show();
    }

    public static List<Favorite> getFavorites(Context context) {
        return DBManager.getInstance(context).getFavoriteDao().queryBuilder().list();
    }
}
