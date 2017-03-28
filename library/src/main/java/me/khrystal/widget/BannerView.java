package me.khrystal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件
 * 支持无限循环，自动翻页，翻页特效
 */
public class BannerView<T> extends LinearLayout {

    private static final String TAG = "BannerView";

    private List<T> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    private BannerPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private BannerPageAdapter pageAdapter;
    private BannerViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime = 5000;
    private boolean turning;
    private boolean canTurn = false;
    private boolean canLoop = true;
    private int indicatorPadding = 10;
    private AdSwitchTask adSwitchTask;

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
        View hView = LayoutInflater.from(context).inflate(
                R.layout.banner, this, true);
        viewPager = (BannerViewPager) hView.findViewById(R.id.viewPager);
        loPageTurningPoint = (ViewGroup) hView
                .findViewById(R.id.indicatorContainer);
        initViewPagerScroll();

        adSwitchTask = new AdSwitchTask(this);
    }

    public BannerView setPages(BannerView.BannerHolder holder, List<T> datas) {
        this.mDatas = datas;
        if (datas == null || datas.size() < 2) {
            loPageTurningPoint.setVisibility(View.INVISIBLE);
            setManualPageable(false);
            canTurn = false;
            stopTurning();
        } else {
            loPageTurningPoint.setVisibility(View.VISIBLE);
            setManualPageable(true);
            if (!isTurning()) {
                startTurning();
            }
        }
        pageAdapter = new BannerPageAdapter(holder, mDatas);
        viewPager.setAdapter(pageAdapter, canLoop);

        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
        return this;
    }

    /**
     * left right
     * 设置内部viewpager 上下左右边距
     */
    public BannerView setPagerPadding(int left, int top, int right, int bottom) {
        viewPager.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置是否clip (漏边)
     * @param flag
     * @return
     */
    public BannerView setClipParent(boolean flag) {
        viewPager.setClipToPadding(flag);
        return this;
    }

    /**
     * 设置viewpager相对父容器的间距 bottom
     * @return
     */
    public BannerView setPagerMargin(int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        return this;
    }

    public BannerView setPagerMargin(int margin) {
        viewPager.setPageMargin(margin);
        return this;
    }

    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public BannerView setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 底部指示器资源图片
     *
     * @param page_indicatorId
     */
    public BannerView setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (mDatas == null) return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(indicatorPadding, 0, indicatorPadding, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new BannerPageChangeListener(mPointViews,
                page_indicatorId);
        viewPager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getRealItem());
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);

        return this;
    }

    public BannerView setIndicatorViewGroup(ViewGroup viewGroup) {
        if (viewGroup != null)
            loPageTurningPoint = viewGroup;
        return this;
    }


    /**
     * 设置指示器padding
     */
    public void setPageIndicatorPadding(int padding) {
        if (padding != 0) {
            indicatorPadding = padding;
        }
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public BannerView setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    /***
     * 是否开启了翻页
     *
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /**
     * 设置自动翻页时间间隔
     */
    public void setTurningTime(long time) {
        this.autoTurningTime = time;
    }

    /***
     * 开始翻页
     */
    public synchronized BannerView startTurning() {
        //如果是正在翻页的话先停掉
        stopTurning();
        if (mDatas != null && mDatas.size() > 1) {
            //设置可以翻页并开启翻页
            canTurn = true;
            turning = true;
            postDelayed(adSwitchTask, autoTurningTime);
        }
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public BannerView setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(
                    viewPager.getContext());
            mScroller.set(viewPager, scroller);

        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanScroll(manualPageable);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) startTurning();
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurn) stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    //获取当前的页面index
    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getRealItem();
        }
        return -1;
    }

    //设置当前的页面index
    public void setcurrentitem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置翻页监听器
     *
     * @param onPageChangeListener
     * @return
     */
    public BannerView setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else viewPager.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        viewPager.setCanLoop(canLoop);
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (pageAdapter != null) {
            pageAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public BannerViewPager getViewPager() {
        return viewPager;
    }

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    public interface BannerHolder<T> {

        View createView(Context context);

        void UpdateUI(Context context, int position, T data);
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    static class AdSwitchTask implements Runnable {

        private final WeakReference<BannerView> reference;

        AdSwitchTask(BannerView bannerView) {
            this.reference = new WeakReference<>(bannerView);
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
}