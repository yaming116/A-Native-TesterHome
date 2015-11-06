package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.NotificationResponse;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.views.adapters.NotificationAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.Bind;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by cvtpc on 2015/10/18.
 */
public class AccountNotificationFragment extends BaseFragment {

    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private NotificationAdapter mAdatper;


    public static AccountNotificationFragment newInstance() {
        AccountNotificationFragment fragment = new AccountNotificationFragment();
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mTesterHomeAccount == null) {
            getUserInfo();
        }
        setupView();
        loadNotification(true);

    }

    @Override
    protected void setupView() {
        mAdatper = new NotificationAdapter(getActivity());
        mAdatper.setListener(new NotificationAdapter.EndlessListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 0) {
                    loadNotification(false);
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdatper);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNextCursor = 0;
                loadNotification(false);
            }
        });


    }

    private TesterUser mTesterHomeAccount;

    private void getUserInfo() {
        mTesterHomeAccount = TesterHomeAccountService.getInstance(getContext()).getActiveAccountInfo();
    }


    private void loadNotification(boolean showloading) {
        if (showloading)
            showLoadingView();

        Call<NotificationResponse> call =
                TesterHomeApi.getInstance().getTopicsService().getNotifications(mTesterHomeAccount.getAccess_token(),
                        mNextCursor * 20);

        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Response<NotificationResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    hideLoadingView();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (response.body().getNotifications().size() > 0) {
                        if (mNextCursor == 0) {
                            mAdatper.setItems(response.body().getNotifications());
                        } else {
                            mAdatper.addItems(response.body().getNotifications());
                        }
                        if (response.body().getNotifications().size() == 20) {
                            mNextCursor += 1;
                        } else {
                            mNextCursor = 0;
                        }

                    } else {
                        mNextCursor = 0;
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                hideLoadingView();
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.e("demo", "failure() called with: " + "error = [" + t.getMessage() + "]", t);
            }
        });

    }


}
