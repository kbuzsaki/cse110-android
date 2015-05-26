package edu.ucsd.studentpoll.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class VerticalViewPager extends ViewPager {

    private static String TAG = "VerticalViewPager";

    private boolean locked = false;

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new VerticalPageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);

                //set Y position to swipe in from top
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
//        Log.d(TAG, "on intercept has focus: " + this.hasFocus());
//        Log.d(TAG, "on intercept focused child: " + this.getFocusedChild());
//        Log.d(TAG, "on intercept focused: " + this.findFocus());
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // return touch coordinates to original reference frame for any child views

        if(ev.getAction() == MotionEvent.ACTION_DOWN && cardViewUnder(ev)) {
            locked = true;
        }
        else if(ev.getAction() == MotionEvent.ACTION_UP) {
            locked = false;
        }
        return intercepted && !locked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "on touch has focus: " + this.hasFocus());
//        Log.d(TAG, "on touch focused child: " + this.getFocusedChild());
//        Log.d(TAG, "on touch focused: " + this.findFocus());
        return super.onTouchEvent(swapXY(ev));
    }

    private boolean cardViewUnder(MotionEvent ev) {
        Deque<View> views = new ArrayDeque<>();
        views.add(this);

        while(!views.isEmpty()) {
            View view = views.pop();
            if(!closeEnough(view, ev)) {
                continue;
            }

            if(view instanceof CardView) {
                Log.d(TAG, "found cardview!");
                return true;
            }
            else if(view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup)view;
                for(int i = 0; i < group.getChildCount(); i++) {
                    views.add(group.getChildAt(i));
                }
            }
        }

        Log.d(TAG, "did not find card view");

        return false;
    }

    private static int SLOP = 10;

    private boolean closeEnough(View view, MotionEvent ev) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0] - SLOP;
        int viewY = location[1] - SLOP;

        int x = (int)ev.getRawX();
        int y = (int)ev.getRawY();

        //point is inside view bounds
        if(( x > viewX && x < (viewX + view.getWidth()  + SLOP)) &&
           ( y > viewY && y < (viewY + view.getHeight() + SLOP))){
            return true;
        } else {
            return false;
        }
    }

}
