package com.testerhome.nativeandroid.views.base;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

/**
 * Created by vclub on 15/9/17.
 */
public abstract class BackBaseActivity extends BaseActivity {

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (toolbar != null && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
