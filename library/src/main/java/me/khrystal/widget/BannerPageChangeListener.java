package me.khrystal.widget;

import android.support.annotation.Size;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * usage: 指示器选中状态监听 (无动画交互)
 * author: kHRYSTAL
 * create time: 17/2/6
 * update time:
 * email: 723526676@qq.com
 */

public class BannerPageChangeListener implements ViewPager.OnPageChangeListener {

    private ArrayList<ImageView> pointViews;

    /** 0未选中图片 1选中图片 */
    @Size(max = 2)
    private int[] pageIndicatorId;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    public BannerPageChangeListener(ArrayList<ImageView> pointViews, @Size(min = 2, max = 2) int[] pageIndicatorId) {
        this.pointViews = pointViews;
        this.pageIndicatorId = pageIndicatorId;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < pointViews.size(); i++) {
            pointViews.get(position).setImageResource(pageIndicatorId[1]);
            if (position != i) {
                pointViews.get(i).setImageResource(pageIndicatorId[0]);
            }
        }
        if (onPageChangeListener != null)
            onPageChangeListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageScrollStateChanged(state);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
