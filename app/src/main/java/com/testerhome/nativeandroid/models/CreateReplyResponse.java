package com.testerhome.nativeandroid.models;

import java.util.List;

/**
 * Created by vclub on 15/10/21.
 */
public class CreateReplyResponse {

    private TopicReplyEntity reply;
    private List<String> error;

    public void setReply(TopicReplyEntity reply) {
        this.reply = reply;
    }

    public TopicReplyEntity getReply() {
        return reply;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public List<String> getError() {
        return error;
    }

}
