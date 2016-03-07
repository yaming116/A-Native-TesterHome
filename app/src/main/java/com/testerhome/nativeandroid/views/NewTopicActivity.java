package com.testerhome.nativeandroid.views;

/**
 * Created by cvter on 8/3/16.
 */
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;


import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.base.BaseActivity;
import com.testerhome.nativeandroid.views.widgets.ThemeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewTopicActivity extends BaseActivity {
    @Bind(R.id.new_topic_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.new_topic_spn_tab)
    protected Spinner spnTab;

    @Bind(R.id.new_topic_edt_title)
    protected EditText edtTitle;

    @Bind(R.id.editor_bar_layout_root)
    protected ViewGroup editorBar;

    @Bind(R.id.new_topic_edt_content)
    protected EditText edtContent;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);


    }


}