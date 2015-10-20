package com.testerhome.nativeandroid.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.TopicDetailFragment;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

/**
 * Created by vclub on 15/9/17.
 */
public class TopicDetailActivity extends BackBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_base);
        setCustomTitle("帖子详情");

        setupView();
    }

    private void setupView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                TopicDetailFragment.newInstance(getIntent().getStringExtra("topic_id")))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                // TODO: 15/10/20 start to share
                Snackbar.make(toolbar, "share", Snackbar.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
