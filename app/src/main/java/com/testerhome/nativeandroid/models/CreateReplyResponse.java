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

    private MetaEntity meta;

    public MetaEntity getMeta() {
        return meta;
    }

    public void setMeta(MetaEntity meta) {
        this.meta = meta;
    }

    public class MetaEntity{
        private int current_reply_count;

        public int getCurrent_reply_count() {
            return current_reply_count;
        }

        public void setCurrent_reply_count(int current_reply_count) {
            this.current_reply_count = current_reply_count;
        }
    }
}
