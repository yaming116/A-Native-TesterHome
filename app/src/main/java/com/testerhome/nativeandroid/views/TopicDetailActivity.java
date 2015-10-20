package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.TopicDetailFragment;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

/**
 * Created by vclub on 15/9/17.
 */
public class TopicDetailActivity extends BackBaseActivity {

    private String mTopicId;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_base);
        setCustomTitle("帖子详情");

        if (getIntent().hasExtra("topic_id")) {
            mTopicId = getIntent().getStringExtra("topic_id");
            setupView(mTopicId);
        } else {
            finish();
        }

    }

    private void setupView(String topicId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, TopicDetailFragment.newInstance(topicId))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = new ShareActionProvider(this);
        MenuItemCompat.setActionProvider(item, mShareActionProvider);

        if (!TextUtils.isEmpty(mTopicId)){
            updateTopicShareUrl(String.format("https://testerhome.com/topics/%s", mTopicId));
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void updateTopicShareUrl(String topicUrl) {
        if (mShareActionProvider != null){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, topicUrl);
            mShareActionProvider.setShareIntent(intent);
        }
    }

}
