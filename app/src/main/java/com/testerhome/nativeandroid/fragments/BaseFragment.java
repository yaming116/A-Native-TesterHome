package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Bin Li on 2015/9/15.
 */
public abstract class BaseFragment extends Fragment {

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
    }

    @Nullable
    @Bind(android.R.id.empty)
    View mEmptyView;

    protected void showLoadingView(){
        if (mEmptyView != null){
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    protected void hideLoadingView(){
        if (mEmptyView != null){
            mEmptyView.setVisibility(View.GONE);
        }
    }
}
