package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.TopicReplyResponse;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.views.adapters.TopicReplyAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.Bind;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by cvtpc on 2015/10/16.
 */
public class TopicReplyFragment extends BaseFragment {
    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicReplyAdapter mAdatper;
    private String mTopicId;

    public static TopicReplyFragment newInstance(String id, ReplyUpdateListener listener) {
        Bundle args = new Bundle();
        args.putString("id", id);
        TopicReplyFragment fragment = new TopicReplyFragment();
        fragment.setReplyUpdateListener(listener);
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
        mTopicId = getArguments().getString("id");
        loadTopicReplies(true);
    }

    @Override
    protected void setupView() {

        mAdatper = new TopicReplyAdapter(getActivity());
        mAdatper.setListener(new TopicReplyAdapter.TopicReplyListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 0) {
                    loadTopicReplies(false);
                }
            }

            @Override
            public void onReplyClick(String replyInfo) {
                mReplyUpdateListener.updateReplyTo(replyInfo);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdatper);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNextCursor = 0;
                loadTopicReplies(false);
            }
        });
    }

    public void refreshReply(){
        loadTopicReplies(true);
    }

    private void loadTopicReplies(boolean showloading) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (showloading)
            showLoadingView();

        Call<TopicReplyResponse> call=
        TesterHomeApi.getInstance().getTopicsService().getTopicsReplies(mTopicId,
                mNextCursor * 20);

        call.enqueue(new Callback<TopicReplyResponse>() {
            @Override
            public void onResponse(Response<TopicReplyResponse> response, Retrofit retrofit) {
                hideLoadingView();
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (response.body()!=null && response.body().getTopicReply().size() > 0) {

                    if (mNextCursor == 0) {
                        mAdatper.setItems(response.body().getTopicReply());
                    } else {
                        mAdatper.addItems(response.body().getTopicReply());
                    }

                    if (response.body().getTopicReply().size() == 20) {
                        mNextCursor += 1;
                    } else {
                        mNextCursor = 0;
                    }
                } else {
                    mNextCursor = 0;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                hideLoadingView();
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.e("demo", "failure() called with: " + "error = [" + t.getMessage() + "]"
                        , t);
            }
        });

    }

    public void scrollToEnd() {
        recyclerViewTopicList.scrollToPosition(mAdatper.getItemCount());
    }

    public interface ReplyUpdateListener{
        void updateReplyTo(String replyInfo);
    }

    private ReplyUpdateListener mReplyUpdateListener;

    public void setReplyUpdateListener(ReplyUpdateListener mReplyUpdateListener) {
        this.mReplyUpdateListener = mReplyUpdateListener;
    }
}
