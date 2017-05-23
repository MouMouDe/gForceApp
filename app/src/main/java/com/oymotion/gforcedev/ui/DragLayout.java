package com.oymotion.gforcedev.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * DragLayout
 *
 * @author MouMou
 */
public class DragLayout extends FrameLayout {

    private ViewDragHelper mHelper;

    public static enum Status {
        Close, Open, Draging
    }

    private Status status = Status.Close;

    public interface OnDragChangeListener {
        void onClose();

        void onOpen();

        void onDraging(float percent);
    }

    private OnDragChangeListener onDragChangeListener;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OnDragChangeListener getOnDragChangeListener() {
        return onDragChangeListener;
    }

    public void setOnDragChangeListener(OnDragChangeListener onDragChangeListener) {
        this.onDragChangeListener = onDragChangeListener;
    }

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // forParent
        // sensitivity  default value 1.0f float
        // Callback
        // set up ViewDragHelper
        mHelper = ViewDragHelper.create(this, 1.0f, callback);
    }

    // receive the results
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        // if the child can be dragged return true
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        // return the drag range
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        //fix up the distance of left
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mMainContent) {
                left = fixLeft(left);
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            if (changedView == mLeftContent) {
                mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
                // The variation of dx transmitting the left panel happened to the main panel
                int newLeft = mMainContent.getLeft() + dx;

                newLeft = fixLeft(newLeft);
                mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
            }

            dispatchDragEvent();

            // Compatible low version ,manual redraw
            invalidate();
        }


        // Decide what to do after leaving go of screen
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            // releasedChild
            // xvel is the speed of right direction , if the value of xvel >0,for right ,Conversely
            if (xvel == 0 && mMainContent.getLeft() > mRange * 0.5f) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }
    };

    /**
     * dispach drag event ,follow the animation and update the status
     */
    protected void dispatchDragEvent() {
        // 0.0 -> 1.0
        float percent = mMainContent.getLeft() * 1.0f / mRange;

        if (onDragChangeListener != null) {
            onDragChangeListener.onDraging(percent);
        }

        // update status
        Status lastStatus = status;
        status = updateStatus(percent);
        if (lastStatus != status && onDragChangeListener != null) {
            if (status == Status.Close) {
                onDragChangeListener.onClose();
            } else if (status == Status.Open) {
                onDragChangeListener.onOpen();
            }
        }

        // Excute animation
        animViews(percent);
    }


    /**
     * update current status
     *
     * @param percent of the animations on excuting
     * @return
     */
    private Status updateStatus(float percent) {
        if (percent == 0) {
            return Status.Close;
        } else if (percent == 1) {
            return Status.Open;
        }
        return Status.Draging;
    }

    private void animViews(float percent) {
        // scale animation 0.0 -> 1.0  >>> 0.0 -> 0.5 >>>0.5 -> 1.0
        // percent * 0.5 + 0.5
        //		mLeftContent.setScaleX(percent * 0.5f + 0.5f);
        //		mLeftContent.setScaleY(percent * 0.5f + 0.5f);

        ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
        ViewHelper.setScaleY(mLeftContent, evaluate(percent, 0.5f, 1.0f));

        //		translate animation -mWidth / 2.0f -> 0
        ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth / 2.0f, 0));

        //      alpha animation 0.2f -> 1.0
        ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.2f, 1.0f));

        //		- main layout: scale animation 1.0 -> 0.8
        ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
        ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));

        //		- Background: lighteness change
        getBackground().setColorFilter((Integer) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }



    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                (int) ((startB + (int) (fraction * (endB - startB))));
    }

    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Corrected position
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > mRange) {
            return mRange;
        }
        return left;
    }

    /**
     * close the menu
     */
    public void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            if (mHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                // If the current position is not the final location. return true
                // redraw the layout,
                ViewCompat.postInvalidateOnAnimation(this);
            }

        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
        }
    }


    /**
     * open the menu
     */
    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        int finalLeft = mRange;
        if (isSmooth) {
            if (mHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }

        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
        }
    }

    //continue the animation
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private int mHeight;
    private int mWidth;
    private int mRange;

    // dispach event
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //excute while the Control size is changing
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        // Calculate drag range
        mRange = (int) (mWidth * 0.6f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new IllegalStateException("Your viewgroup must have 2 children");
        }
        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("Child must be an instance of ViewGroup");
        }
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

}
