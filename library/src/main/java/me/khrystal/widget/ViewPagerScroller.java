package me.khrystal.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/2/6
 * update time:
 * email: 723526676@qq.com
 */

public class ViewPagerScroller extends Scroller {
    /**
     * 滑动速度, 数值越大滑动越慢 滑动太快会使3d效果不明显
     */
    private int mScrollDuration = 800;
    private boolean isZero;

    public ViewPagerScroller(Context context) {
        super(context);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, isZero ? 0 : mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, isZero ? 0 : mScrollDuration);
    }

    public int getScrollDuration() {
        return mScrollDuration;
    }

    public void setScrollDuration(int scrollDuration) {
        this.mScrollDuration = scrollDuration;
    }

    public boolean isZero() {
        return isZero;
    }

    public void setZero(boolean isZero) {
        this.isZero = isZero;
    }
}
