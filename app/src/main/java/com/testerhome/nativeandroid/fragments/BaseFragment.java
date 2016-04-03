package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.application.NativeApp;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Bin Li on 2015/9/15.
 */
public abstract class BaseFragment extends Fragment {

    protected Subscription mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.bind(this, view);

        setupView();
        return view;
    }

    protected void setupView(){

    }

    protected abstract int getLayoutRes();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        unsubscribe();
        NativeApp.getRefWatcher().watch(this);
    }

    protected void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Nullable
    @Bind(android.R.id.empty)
    View mEmptyView;

    protected void showEmptyView(){
        if (mEmptyView != null){
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    protected void hideEmptyView(){
        if (mEmptyView != null){
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Bind(R.id.empty_load)
    View mEmptyLoad;
    protected void hideLoading(){
        if (mEmptyLoad != null){
            mEmptyLoad.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Bind(R.id.error_panel)
    View mErrorView;

    @Nullable
    @Bind(R.id.error_subtitle)
    TextView mErrorText;

    protected void showErrorView(String errorMessage){
        showEmptyView();
        if (mErrorView != null){
            if (mErrorText != null){
                mErrorText.setText(errorMessage);
            }
            mErrorView.setVisibility(View.VISIBLE);
            hideLoading();
        }
    }
}
