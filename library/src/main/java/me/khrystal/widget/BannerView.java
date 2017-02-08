package me.khrystal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/2/6
 * update time:
 * email: 723526676@qq.com
 */

public class BannerView<T> extends LinearLayout {

    private List<T> mData;
    private int[] pageIndicatorId;
    private ArrayList<ImageView> mIndicators = new ArrayList<>();
    private BannerPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private BannerPageAdapter pageAdapter;
    private BannerViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup indicatorContainer;
    private long autoTurningTime = 3000;
    private boolean turning;
    private boolean canTurn = false;
    private boolean canLoop = true;
    private int indicatorPadding = 10;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private AdSwitchTask adSwitchTask;

    public interface BannerHolder<T> {
        View createView(Context context);
        void updateUI(Context context, int position, T data);
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        viewPager = (BannerViewPager) layout.findViewById(R.id.viewPager);
        indicatorContainer = (ViewGroup) layout.findViewById(R.id.indicatorContainer);
        initViewPagerScroll();

        adSwitchTask = new AdSwitchTask(this);
    }

    private void initViewPagerScroll() {

    }

    public BannerView setPages(BannerView.BannerHolder holder, List<T> data) {
        this.mData = data;
        if (data == null || data.size() < 2) {
            indicatorContainer.setVisibility(INVISIBLE);
            setCanScroll(false);
            canTurn = false;
            stopTurning();
        } else {
            indicatorContainer.setVisibility(VISIBLE);
            setCanScroll(true);
            if (!isTurning()) {
                startTurning();
            }
        }

        pageAdapter = new BannerPageAdapter(holder, mData);
        viewPager.setAdapter(pageAdapter, canLoop);

        if (pageIndicatorId != null) {
            setPageIndicator(pageIndicatorId);
        }
        return this;
    }

    static class AdSwitchTask implements Runnable {

        private final WeakReference<BannerView> reference;

        AdSwitchTask(BannerView bannerView) {
            this.reference = new WeakReference<BannerView>(bannerView);
        }

        @Override
        public void run() {
            BannerView bannerView = reference.get();

            if (bannerView != null) {
                if (bannerView.viewPager != null && bannerView.turning) {
                    int page = bannerView.viewPager.getCurrentItem() + 1;
                    bannerView.viewPager.setCurrentItem(page);
                    bannerView.postDelayed(bannerView.adSwitchTask, bannerView.autoTurningTime);
                }
            }
        }
    }

    
//    ==================== public method =======================
    /**
     * notifyDataSetChanged
     * if just add data, suggest use
     * {@see notifyDataSetAdd}
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (pageIndicatorId != null) {
            setPageIndicator(pageIndicatorId);
        }
    }

    /**
     * set indicator is visible 
     * @param visible
     * @return
     */
    public BannerView setIndicatorVisible(boolean visible) {
        indicatorContainer.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }

    /**
     * set page indicator src
     * @param pageIndicatorId
     * @return
     */
    public BannerView setPageIndicator(int[] pageIndicatorId) {
        indicatorContainer.removeAllViews();
        mIndicators.clear();
        this.pageIndicatorId = pageIndicatorId;
        if (mData == null) 
            return this;
        for (int count = 0; count < mData.size(); count++) {
            ImageView indicator = new ImageView(getContext());
            indicator.setPadding(indicatorPadding, 0, indicatorPadding, 0);
            if (mIndicators.isEmpty()) {
                indicator.setImageResource(pageIndicatorId[1]);
            } else {
                indicator.setImageResource(pageIndicatorId[0]);
            }
            mIndicators.add(indicator);
            indicatorContainer.addView(indicator);
        }
        pageChangeListener = new BannerPageChangeListener(mIndicators, pageIndicatorId);
        viewPager.addOnPageChangeListener(pageChangeListener);

        return this;
    }

    /**
     * set indicators padding
     * @param padding
     */
    public void setPageIndicatorPadding(int padding) {
        if (padding != 0) {
            indicatorPadding = padding;
        }
    }

    /**
     * set indicators align
     * @param align
     * @return
     */
    public BannerView setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) indicatorContainer.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        indicatorContainer.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * @return is auto turning
     */
    public boolean isTurning() {
        return turning;
    }

    /**
     * set auto turn duration
     * @param time time millis
     */
    public void setDurationTime(long time) {
        this.autoTurningTime = time;
    }

    public synchronized BannerView startTurning() {
        //如果是正在翻页的话先停掉
        stopTurning();
        if (mData != null && mData.size() > 1) {
            //设置可以翻页并开启翻页
            canTurn = true;
            turning = true;
            postDelayed(adSwitchTask, autoTurningTime);
        }
        return this;
    }

    /**
     * stop auto turing
     */
    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    public void setCanScroll(boolean manualPageable) {
    }




    
}
