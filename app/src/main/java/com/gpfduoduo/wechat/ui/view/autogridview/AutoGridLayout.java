package com.gpfduoduo.wechat.ui.view.autogridview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;

public class AutoGridLayout extends ViewGroup {
    private static final String tag = AutoGridLayout.class.getSimpleName();

    private final int ITEM_GAP_WIDTH = (int) getResources().getDimension(
            R.dimen.x10);
    private final int ITEM_GAP_HEIGHT = (int) getResources().getDimension(
            R.dimen.y10);
    private final int DEFAULT_WIDTH = (int) getResources().getDimension(
            R.dimen.x90);
    private final int DEFAULT_HEIGHT = (int) getResources().getDimension(
            R.dimen.y90);

    private AutoGridAdapter<?> mAutoGridAdapter;
    private int mTotalWidth;
    private int mSingleWidth, mSingleHeight;
    private int mItemGapWidth, mItemGapHeight;
    private int mColumns;
    private int mRows;

    public interface OnItemClickListener {
        public void onItem(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;


    public AutoGridLayout(Context context) {
        this(context, null);
    }


    public AutoGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public AutoGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mItemGapWidth = ITEM_GAP_WIDTH;
        mItemGapHeight = ITEM_GAP_HEIGHT;
        mSingleWidth = DEFAULT_WIDTH;
        mSingleHeight = DEFAULT_HEIGHT;
    }


    @SuppressLint("DrawAllocation") @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAutoGridAdapter == null || mAutoGridAdapter.getCount() == 0) {
            return;
        }
        int childCount = mAutoGridAdapter.getCount();
        for (int i = 0; i < childCount; i++) {
            int[] position = findPosition(i);
            int left = (mSingleWidth + mItemGapWidth) * position[1] +
                    getPaddingLeft();
            int top = (mSingleHeight + mItemGapHeight) * position[0] +
                    getPaddingTop();
            int right = left + mSingleWidth;
            int bottom = top + mSingleHeight;
            View childView = getChildAt(i);
            final int itemPos = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItem(v, itemPos);
                    }
                }
            });
            childView.layout(left, top, right, bottom);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        mTotalWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        if (mAutoGridAdapter != null && mAutoGridAdapter.getCount() > 0) {
            int measureWidth, measureHeight;
            int childCount = mAutoGridAdapter.getCount();
            if (childCount == 1) {
            }
            else {
                mSingleWidth = (mTotalWidth - mItemGapWidth * (3 - 1)) / 3;
            }
            measureChildren(MeasureSpec.makeMeasureSpec(mSingleWidth,
                    MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mSingleHeight,
                            MeasureSpec.EXACTLY));

            measureWidth = mSingleWidth * mColumns +
                    mItemGapWidth * (mColumns - 1);
            measureHeight = mSingleHeight * mRows +
                    mItemGapHeight * (mRows - 1);

            setMeasuredDimension(measureWidth, measureHeight);
        }
    }


    public void setAdapter(AutoGridAdapter<?> adapter) {
        this.mAutoGridAdapter = adapter;
        if (adapter == null) return;
        generateChildrenLayout(adapter.getCount());

        removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null);
            addView(itemView, generateDefaultLayoutParams());
        }

        requestLayout();
    }


    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mColumns; j++) {
                if ((i * mColumns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }


    private void generateChildrenLayout(int count) {
        if (count <= 3) {
            mRows = 1;
            mColumns = count;
        }
        else if (count <= 6) {
            mRows = 2;
            mColumns = 3;
            if (count == 4) {
                mColumns = 2;
            }
        }
        else {
            mRows = 3;
            mColumns = 3;
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
