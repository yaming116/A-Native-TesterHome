package com.testerhome.nativeandroid.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.application.NativeApp;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.AccountFavoriteFragment;
import com.testerhome.nativeandroid.fragments.AccountNotificationFragment;
import com.testerhome.nativeandroid.fragments.AccountTopicsFragment;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.models.UserResponse;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.Bind;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * Created by cvtpc on 2016/1/21.
 */
public class UserInfoActivity extends BackBaseActivity {


    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.id_user_avatar)
    SimpleDraweeView userAvatar;
    @Bind(R.id.login_name)
    TextView userName;
    @Bind(R.id.id_tag_line)
    TextView tagLine;

    private TesterUser mTesterHomeAccount;
    private String loginName;
    private boolean isLoginer ;

    private final String LOGINERVALUE[] = {"我收藏的帖子","我发表的帖子","我的通知"};
    private final String NOMALVALUE[] = {"他收藏的帖子","他发表的帖子"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_info);

        mTesterHomeAccount = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        loginName = getIntent().getStringExtra("loginName");

        if (loginName.equals(mTesterHomeAccount.getLogin())) {
            isLoginer = true;
        } else {
            isLoginer = false;
        }

        setupView();
        initData();

    }


    public boolean enableTheme() {
        return true;
    }

    private void initData() {

        if (!isLoginer) {
            loadUserInfo();
        }else {
            initUserInfo();
        }

    }

    private void loadUserInfo() {
        Call<UserResponse> call =
                TesterHomeApi.getInstance().getTopicsService().getUserInfo(loginName);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(retrofit.Response<UserResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().getUser() != null) {
                    mTesterHomeAccount = response.body().getUser();
                    initUserInfo();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(UserInfoActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void initUserInfo() {

        userAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTesterHomeAccount.getAvatar_url())));
        userName.setText(mTesterHomeAccount.getLogin());
        if (mTesterHomeAccount.getTagline()== null) {
            tagLine.setText("这个人很懒,什么都没有");
        } else {
            tagLine.setText(mTesterHomeAccount.getTagline());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    private void setupView() {

        UserInfoViewPagerAdapter userInfoViewPagerAdapter;
        if (isLoginer) {
            userInfoViewPagerAdapter = new UserInfoViewPagerAdapter(getSupportFragmentManager(),LOGINERVALUE);
        } else {
            userInfoViewPagerAdapter = new UserInfoViewPagerAdapter(getSupportFragmentManager(),NOMALVALUE);
        }
        viewPager.setAdapter(userInfoViewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(userInfoViewPagerAdapter);



//        toolbar.setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        setCustomTitle();
    }



    class UserInfoViewPagerAdapter extends FragmentPagerAdapter {


        private  String userValue[];

        public UserInfoViewPagerAdapter(FragmentManager fm, String[] userValue) {
            super(fm);
            this.userValue = userValue;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AccountFavoriteFragment.newInstance(loginName);
            } else if (position ==1 ) {
                return AccountTopicsFragment.newInstance(loginName);
            } else {
                return AccountNotificationFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return userValue.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return userValue[position];
        }
    }


}
