package me.khrystal.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/2/6
 * update time:
 * email: 723526676@qq.com
 */

public class BannerViewPager extends ViewPager {

    OnPageChangeListener mOuterPageChangeListener;
    private BannerPageAdapter mAdapter;

    private boolean isCanScroll = true;
    private boolean canLoop = true;

    public void setAdapter(PagerAdapter adapter, boolean canLoop) {
        mAdapter = (BannerPageAdapter) adapter;
    }

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
