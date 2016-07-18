package com.gpfduoduo.wechat.ui.view.listview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import java.lang.reflect.Field;

/**
 * 作者 gpfduoduo 类说明 下拉、上拉时具有弹性的ListView
 * 原理：通过添加HeaderView和FooterView的方法，然后在拉动的时候修改HeaderView和FooterView的LayoutParameters.height
 * 缺点：在内容较少，不能全屏显示的时候，上拉动态修改FooterView的Layout height没有效果。
 */
public class BounceListView extends ListView
        implements OnScrollListener, Runnable {
    private static final String tag = BounceListView.class.getSimpleName();

    /**
     * 下拉因子,实现下拉时的延迟效果
     */
    private static final float FACTOR = 0.6F;
    /**
     * 回弹时每次减少的高度
     */
    private static final int STEP = 10;

    private static final int DOWN_THRESHOLD = 100;
    /**
     * 记录下拉的起始点
     */
    private boolean isRecorded;

    /**
     * 记录刚开始下拉时的触摸位置的Y坐标
     */
    private int startY;
    /**
     * 第一个可见条目的索引
     */
    private int firstItemIndex;

    /**
     * 用于实现下拉弹性效果的HeaderView
     */
    private View headView;
    /**
     * 用于实现上拉弹性效果的FooterView
     */
    private View footerView;

    private int downDistance;
    private boolean isUp = false;
    private boolean isDown = false;

    public interface OnDownListener {
        public void onDownListener(int distance);

        public void onDownEnd();

        public void onDownStart();
    }

    private OnDownListener mDownListener;


    public BounceListView(Context context) {
        super(context);
        init();
    }


    public BounceListView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }


    public void setDownListener(OnDownListener listener) {
        mDownListener = listener;
    }


    /**
     * 覆盖onTouchEvent方法,实现下拉回弹效果
     */
    @Override public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录下拉起点状态
                if (firstItemIndex == 0 ||
                        getLastVisiblePosition() == getCount() - 1) {
                    isRecorded = true;
                    startY = (int) event.getY();
                }
                isUp = isDown = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isRecorded) {
                    break;
                }
                post(this);
                isRecorded = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isRecorded && (firstItemIndex == 0 ||
                        getLastVisiblePosition() == getCount() - 1)) {
                    isRecorded = true;
                    startY = (int) event.getY();
                }
                if (!isRecorded) {
                    break;
                }
                int tempY = (int) event.getY();
                int moveY = tempY - startY;
                if (moveY < 0 &&
                        getLastVisiblePosition() == getCount() - 1) { //上拉刷新
                    footerView.setLayoutParams(
                            new LayoutParams(LayoutParams.MATCH_PARENT,
                                    (int) ((-moveY) * FACTOR)));
                    footerView.invalidate();
                    isUp = true;
                }

                if (moveY > 0 && firstItemIndex == 0) { //下拉刷新
                    downDistance = (int) (moveY * FACTOR);
                    headView.setLayoutParams(
                            new LayoutParams(LayoutParams.MATCH_PARENT,
                                    downDistance));
                    if (downDistance > DOWN_THRESHOLD) {
                        if (mDownListener != null) {
                            mDownListener.onDownListener(downDistance);
                        }
                        headView.invalidate();
                        if (!isDown && mDownListener != null) {
                            mDownListener.onDownStart();
                        }
                    }
                    isDown = true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }


    @Override public void setSelectionFromTop(int position, int y) {
        super.setSelectionFromTop(position, y);
    }


    @Override public void run() {
        if (isDown) {
            LayoutParams paramsHeader
                    = (LayoutParams) headView.getLayoutParams();
            paramsHeader.height -= STEP;
            headView.setLayoutParams(paramsHeader);
            headView.invalidate();
            if (paramsHeader.height <= 0) {
                isDown = false;
                if (mDownListener != null) {
                    mDownListener.onDownEnd();
                }
                return;
            }
        }
        if (isUp) {
            LayoutParams paramsFooter
                    = (LayoutParams) footerView.getLayoutParams();
            paramsFooter.height -= STEP;
            footerView.setLayoutParams(paramsFooter);
            footerView.invalidate();
            if (paramsFooter.height <= 0) {
                isUp = false;
                return;
            }
        }
        post(this);
    }


    @Override
    public void onScroll(AbsListView view, int firstVisiableItem, int visibleItemCount, int totalItemCount) {
        firstItemIndex = firstVisiableItem;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


    //去除阴影
    private void removeShadow() {
        try {
            Class<?> c = (Class<?>) Class.forName(AbsListView.class.getName());
            Field egtField = c.getDeclaredField("mEdgeGlowTop");
            Field egbBottom = c.getDeclaredField("mEdgeGlowBottom");
            egtField.setAccessible(true);
            egbBottom.setAccessible(true);
            Object egtObject = egtField.get(this); // this 指的是ListiVew实例
            Object egbObject = egbBottom.get(this);

            // egtObject.getClass() 实际上是一个 EdgeEffect 其中有两个重要属性 mGlow mEdge
            // 并且这两个属性都是Drawable类型
            Class<?> cc = (Class<?>) Class.forName(
                    egtObject.getClass().getName());
            Field mGlow = cc.getDeclaredField("mGlow");
            mGlow.setAccessible(true);
            mGlow.set(egtObject, new ColorDrawable(Color.TRANSPARENT));
            mGlow.set(egbObject, new ColorDrawable(Color.TRANSPARENT));

            Field mEdge = cc.getDeclaredField("mEdge");
            mEdge.setAccessible(true);
            mEdge.set(egtObject, new ColorDrawable(Color.TRANSPARENT));
            mEdge.set(egbObject, new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init() {
        removeShadow();
        //监听滚动状态
        setOnScrollListener(this);
        //创建PullListView的HeaderView
        headView = new View(this.getContext());
        //默认白色背景,可以改变颜色, 也可以设置背景图片
        headView.setBackgroundColor(Color.WHITE);
        //默认高度为0
        headView.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        this.addHeaderView(headView);
        footerView = new View(getContext());
        footerView.setBackgroundColor(Color.WHITE);
        footerView.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        this.addFooterView(footerView);
    }
}