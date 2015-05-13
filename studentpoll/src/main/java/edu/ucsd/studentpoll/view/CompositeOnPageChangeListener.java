package edu.ucsd.studentpoll.view;

import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kbuzsaki on 5/12/15.
 */
public class CompositeOnPageChangeListener implements ViewPager.OnPageChangeListener {

    private List<ViewPager.OnPageChangeListener> listeners;

    public CompositeOnPageChangeListener(ViewPager.OnPageChangeListener... listeners) {
        this(Arrays.asList(listeners));
    }

    public CompositeOnPageChangeListener(List<ViewPager.OnPageChangeListener> listeners) {
        this.listeners = new ArrayList<>(listeners);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for(ViewPager.OnPageChangeListener listener : listeners) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for(ViewPager.OnPageChangeListener listener : listeners) {
            listener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        for(ViewPager.OnPageChangeListener listener : listeners) {
            listener.onPageScrollStateChanged(state);
        }
    }
}
