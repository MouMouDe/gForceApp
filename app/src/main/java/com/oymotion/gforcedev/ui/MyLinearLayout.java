package com.oymotion.gforcedev.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/***
 * MyLinearLayout
 * @author MouMou
 */


public class MyLinearLayout extends LinearLayout {

    private DragLayout draglayout;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //intercept touch event
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (draglayout != null && draglayout.getStatus() != DragLayout.Status.Close) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (draglayout != null && draglayout.getStatus() != DragLayout.Status.Close) {
            // if the finger leave,excute the close animation
            if (event.getAction() == MotionEvent.ACTION_UP) {
                draglayout.close();
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public void setDragLayout(DragLayout draglayout) {
        this.draglayout = draglayout;
    }


}
