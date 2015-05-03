package edu.ucsd.studentpoll.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class QuestionViewPager extends ViewPager {

    public QuestionViewPager(Context context) {
        super(context);
    }

    public QuestionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev){
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev){
        return super.onTouchEvent(ev);
    }
}
