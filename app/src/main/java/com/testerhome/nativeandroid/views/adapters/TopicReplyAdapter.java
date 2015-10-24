package com.testerhome.nativeandroid.views.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.TopicReplyEntity;
import com.testerhome.nativeandroid.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cvtpc on 2015/10/16.
 */
public class TopicReplyAdapter extends BaseAdapter<TopicReplyEntity> {

    public static String TAG = "TopicReplyAdapter";
    private final int VIEW_ITEM = 1;
    private final int VIEW_DELETE_ITEM = 0;

    public TopicReplyAdapter(Context context) {
        super(context);
    }


    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isDeleted() ? VIEW_DELETE_ITEM : VIEW_ITEM ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = View.inflate(parent.getContext(), R.layout.list_item_reply, null);
            return new ReplyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_delete_reply, parent, false);
            return new DeleteFloorHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        if (viewHolder instanceof ReplyViewHolder) {
            TopicReplyEntity topicReplyEntity = mItems.get(position);
            ReplyViewHolder holder = (ReplyViewHolder) viewHolder;
            holder.praiseReplyLayout.setVisibility(View.VISIBLE);
            holder.userAvatar.setVisibility(View.VISIBLE);
            holder.topicItemAuthor.setVisibility(View.VISIBLE);
            holder.topicTime.setVisibility(View.VISIBLE);
            holder.topicTime.setText(StringUtils.formatPublishDateTime(topicReplyEntity.getCreated_at()));
            holder.topicItemAuthor.setText(TextUtils.isEmpty(topicReplyEntity.getUser().getName()) ? topicReplyEntity.getUser().getLogin() : topicReplyEntity.getUser().getName());
            String html = topicReplyEntity.getBody_html();
//            html = html.replaceAll("src=\"/photo", "src=\"" + Config.BASEURL + "/photo");
            holder.topicItemBody.setText(Html.fromHtml(html));
            holder.topicItemBody.getPaint().setFlags(0);
            holder.userAvatar.setImageURI(Uri.parse(Config.getImageUrl(topicReplyEntity.getUser().getAvatar_url())));

        } else {
            DeleteFloorHolder holder = (DeleteFloorHolder) viewHolder;
            holder.topicItemBody.setText("该楼层已被删除");
            holder.topicItemBody.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线


        }

        if (position == mItems.size() - 1 && mListener != null) {
            mListener.onListEnded();
        }
    }

    private EndlessListener mListener;

    public void setListener(EndlessListener mListener) {
        this.mListener = mListener;
    }

    public interface EndlessListener {
        void onListEnded();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.id_praise_reply_layout)
        LinearLayout praiseReplyLayout;

        @Bind(R.id.id_topic_item_author)
        TextView topicItemAuthor;

        @Bind(R.id.id_topic_item_content)
        TextView topicItemBody;
        @Bind(R.id.id_topic_time)
        TextView topicTime;

        @Bind(R.id.id_user_avatar)
        SimpleDraweeView userAvatar;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class DeleteFloorHolder extends RecyclerView.ViewHolder{


        @Bind(R.id.id_topic_item_content)
        TextView topicItemBody;

        public DeleteFloorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
