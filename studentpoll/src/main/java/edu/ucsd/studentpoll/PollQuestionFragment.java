package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.ucsd.studentpoll.view.SlidingTabLayout;
import edu.ucsd.studentpoll.view.VerticalViewPager;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class PollQuestionFragment extends Fragment {

    private ViewGroup rootView;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private VerticalViewPager viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    private SlidingTabLayout slidingTabLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.question_fragment, container, false);


        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.vertical_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        return rootView;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new ChoiceResponseFragment();
                case 1:
                    return new ChoiceResultFragment();
            }
            return new HomeFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "Question";
                case 1:
                    return "Results";
            }
            return "wat";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
