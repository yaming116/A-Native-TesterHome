package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.MarkdownFragment;
import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.testerhome.nativeandroid.fragments.TopicReplyFragment;
import com.testerhome.nativeandroid.models.CollectTopicResonse;
import com.testerhome.nativeandroid.models.CreateReplyResponse;
import com.testerhome.nativeandroid.models.PraiseEntity;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.models.TopicDetailEntity;
import com.testerhome.nativeandroid.models.TopicDetailResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.utils.DeviceUtil;
import com.testerhome.nativeandroid.utils.FavoriteUtil;
import com.testerhome.nativeandroid.utils.PraiseUtil;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by vclub on 15/9/17.
 */
public class TopicDetailActivity extends BackBaseActivity implements TopicReplyFragment.ReplyUpdateListener {

    private String mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeEdge(200)
                .setSwipeEdgePercent(0.2f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(0.5f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
                .setClosePercent(0.8f)//触发关闭Activity百分比
                .setSwipeRelateEnable(false)//是否与下一级activity联动(微信效果)。默认关
                .setSwipeRelateOffset(500);//activity联动时的偏移量。默认500px。

        setCustomTitle("帖子详情");

        if (getIntent().hasExtra("topic_id")) {
            mTopicId = getIntent().getStringExtra("topic_id");

            setupView();
            loadInfo();
        } else {
            finish();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public boolean enableTheme() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            final Intent target = new Intent(Intent.ACTION_SEND);
            target.setType("text/plain");
            target.putExtra(Intent.EXTRA_TITLE, tvDetailTitle.getText().toString());
            target.putExtra(Intent.EXTRA_TEXT, String.format("https://testerhome.com/topics/%s", mTopicId));
            startActivity(Intent.createChooser(target, "分享到"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Bind(R.id.tab_layout)
    TabLayout tabLayoutTopicsTab;

    @Bind(R.id.view_pager)
    ViewPager viewPagerTopics;

    private TopicDetailPagerAdapter mAdapter;

    private void setupView() {
        mAdapter = new TopicDetailPagerAdapter(getSupportFragmentManager());
        viewPagerTopics.setAdapter(mAdapter);

        tabLayoutTopicsTab.setupWithViewPager(viewPagerTopics);
        tabLayoutTopicsTab.setTabsFromPagerAdapter(mAdapter);
    }

    private MarkdownFragment mMarkdownFragment;
    private TopicReplyFragment mTopicReplyFragment;

    public void updateReplyCount(int count) {
        if (tvDetailRepliesCount != null) {
            if (count > mTopicEntity.getReplies_count())
                tvDetailRepliesCount.setText(getString(R.string.reply_count_info, count));
        }
    }

    public class TopicDetailPagerAdapter extends FragmentPagerAdapter {

        private String[] typeName = {"帖子", "评论"};

        public TopicDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mMarkdownFragment == null) {
                    mMarkdownFragment = new MarkdownFragment();
                }
                return mMarkdownFragment;
            } else {
                if (mTopicReplyFragment == null) {
                    mTopicReplyFragment = TopicReplyFragment.newInstance(mTopicId, TopicDetailActivity.this);
                }
                return mTopicReplyFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return typeName[position];
        }

        @Override
        public int getCount() {
            return typeName.length;
        }
    }

    @Bind(R.id.tv_detail_title)
    TextView tvDetailTitle;
    @Bind(R.id.sdv_detail_user_avatar)
    SimpleDraweeView sdvDetailUserAvatar;
    @Bind(R.id.tv_detail_name)
    TextView tvDetailName;
    @Bind(R.id.tv_detail_username)
    TextView tvDetailUsername;
    @Bind(R.id.tv_detail_publish_date)
    TextView tvDetailPublishDate;
    @Bind(R.id.tv_detail_replies_count)
    TextView tvDetailRepliesCount;

    TopicDetailEntity mTopicEntity;

    private void loadInfo() {
        Call<TopicDetailResponse> call = RestAdapterUtils.getRestAPI(this).getTopicById(mTopicId);

        call.enqueue(new Callback<TopicDetailResponse>() {
            @Override
            public void onResponse(Response<TopicDetailResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    mTopicEntity = response.body().getTopic();
                    if (tvDetailTitle == null) {
                        return ;
                    }
                    tvDetailTitle.setText(mTopicEntity.getTitle());
                    tvDetailName.setText(mTopicEntity.getNode_name().concat("."));
                    tvDetailUsername.setText(TextUtils.isEmpty(mTopicEntity.getUser().getLogin()) ?
                            "匿名用户" : mTopicEntity.getUser().getName());
                    tvDetailPublishDate.setText(StringUtils.formatPublishDateTime(
                            mTopicEntity.getCreated_at()).concat(".")
                            .concat(mTopicEntity.getHits()).concat("次阅读"));
                    sdvDetailUserAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTopicEntity.getUser().getAvatar_url())));
                    sdvDetailUserAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(TopicDetailActivity.this,UserInfoActivity.class).putExtra("loginName",mTopicEntity.getUser()
                            .getLogin()));
                        }
                    });
                    // 用户回复数
                    tvDetailRepliesCount.setText(getString(R.string.reply_count_info, mTopicEntity.getReplies_count()));

