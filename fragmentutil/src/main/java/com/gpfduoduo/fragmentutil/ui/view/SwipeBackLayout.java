package com.gpfduoduo.fragmentutil.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.gpfduoduo.fragmentutil.R;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import com.gpfduoduo.fragmentutil.ui.SwipeBackFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * seeLink{https://github.com/ikew0ng/SwipeBackLayout}
 * Created by gpfduoduo on 2016/7/25.
 */
public class SwipeBackLayout extends FrameLayout {

    private static final String tag = SwipeBackLayout.class.getSimpleName();
    private static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;
    private static final int EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT;

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;
    private static final int FULL_ALPHA = 255;
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.4f;
    private static final int OVERSCROLL_DISTANCE = 10;

    private int mEdgeFlag = EDGE_LEFT;
    private int mCurSwipeOrientation;
    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;
    private float mScrollPercent;
    private float mScrimOpacity;

    private boolean mEnable = true;

    private View mContentView;
    private ViewDragHelper mViewDragHelper;

    private BaseFragment mFragment;
    private Fragment mPreFragment;

    private Drawable mShadowLeft, mShadowRight;
    private Rect mTmpRect = new Rect();

    private List<OnSwipeListener> mListeners;

    public interface OnSwipeListener {
        void onDragStateChange(int state);

        void onEdgeTouch(int orientationEdgeFlag);

        void onDragScrolled(float scrollPercent);
    }


    public SwipeBackLayout(Context context) {
        super(context);
        init(context, null);
    }


    public SwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
        setShadow(getResources().getDrawable(R.drawable.shadow_left),
                EDGE_LEFT);
        setEdgeOrientation(EDGE_LEFT);
    }


    public void addSwipeListener(OnSwipeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }


    public void removeSwipeListener(OnSwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }


    public void setEdgeOrientation(int orientation) {
        mEdgeFlag = orientation;
        mViewDragHelper.setEdgeTrackingEnabled(orientation);//实现边缘滑动必须实现
    }


    public void setShadow(Drawable shadow, int edgeFlag) {
        if ((edgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft = shadow;
        }
        else if ((edgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight = shadow;
        }
        invalidate();
    }


    public void attachToFragment(SwipeBackFragment fragment, View view) {
        addView(view);
        setFragment(fragment, view);
    }


    public void setFragment(BaseFragment fragment, View view) {
        this.mFragment = fragment;
        mContentView = view;
    }


    public void setSwipeBackEnabled(boolean enable) {
        this.mEnable = enable;
    }


    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnable) {
            return super.onInterceptTouchEvent(ev);
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override public boolean onTouchEvent(MotionEvent ev) {
        if (!mEnable) {
            return super.onTouchEvent(ev);
        }
        mViewDragHelper.processTouchEvent(ev);
        return true;
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean isDrawView = child == mContentView;
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        if (isDrawView && mScrimOpacity > 0 &&
                mViewDragHelper.getViewDragState() !=
                        ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }
        return drawChild;
    }


    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);

        if ((mCurSwipeOrientation & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(
                    childRect.left - mShadowLeft.getIntrinsicWidth(),
                    childRect.top, childRect.left, childRect.bottom);
            mShadowLeft.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowLeft.draw(canvas);
        }
        else if ((mCurSwipeOrientation & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(childRect.right, childRect.top,
                    childRect.right + mShadowRight.getIntrinsicWidth(),
                    childRect.bottom);
            mShadowRight.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowRight.draw(canvas);
        }
    }


    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (DEFAULT_SCRIM_COLOR & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24;

        if ((mCurSwipeOrientation & EDGE_LEFT) != 0) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        }
        else if ((mCurSwipeOrientation & EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        }
        canvas.drawColor(color);
    }


    //要实现fling效果（滑动时松手后以一定速率继续自动滑动下去并逐渐停止，类似于扔东西）或者松手后自动滑动到指定位置
    @Override public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mScrimOpacity >= 0) {
            if (mViewDragHelper.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }


    public void hideFragment(Fragment fragment) {
        if (fragment != null && fragment.getView() != null) {
            fragment.getView().setVisibility(View.GONE);
        }
    }


    public Fragment getPreFragment() {
        return mPreFragment;
    }


    class ViewDragCallback extends ViewDragHelper.Callback {

        // 返回true，表示可以捕获该View
        @Override public boolean tryCaptureView(View child, int pointerId) {
            boolean isEdge = mViewDragHelper.isEdgeTouched(mEdgeFlag,
                    pointerId);
            if (isEdge) {
                if (mViewDragHelper.isEdgeTouched(EDGE_LEFT, pointerId)) {
                    mCurSwipeOrientation = EDGE_LEFT;
                }
                else if (mViewDragHelper.isEdgeTouched(EDGE_RIGHT, pointerId)) {
                    mCurSwipeOrientation = EDGE_RIGHT;
                }
                if (mPreFragment == null) {
                    if (mFragment != null) {
                        getPreFragment();
                    }
                }
                else {
                    View preView = mPreFragment.getView();
                    if (preView != null && preView.getVisibility() != VISIBLE) {
                        preView.setVisibility(VISIBLE);
                    }
                }
            }

            return isEdge;
        }


        //控制移动的水平边界
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            if ((mCurSwipeOrientation & EDGE_LEFT) != 0) {
                ret = Math.min(Math.max(0, left), child.getWidth());
            }
            else if ((mCurSwipeOrientation & EDGE_RIGHT) != 0) {
                ret = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return ret;
        }


        //只有返回大于0的值才能正常的捕获。
        @Override public int getViewHorizontalDragRange(View child) {
            if (mFragment != null) {
                return 1;
            }
            return 0;
        }


        //当位置发生改变时回调
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if ((mCurSwipeOrientation & EDGE_LEFT) != 0) {
                if (mShadowLeft != null) {
                    mScrollPercent = Math.abs(((float) left) /
                            (mContentView.getWidth() +
                                    mShadowLeft.getIntrinsicWidth()));
                }
            }
            else if ((mCurSwipeOrientation & EDGE_RIGHT) != 0) {
                if (mShadowRight != null) {
                    mScrollPercent = Math.abs((float) left /
                            (mContentView.getWidth() +
                                    mShadowRight.getIntrinsicWidth()));
                }
            }
            invalidate();

            if (mListeners != null && !mListeners.isEmpty()/* &&
                    mViewDragHelper.getViewDragState() ==
                            ViewDragHelper.STATE_DRAGGING &&
                    mScrollPercent <= 1 && mScrollPercent > 0*/) {
                for (OnSwipeListener listener : mListeners) {
                    listener.onDragScrolled(mScrollPercent);
                }
            }

            //退出Fragment无动画
            if (mScrollPercent > 1) {
                if (mPreFragment != null &&
                        mPreFragment instanceof BaseFragment) {
                    ((BaseFragment) mPreFragment).mIsSwipeBacking = true;
                }
                if (mFragment != null) {
                    mFragment.popForSwipeBack();
                }
                if (mPreFragment != null &&
                        mPreFragment instanceof BaseFragment) {
                    ((BaseFragment) mPreFragment).mIsSwipeBacking = false;
                }
            }
        }


        @Override public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListeners != null && !mListeners.isEmpty()) {
                for (OnSwipeListener listener : mListeners) {
                    listener.onDragStateChange(state);
                }
            }
        }


        //手指释放的调用调用，来实现滑动到某一个阶段界面自动打开或者关闭的效果
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();

            int left = 0, top = 0;
            if ((mCurSwipeOrientation & EDGE_LEFT) != 0) {
                left = xvel > 0 ||
                               xvel == 0 && mScrollPercent > mScrollThreshold
                       ? (childWidth +
                        mShadowLeft.getIntrinsicWidth() +
                        OVERSCROLL_DISTANCE)
                       : 0;
            }
            else if ((mCurSwipeOrientation & EDGE_RIGHT) != 0) {
                left = xvel < 0 ||
                               xvel == 0 && mScrollPercent > mScrollThreshold
                       ? -(childWidth +
                        mShadowRight.getIntrinsicWidth() +
                        OVERSCROLL_DISTANCE)
                       : 0;
            }

            mViewDragHelper.settleCapturedViewAt(left, top);
            invalidate();
        }


        //当触摸到边界时的回调
        @Override public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            if ((mEdgeFlag & edgeFlags) != 0) {
                mCurSwipeOrientation = edgeFlags;
            }
        }


        private void getPreFragment() {
            List<Fragment> fragments = mFragment.getFragmentManager()
                                                .getFragments();
            if (fragments != null && fragments.size() > 1) {
                int index = fragments.indexOf(mFragment);
                for (int i = index - 1; i >= 0; i--) {
                    Fragment fragment = fragments.get(i);
                    if (fragment != null && fragment.getView() != null) {
                        fragment.getView().setVisibility(View.VISIBLE);
                        mPreFragment = fragment;
                        break;
                    }
                }
            }
        }
    }
}
