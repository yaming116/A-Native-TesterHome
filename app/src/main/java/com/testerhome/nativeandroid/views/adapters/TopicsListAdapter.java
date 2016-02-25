package com.testerhome.nativeandroid.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jauker.widget.BadgeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.models.ToutiaoResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.TopicDetailActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class TopicsListAdapter extends BaseAdapter<TopicEntity> {

    public static final int TOPIC_LIST_TYPE_BANNER = 0;
    public static final int TOPIC_LIST_TYPE_TOPIC = 1;
    private Context context;
    private View[] imageViews;

    public TopicsListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TOPIC_LIST_TYPE_BANNER:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_banner, parent, false);
                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.view_group);
                loadToutiao(viewGroup);
                return new TopicBannerViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_topic, parent, false);
                return new TopicItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    private TopicBannerAdapter mBannerAdapter;
    private TextView mTopicBannerTitle;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (getItemViewType(position)) {
            case TOPIC_LIST_TYPE_BANNER:
                TopicBannerViewHolder bannerViewHolder = (TopicBannerViewHolder) viewHolder;
                if (mBannerAdapter == null) {
                    mBannerAdapter = new TopicBannerAdapter();
                }
                bannerViewHolder.mTopicBanner.setAdapter(mBannerAdapter);
                startPlay(bannerViewHolder.mTopicBanner);
                mTopicBannerTitle = bannerViewHolder.mTopicBannerTitle;
                break;
            default:
                TopicItemViewHolder holder = (TopicItemViewHolder) viewHolder;

                TopicEntity topic = mItems.get(position);

                holder.topicUserAvatar.setImageURI(Uri.parse(Config.getImageUrl(topic.getUser().getAvatar_url())));
                holder.textViewTopicTitle.setText(topic.getTitle());
                holder.topicUsername.setText(TextUtils.isEmpty(topic.getUser().getName()) ? topic.getUser().getLogin() : topic.getUser().getName());

                holder.topicPublishDate.setText(StringUtils.formatPublishDateTime(topic.getCreated_at()));

                holder.topicName.setText(topic.getNode_name());

                holder.badgeView.setTargetView(holder.topicRepliesCount);
                holder.badgeView.setHideOnNull(false);
                holder.badgeView.setBadgeCount(topic.getReplies_count());
                holder.topicItem.setTag(topic.getId());
                holder.topicItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String topicId = (String) v.getTag();
                        mContext.startActivity(new Intent(mContext, TopicDetailActivity.class).putExtra("topic_id", topicId));
                    }
                });
                break;
        }

        if (position == mItems.size() - 1 && mListener != null) {
            mListener.onListEnded();
        }
    }

    private Timer timer;

    private void startPlay(final ViewPager banner){
        if (timer != null){
            timer.cancel();
        }

        long delay = 3000;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int cur = banner.getCurrentItem() + 1;
                        if ( cur >= banner.getAdapter().getCount()){
                            cur = 0;
                        }
                        banner.setCurrentItem(cur);
                    }
                });
            }
        }, delay, delay);
    }

    private EndlessListener mListener;

    public void setListener(EndlessListener mListener) {
        this.mListener = mListener;
    }

    public interface EndlessListener {
        void onListEnded();
    }

    public class TopicItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.sdv_topic_user_avatar)
        SimpleDraweeView topicUserAvatar;

        @Bind(R.id.tv_topic_title)
        TextView textViewTopicTitle;

        @Bind(R.id.tv_topic_username)
        TextView topicUsername;

        @Bind(R.id.tv_topic_publish_date)
        TextView topicPublishDate;

        @Bind(R.id.tv_topic_name)
        TextView topicName;

        @Bind(R.id.tv_topic_replies_count)
        TextView topicRepliesCount;

        @Bind(R.id.rl_topic_item)
        View topicItem;

        BadgeView badgeView;

        public TopicItemViewHolder(View itemView) {
            super(itemView);

            badgeView = new BadgeView(itemView.getContext());
            ButterKnife.bind(this, itemView);
        }
    }

    public class TopicBannerViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.vp_topic_banner)
        ViewPager mTopicBanner;

        @Bind(R.id.tv_banner_title)
        TextView mTopicBannerTitle;


        public TopicBannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mTopicBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    initPoint();
                    if (mTopicBannerTitle != null) {
                        mTopicBannerTitle.setText(mBannerAdapter.getPageTitle(position));
                        imageViews[position].setBackgroundResource(R.color.colorAccent);
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }

    private void initPoint() {
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setBackgroundResource(R.color.tab_item_tint_default);
        }
    }


    // region
    private void loadToutiao(final ViewGroup viewGroup) {
        Call<ToutiaoResponse> call = RestAdapterUtils.getRestAPI(mContext).getToutiao();

        call.enqueue(new Callback<ToutiaoResponse>() {
            @Override
            public void onResponse(Response<ToutiaoResponse> response, Retrofit retrofit) {
                if (response.body() != null) {

                    imageViews = new View[response.body().getAds().size()];
                    for (int i = 0; i < imageViews.length; i++) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                        layoutParams.weight = 1;

                        View imageView = new View(context);
                        imageViews[i] = imageView;
                        if (i == 0) {
                            imageViews[i].setBackgroundResource(R.color.colorAccent);
                        } else {
                            imageViews[i].setBackgroundResource(R.color.tab_item_tint_default);
                        }
                        viewGroup.addView(imageViews[i], layoutParams);
                    }

                    if (response.body().getAds().size() > 0) {
                        mBannerAdapter.setItems(response.body().getAds());
                        if (mBannerAdapter != null)
                            mTopicBannerTitle.setText(mBannerAdapter.getPageTitle(0));
                    } else {
                        // no info
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TopicsListAdapter", t.getMessage());
            }
        });

    }
    // endregion
}
