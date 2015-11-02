package com.testerhome.nativeandroid.utils;

import android.content.Context;
import android.widget.Toast;

import com.testerhome.nativeandroid.dao.DBManager;
import com.testerhome.nativeandroid.dao.UserPraiseHistory;
import com.testerhome.nativeandroid.dao.UserPraiseHistoryDao;
import com.testerhome.nativeandroid.models.TopicEntity;

/**
 * Created by Bin Li on 2015/11/2.
 */
public class PraiseUtil {

    public static boolean hasPraised(Context context, String topicId){
        return DBManager.getInstance(context).getUserPraiseHistory().queryBuilder()
                .where(UserPraiseHistoryDao.Properties.TopicId.eq(topicId)).count() > 0;
    }

    public static void delPraise(Context context, String topicId){
        DBManager.getInstance(context).getUserPraiseHistory().deleteByKey(topicId);
        Toast.makeText(context, "取消点赞", Toast.LENGTH_SHORT).show();
    }

    public static void addPraise(Context context, TopicEntity topicEntity){
        UserPraiseHistoryDao userPraiseHistoryDao = DBManager.getInstance(context).getUserPraiseHistory();

        userPraiseHistoryDao.insert(new UserPraiseHistory(topicEntity.getId(), topicEntity.getUser().getId()));

        Toast.makeText(context, "点赞成功！", Toast.LENGTH_SHORT).show();
    }
}
