package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.BuildConfig;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.SearchResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class SearchResultFragment extends BaseFragment implements Callback<SearchResponse> {

    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 1;

    private TopicsListAdapter mAdatper;

    private String keyword;

    public static SearchResultFragment newInstance(String keyword) {
        Bundle args = new Bundle();
        args.putString("keyword", keyword);
        SearchResultFragment fragment = new SearchResultFragment();
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
        keyword = getArguments().getString("keyword");

        loadTopics(true);
    }


    @Override
    protected void setupView() {
        mAdatper = new TopicsListAdapter(getActivity());
        mAdatper.setListener(new TopicsListAdapter.EndlessListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 1) {
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
                mNextCursor = 1;
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


        if (keyword != null) {
            RestAdapterUtils.getRestAPI(getActivity()).searchTopicsByKeyword(keyword,
                    mNextCursor * 20).enqueue(this);
        }

    }

    @Override
    public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {

        if (BuildConfig.DEBUG)
            Log.e("cache", "cache header = [" + response.headers().toString() + "]");

        hideEmptyView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        Log.e("retrofit", response.raw().request().urlString());
        if (response.body() != null && response.body().getTopics().size() > 0) {
            if (mNextCursor == 1) {
                mAdatper.setItems(response.body().getTopics());
            } else {
                mAdatper.addItems(response.body().getTopics());
            }
            if (response.body().getTopics().size() >= 10) {
                mNextCursor += 1;
            } else {
                mNextCursor = 1;
            }

        } else {
            mNextCursor = 1;
        }
    }

    @Override
    public void onFailure(Throwable t) {
        hideEmptyView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (BuildConfig.DEBUG)
            Log.e("cache", "failure() called with: " + "error = [" + t.getMessage() + "]", t);
    }
}