                    if (PraiseUtil.hasPraised(TopicDetailActivity.this, mTopicId)) {
                        tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_heart_off, 0, 0, 0);
                    } else {
                        tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_heart, 0, 0, 0);
                    }

                    if (FavoriteUtil.hasFavorite(TopicDetailActivity.this, mTopicId)) {
                        tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark_off, 0, 0, 0);
                    } else {
                        tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark, 0, 0, 0);
                    }

                    if (mMarkdownFragment != null) {
                        mMarkdownFragment.showWebContent(response.body().getTopic().getBody_html());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (mFabAddComment != null) {
                    Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    Log.e("TopicDetailActivity", "Error:" + t.getMessage());
                }
            }
        });

    }

    private TesterUser mCurrentUser;
    @Bind(R.id.tv_detail_collect)
    TextView tvDetailCollect;
    @Bind(R.id.tv_detail_praise)
    TextView tvDetailPraise;

    boolean isFavorite = false;

    @OnClick(R.id.tv_detail_collect)
    void onDetailCollectClick() {
        if (mCurrentUser == null) {
            mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
            if (TextUtils.isEmpty(mCurrentUser.getLogin())) {
                Snackbar.make(mFabAddComment, "请先登录客户端", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }

        // TODO: check login and token not expire
        Call<CollectTopicResonse> call;
        if (FavoriteUtil.hasFavorite(TopicDetailActivity.this, mTopicId)) {
            // 取消收藏
            call = TesterHomeApi.getInstance().getTopicsService().uncollectTopic(mTopicId, mCurrentUser.getAccess_token());
            isFavorite = true;
        } else {
            call = TesterHomeApi.getInstance().getTopicsService().collectTopic(mTopicId, mCurrentUser.getAccess_token());
            isFavorite = false;
        }

        call.enqueue(new Callback<CollectTopicResonse>() {
            @Override
            public void onResponse(Response<CollectTopicResonse> response, Retrofit retrofit) {
                if (response.body() != null){
                    if (isFavorite){
                        if (response.body().getOk() == 1) {
                            Snackbar.make(mFabAddComment, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
                            FavoriteUtil.delFavorite(TopicDetailActivity.this, mTopicId);
                            tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark, 0, 0, 0);
                        }
                    } else {
                        if (response.body().getOk() == 1) {
                            Snackbar.make(mFabAddComment, "收藏成功", Snackbar.LENGTH_SHORT).show();
                            FavoriteUtil.addTopicToFavorite(TopicDetailActivity.this, mTopicEntity);
                            tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark_off, 0, 0, 0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    boolean isPraised = false;
    @OnClick(R.id.tv_detail_praise)
    void onDetailPraiseClick() {
        if (mCurrentUser == null) {
            mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
            if (TextUtils.isEmpty(mCurrentUser.getLogin())) {
                Snackbar.make(mFabAddComment, "请先登录客户端", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        // TODO: check login and token not expire

        Call<PraiseEntity> call;
        if (PraiseUtil.hasPraised(TopicDetailActivity.this, mTopicId)) {
            call = TesterHomeApi.getInstance().getTopicsService().unLikeTopic(Config.PRAISE_TOPIC, mTopicId, mCurrentUser.getAccess_token());
            isPraised = true;
        } else {
            call = TesterHomeApi.getInstance().getTopicsService().praiseTopic(Config.PRAISE_TOPIC, mTopicId, mCurrentUser.getAccess_token());
            isPraised = false;
        }

        call.enqueue(new Callback<PraiseEntity>() {
            @Override
            public void onResponse(Response<PraiseEntity> response, Retrofit retrofit) {
                if (response.body() != null){
                    if (isPraised){
                        Snackbar.make(mFabAddComment, "取消点赞成功", Snackbar.LENGTH_SHORT).show();
                        PraiseUtil.delPraise(TopicDetailActivity.this, mTopicId);
                        tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_heart, 0, 0, 0);
                    } else {
                        Snackbar.make(mFabAddComment, "点赞成功", Snackbar.LENGTH_SHORT).show();
                        PraiseUtil.addPraise(TopicDetailActivity.this, mTopicEntity);
                        tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_heart_off, 0, 0, 0);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Bind(R.id.llAddComment)
    View mAddCommentPanel;

    @Bind(R.id.fab_add_comment)
    FloatingActionButton mFabAddComment;

    @OnClick(R.id.fab_add_comment)
    void onFabClick() {
        mAddCommentPanel.setVisibility(View.VISIBLE);
        mFabAddComment.setVisibility(View.GONE);
    }

    @Bind(R.id.etComment)
    EditText mEtComment;

    // 回帖子
    @OnClick(R.id.btnSendComment)
    void onSendCommentClick() {
        mEtComment.setError(null);

        if (mCurrentUser == null) {
            mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        }

        if (TextUtils.isEmpty(mCurrentUser.getAccess_token())){
            Snackbar.make(mFabAddComment,
                    "请先登录",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(mEtComment.getText().toString())) {
            mEtComment.setError("请输入回复内容");
        } else {
            String replyBody = mEtComment.getText().toString();

            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(SettingsFragment.KEY_PREF_COMMENT_WITH_SNACK, true)) {
                replyBody = replyBody.concat("\n\n")
                        .concat("—— 来自TesterHome官方 [安卓客户端](http://fir.im/p9vs)");
            }


            Call<CreateReplyResponse> call =
            TesterHomeApi.getInstance().getTopicsService()
                    .createReply(mTopicId,
                            replyBody,
                            mCurrentUser.getAccess_token());

            call.enqueue(new Callback<CreateReplyResponse>() {
                @Override
                public void onResponse(Response<CreateReplyResponse> response, Retrofit retrofit) {

                    if (response.body() != null) {
                        // 发送成功
                        mEtComment.setText("");
                        DeviceUtil.hideSoftInput(TopicDetailActivity.this);
                        mAddCommentPanel.setVisibility(View.GONE);
                        mTopicReplyFragment.refreshReply();
                        // refresh reply count
                        if (response.body().getMeta() != null && response.body().getMeta().getCurrent_reply_count() > 0) {
                            updateReplyCount(response.body().getMeta().getCurrent_reply_count() + 1);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Snackbar.make(mFabAddComment,
                            t.getMessage(),
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            });

        }
    }

    @Override
    public void updateReplyTo(String replyInfo) {
        if (mEtComment != null) {
            mAddCommentPanel.setVisibility(View.VISIBLE);
            mFabAddComment.setVisibility(View.GONE);
            mEtComment.setText(replyInfo);
            mEtComment.setSelection(mEtComment.getText().toString().length());
        }
    }

    @Override
    public void onBackPressed() {
        if (mAddCommentPanel.getVisibility() == View.VISIBLE) {
            mAddCommentPanel.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

}
