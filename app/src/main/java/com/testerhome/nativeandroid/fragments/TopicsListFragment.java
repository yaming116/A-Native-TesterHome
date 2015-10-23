package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.db.DBManager;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.models.TopicsResponse;
import com.testerhome.nativeandroid.models.UserEntity;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.dao.query.QueryBuilder;
import greendao.TopicDB;
import greendao.TopicDBDao;
import greendao.UserDB;
import greendao.UserDBDao;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class TopicsListFragment extends BaseFragment implements Callback<TopicsResponse> {

    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicsListAdapter mAdatper;

    private String type;
    private int nodeId;


    public static TopicsListFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("type", type);
        TopicsListFragment fragment = new TopicsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TopicsListFragment newInstance(int nodeId) {
        Bundle args = new Bundle();
        args.putInt("nodeId", nodeId);
        TopicsListFragment fragment = new TopicsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getString("type");
        if (type == null) {
            nodeId = getArguments().getInt("nodeId");
        }

        List<TopicEntity> topicEntities = getDataFromDB();
        if (topicEntities.size()==0){
            loadTopics(true);
        }else{
            mAdatper.setItems(topicEntities);
            hideLoadingView();
        }

    }

    @Override
    protected void setupView() {
        mAdatper = new TopicsListAdapter(getActivity());
        mAdatper.setListener(new TopicsListAdapter.EndlessListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 0) {
                    loadTopics(false);
                }
            }
        });

        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdatper);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNextCursor = 0;
                loadTopics(false);
            }
        });

    }

    private void loadTopics(boolean showloading) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (showloading)
            showLoadingView();

        if (type != null) {
            TesterHomeApi.getInstance().getTopicsService().getTopicsByType(type,
                    mNextCursor * 20, this);
        } else {
            TesterHomeApi.getInstance().getTopicsService().getTopicsByNodeId(nodeId,
                    mNextCursor * 20, this);
        }

    }


    @Override
    public void success(TopicsResponse topicsResponse, Response response) {
        hideLoadingView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        if (topicsResponse.getTopics().size() > 0) {
            if (mNextCursor == 0) {
                updateDaoDb(topicsResponse);
                mAdatper.setItems(topicsResponse.getTopics());
            } else {
                mAdatper.addItems(topicsResponse.getTopics());
            }
            if (topicsResponse.getTopics().size() == 20) {
                mNextCursor += 1;
            } else {
                mNextCursor = 0;
            }

        } else {
            mNextCursor = 0;
        }
    }



    private List<TopicEntity> getDataFromDB() {
        TopicDBDao topicDBDao = DBManager.getInstance().getDaoSession().getTopicDBDao();
        UserDBDao userDBDao = DBManager.getInstance().getDaoSession().getUserDBDao();
        QueryBuilder<TopicDB> qb = topicDBDao.queryBuilder();
        List<TopicDB> topicDBList ;
        List<TopicEntity> topicList = new ArrayList<>();
        if (type==null){
            qb.where(TopicDBDao.Properties.Node_id.eq(19));
            qb.orderDesc(TopicDBDao.Properties.Created_at);
            topicDBList = qb.list();
        }else if(type.equals(Config.TOPICS_TYPE_RECENT)){
            qb.orderDesc(TopicDBDao.Properties.Created_at);
            topicDBList = qb.list();
        }else if(type.equals(Config.TOPICS_TYPE_POPULAR)){
            qb.where(TopicDBDao.Properties.Is_hot.eq(1));
            qb.orderDesc(TopicDBDao.Properties.Replied_at);
            topicDBList = qb.list();
        }else if(type.equals(Config.TOPICS_TYPE_NO_REPLY)){
            qb.where(TopicDBDao.Properties.Replies_count.eq(0));
            qb.orderDesc(TopicDBDao.Properties.Created_at);
            topicDBList = qb.list();
        } else if(type.equals(Config.TOPICS_TYPE_EXCELLENT)){
            qb.where(TopicDBDao.Properties.Is_excellent.eq(1));
            qb.orderDesc(TopicDBDao.Properties.Created_at);
            topicDBList = qb.list();
        }else {
            qb.orderDesc(TopicDBDao.Properties.Updated_at);
            topicDBList = qb.list();
        }
        int i=0;
        if (topicDBList.size()<20){
            return topicList;
        }
        for(TopicDB topicItem:topicDBList){
            if(i>=21){
                break;
            }
            i++;
            TopicEntity topic = new TopicEntity();
            topic.setTitle(topicItem.getTitle());
            topic.setId(String.valueOf(topicItem.getId()));
            topic.setCreated_at(StringUtils.timeStampToTime(topicItem.getCreated_at()));
            topic.setUpdated_at(StringUtils.timeStampToTime(topicItem.getUpdated_at()));
            topic.setReplied_at(StringUtils.timeStampToTime(topicItem.getReplied_at()));
            topic.setLast_reply_user_id(topicItem.getLast_reply_user_id());
            topic.setLast_reply_user_login(topicItem.getLast_reply_user_login());
            topic.setNode_id(topicItem.getNode_id());
            topic.setNode_name(topicItem.getNode_name());
            topic.setReplies_count(topicItem.getReplies_count());
            UserEntity user = new UserEntity();
            QueryBuilder<UserDB> userDBQueryBuilder = userDBDao.queryBuilder();
            userDBQueryBuilder.where(UserDBDao.Properties.User_id.eq(topicItem.getUser_id()));
            List<UserDB> userDBList = userDBQueryBuilder.list();
            UserDB userDB = userDBList.get(0);
            user.setId(topicItem.getUser_id());
            user.setAvatar_url(userDB.getAvatar_url());
            user.setLogin(userDB.getLogin());
            user.setName(userDB.getName());
            topic.setUser(user);
            topicList.add(topic);
        }


        return topicList;


    }


    private void updateDaoDb(TopicsResponse topics) {
        TopicDBDao topicDBDao = DBManager.getInstance().getDaoSession().getTopicDBDao();
        UserDBDao userDBDao = DBManager.getInstance().getDaoSession().getUserDBDao();
        List<UserDB> userDBList = new ArrayList<>();
        for(int i=0;i<topics.getTopics().size();i++){
            TopicEntity topic = topics.getTopics().get(i);
            TopicDB topicDB = new TopicDB();
            if(type != null && type.equals(Config.TOPICS_TYPE_POPULAR)){
                topicDB.setIs_hot(true);
                topicDB.setIs_excellent(true);
            }
            topicDB.setId(Long.valueOf(topic.getId() ));
            topicDB.setCreated_at(StringUtils.timeToTimeStamp(topic.getCreated_at()) / 1000);
            topicDB.setLast_reply_user_id(topic.getLast_reply_user_id());
            topicDB.setLast_reply_user_login(topic.getLast_reply_user_login());
            topicDB.setNode_id(topic.getNode_id());
            topicDB.setNode_name(topic.getNode_name());
            topicDB.setTitle(topic.getTitle());
            topicDB.setUser_id(topic.getUser().getId());
            topicDB.setReplied_at(StringUtils.timeToTimeStamp(topic.getReplied_at()) / 1000);
            topicDB.setUpdated_at(StringUtils.timeToTimeStamp(topic.getUpdated_at()) / 1000);
            topicDB.setReplies_count(topic.getReplies_count());
            if(type != null && (type.equals(Config.TOPICS_TYPE_EXCELLENT )|| type.equals(Config.TOPICS_TYPE_POPULAR))){
                topicDB.setIs_excellent(true);
                QueryBuilder<TopicDB> qb  =topicDBDao.queryBuilder();
                qb.where(TopicDBDao.Properties.Id.eq(Long.valueOf(topic.getId())), TopicDBDao.Properties.Is_hot.eq(true));
                if(qb.list().size()==0){
                    topicDBDao.insertOrReplace(topicDB);
                }
            }else{
                QueryBuilder<TopicDB> qb  =topicDBDao.queryBuilder();
                qb.where(TopicDBDao.Properties.Id.eq((Long.valueOf(topic.getId()))));
                if(qb.list().size()==0){
                    topicDBDao.insert(topicDB);
                }else{
                    qb  =topicDBDao.queryBuilder();
                    qb.where(TopicDBDao.Properties.Id.eq((Long.valueOf(topic.getId()))), TopicDBDao.Properties.Is_excellent.notEq(true));
                    if(qb.list().size()!=0){
                        topicDBDao.insertOrReplace(topicDB);
                    }
                }
            }
            UserDB userDB = new UserDB();
            userDB.setUser_id((long) topic.getUser().getId());
            userDB.setLogin(topic.getUser().getLogin());
            userDB.setName(topic.getUser().getName());
            userDB.setAvatar_url(topic.getUser().getAvatar_url());
            userDBList.add(userDB);
        }
        userDBDao.insertOrReplaceInTx(userDBList);

    }

    @Override
    public void failure(RetrofitError error) {
        hideLoadingView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        Log.e("demo", "failure() called with: " + "error = [" + error + "]"
                + error.getUrl());
    }
}
