package com.testerhome.nativeandroid.views.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.R;

/**
 * Created by vclub on 15/10/28.
 */
public class TopicBannerAdapter extends PagerAdapter {

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getItemView(container, position);
        container.addView(view);
        return view;
    }

    public View getItemView(ViewGroup container, int position) {
        SimpleDraweeView imageView = new SimpleDraweeView(container.getContext());
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.btn_heart);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        // TODO: 15/10/28 加载正式图片

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
