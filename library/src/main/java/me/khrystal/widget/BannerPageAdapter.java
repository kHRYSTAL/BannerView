package me.khrystal.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

/**
 * banner ViewPager page adapter
 */
public class BannerPageAdapter<T> extends PagerAdapter {
    private static final String TAG = "BannerPagerAdapter";
    protected List<T> mDatas;
    protected BannerView.BannerHolder holderCreator;
    private boolean canLoop = true;
    private BannerViewPager viewPager;
    private final int MULTIPLE_COUNT = 300;
    private BannerView.OnItemClickListener clickListener;

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
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);

        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFristItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {
        }
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

    public BannerPageAdapter(BannerView.BannerHolder holder, List<T> datas) {
        this.holderCreator = holder;
        this.mDatas = datas;
    }

    public View getView(int position, View view, ViewGroup container) {
        BannerView.BannerHolder holder;
        if (view == null) {
            holder = holderCreator;
            view = holder.createView(container.getContext());
            view.setTag(R.id.banner_item_tag, holder);
        } else {
            holder = (BannerView.BannerHolder<T>) view.getTag(R.id.banner_item_tag);
        }
        if (mDatas != null && !mDatas.isEmpty())
            holder.UpdateUI(container.getContext(), position, mDatas.get(position));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null && viewPager != null) {
                    clickListener.onItemClick(viewPager.getRealItem());
                }
            }
        });
        return view;
    }

    public void setOnItemClickListener(BannerView.OnItemClickListener onItemClickListener) {
        this.clickListener = onItemClickListener;
    }
}
