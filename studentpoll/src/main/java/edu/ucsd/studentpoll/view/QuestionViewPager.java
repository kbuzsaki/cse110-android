package edu.ucsd.studentpoll.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class QuestionViewPager extends ViewPager {

    private GestureDetector mGestureDetector;
    private boolean mIsLockOnHorizontalAxis = false;

    public QuestionViewPager(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new XScrollDetector());
    }

    public QuestionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new XScrollDetector());
    }

    public boolean onTouchEvent (MotionEvent ev){
        // release the lock when finger is up
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mIsLockOnHorizontalAxis = false;
            // For some reason ViewPagers hold on to focus for a bit longer than expected, which prevents immediate
            // interaction with any child views (including the VerticalViewPager)
            // so manually relinquish focus early
            clearFocus();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev){
        // decide if horizontal axis is locked already or we need to check the scrolling direction
        if (!mIsLockOnHorizontalAxis) {
            mIsLockOnHorizontalAxis = mGestureDetector.onTouchEvent(ev);
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mIsLockOnHorizontalAxis = false;
            super.onTouchEvent(ev);
        }

        return mIsLockOnHorizontalAxis;
    }

    private class XScrollDetector extends SimpleOnGestureListener {
        /**
         * @return true - if we're scrolling in X direction, false - in Y direction.
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceX) > Math.abs(distanceY);
        }

    }
}
