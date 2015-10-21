package com.testerhome.nativeandroid.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.TopicReplyFragment;
import com.testerhome.nativeandroid.models.CreateReplyResponse;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by cvtpc on 2015/10/16.
 */
public class TopicReplyActivity extends BackBaseActivity {

    private String mTopicId;
    private TesterUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reply);
        setCustomTitle("回帖列表");

        if (getIntent().hasExtra("topic_id")) {
            mTopicId = getIntent().getStringExtra("topic_id");
            setupView(mTopicId);
        } else {
            finish();
        }

    }

    private void setupView(String topicId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, TopicReplyFragment.newInstance(topicId))
                .commit();
    }

    @Bind(R.id.etComment)
    EditText mEtComment;

    @Bind(R.id.btnSendComment)
    Button mSendComment;

    @OnClick(R.id.btnSendComment)
    void onSendCommentClick() {
        mEtComment.setError(null);

        if (TextUtils.isEmpty(mEtComment.getText().toString())) {
            mEtComment.setError("请输入回复内容");
        } else {
            String replyBody = mEtComment.getText().toString();

            if (mCurrentUser == null) {
                mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
            }
            TesterHomeApi.getInstance().getTopicsService()
                    .createReply(mTopicId,
                            replyBody,
                            mCurrentUser.getAccess_token(), new Callback<CreateReplyResponse>() {
                                @Override
                                public void success(CreateReplyResponse createReplyResponse, Response response) {
                                    if (createReplyResponse.getError() == null){
                                        // 发送成功
                                        mEtComment.setText("");
                                        // TODO: 15/10/21 hide soft keyboard
                                    } else {
                                        Snackbar.make(mSendComment, createReplyResponse.getError().toString(), Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Snackbar.make(mSendComment, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            });
        }
    }
}
