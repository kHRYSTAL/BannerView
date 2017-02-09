package me.khrystal.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/2/8
 * update time:
 * email: 723526676@qq.com
 */

public class BannerPageAdapter<T> extends PagerAdapter {

    protected List<T> mData;
    protected BannerView.BannerHolder holderCreator;
    private boolean canLoop = true;
    private BannerViewPager viewPager;
    private final int MULTIPLE_COUNT = 500;
    private BannerView.OnItemClickListener clickListener;
    
    public BannerPageAdapter(BannerView.BannerHolder holder, List<T> data) {
        this.holderCreator = holder;
        this.mData = data;
    }

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = position % realCount;
        return realPosition;
    }

    @Override
    public int getCount() {
        return canLoop ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    public int getRealCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);

        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @SuppressWarnings("unchecked")
    public View getView(final int position, View view, ViewGroup container) {
        BannerView.BannerHolder holder = null;
        if (view == null) {
            holder = holderCreator;
            view = holder.createView(container.getContext());
            view.setTag(R.id.banner_item_tag, holder);
        } else {
            holder = (BannerView.BannerHolder<T>) view.getTag(R.id.banner_item_tag);
        }
        if (mData != null && !mData.isEmpty()) {
            holder.updateUI(container.getContext(), position, mData.get(viewPager.getRealItem()));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null && viewPager != null) {
                    clickListener.onItemClick(position);
                }
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public void setViewPager(BannerViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void setOnItemClickListener(BannerView.OnItemClickListener onItemClickListener) {
        this.clickListener = onItemClickListener;
    }

}
