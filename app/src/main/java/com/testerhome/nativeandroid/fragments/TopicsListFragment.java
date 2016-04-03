package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.testerhome.nativeandroid.BuildConfig;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.BannerEntity;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.models.TopicsResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class TopicsListFragment extends BaseFragment implements Observer<TopicsResponse> {

    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicsListAdapter mAdatper;

    private String type;
    private int nodeId;


    public static TopicsListFragment newInstance(String type) {
        Log.d("TopicListFragment","newInstance");
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

        loadTopics(true);
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
            showEmptyView();
        if (type != null) {

            RestAdapterUtils.getRestAPI(getActivity()).getTopicsByType(type,mNextCursor * 20)

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
            
            
        } else {
            RestAdapterUtils.getRestAPI(getActivity()).getTopicsByNodeId(nodeId, mNextCursor * 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        hideEmptyView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (BuildConfig.DEBUG)
            Log.e("cache", "failure() called with: " + "error = [" + e.getMessage() + "]", e);
        if (mNextCursor == 0){
            showErrorView("无法加载数据,请检查网络");
        }
    }

    @Override
    public void onNext(TopicsResponse response) {
        hideEmptyView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (response != null && response.getTopics().size() > 0) {
            if (mNextCursor == 0) {
                if (!TextUtils.isEmpty(type) && type.equals(Config.TOPICS_TYPE_RECENT)) {
                    response.getTopics().add(0, new TopicEntity(TopicsListAdapter.TOPIC_LIST_TYPE_BANNER, new ArrayList<BannerEntity>()));
                }
                mAdatper.setItems(response.getTopics());
            } else {
                mAdatper.addItems(response.getTopics());
            }
            if (response.getTopics().size() >= 20) {
                mNextCursor += 1;
            } else {
                mNextCursor = 0;
            }

        } else {
            if (mNextCursor == 0){
                showErrorView("无法加载数据,请检查网络");
            }
            mNextCursor = 0;
        }
    }
}
