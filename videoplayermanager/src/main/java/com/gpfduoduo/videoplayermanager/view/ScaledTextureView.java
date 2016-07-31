package com.gpfduoduo.videoplayermanager.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by gpfduoduo on 2016/7/27.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class ScaledTextureView extends TextureView {

    private static final String tag = ScaledTextureView.class.getSimpleName();

    private Integer mContentWidth, mContentHeight;
    private int mContentX = 0, mContentY = 0;

    private float mContentScaleX = 1.0f, mContentScaleY = 1.0f;
    private float mPivotPointX = 0f, mPivotPointY = 0f;
    private float mContentScaleMultiplier = 1f;
    private float mContentRotation = 0f;
    private final Matrix mTransformMatrix = new Matrix();

    private ScaleType mScaleType;

    public enum ScaleType {
        CENTER_CROP, //按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View
        TOP,
        BOTTOM,
        FILL //不按照比例内容铺满整个View
    }


    public ScaledTextureView(Context context) {
        super(context);
    }


    public ScaledTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ScaledTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setScaleType(ScaleType type) {
        mScaleType = type;
    }


    public void setContentWidth(int width) {
        mContentWidth = width;
    }


    public void setContentHeight(int height) {
        mContentHeight = height;
    }


    protected final Integer getContentWidth() {
        return mContentWidth;
    }


    protected final Integer getContentHeight() {
        return mContentHeight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mContentWidth != null && mContentHeight != null) {
            updateTextureViewSize();
        }
    }


    protected void updateTextureViewSize() {
        if (mContentWidth == null || mContentHeight == null) {
            throw new RuntimeException("null content size");
        }
        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();
        float contentWidth = mContentWidth;
        float contentHeight = mContentHeight;
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        switch (mScaleType) {
            case FILL://不按照比例
                if (viewWidth > viewHeight) {//横屏
                    scaleX = (viewHeight * contentWidth) /
                            (viewWidth * contentHeight);
                }
                else {
                    scaleY = (viewWidth * contentHeight) /
                            (viewHeight * contentWidth);
                }
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                //按照x和y的比例
                if (contentWidth > viewWidth && contentHeight > viewHeight) {
                    scaleX = contentWidth / viewWidth;
                    scaleY = contentHeight / viewHeight;
                }
                else if (contentWidth < viewWidth &&
                        contentHeight < viewHeight) {
                    scaleY = viewWidth / contentWidth;
                    scaleX = viewHeight / contentHeight;
                }
                else if (viewWidth > contentWidth) {
                    scaleY = (viewWidth / contentWidth) /
                            (viewHeight / contentHeight);
                }
                else if (viewHeight > contentHeight) {
                    scaleX = (viewHeight / contentHeight) /
                            (viewWidth / contentWidth);
                }
                break;
        }
        float pivotPointX;
        float pivotPointY;
        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = viewWidth;
                pivotPointY = viewHeight;
                break;
            case CENTER_CROP:
                pivotPointX = viewWidth / 2;
                pivotPointY = viewHeight / 2;
                break;
            case FILL:
                pivotPointX = mPivotPointX;
                pivotPointY = mPivotPointY;
                break;
            default:
                throw new IllegalStateException(
                        "pivotPointX, pivotPointY for ScaleType " + mScaleType +
                                " are not defined");
        }
        float fitCoef = 1;
        switch (mScaleType) {
            case FILL:
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (mContentHeight > mContentWidth) { //竖屏Video
                    fitCoef = viewWidth / (viewWidth * scaleX);
                }
                else { //横屏的Video
                    fitCoef = viewHeight / (viewHeight * scaleY);
                }
                break;
        }

        mContentScaleX = fitCoef * scaleX;
        mContentScaleY = fitCoef * scaleY;

        mPivotPointX = pivotPointX;
        mPivotPointY = pivotPointY;
        updateMatrixScaleRotate();
    }


    private void updateMatrixScaleRotate() {
        mTransformMatrix.reset();
        mTransformMatrix.setScale(mContentScaleX * mContentScaleMultiplier,
                mContentScaleY * mContentScaleMultiplier, mPivotPointX,
                mPivotPointY);
        mTransformMatrix.postRotate(mContentRotation, mPivotPointX,
                mPivotPointY);
        setTransform(mTransformMatrix);
    }
}
