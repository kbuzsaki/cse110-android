package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.view.VerticalViewPager;

/**
 * Created by kdhuynh on 5/8/15.
 */
public abstract class QuestionFragment extends Fragment {

    private static final String TAG = "QuestionFragment";

    private static final int QUESTION_RESPONSE_INDEX = 0;
    private static final int QUESTION_RESULTS_INDEX = 1;
    private static final int NUM_PAGES = 2;

    private static final String QUESTION_RESPONSE_TITLE = "Question";
    private static final String QUSTION_RESULTS_TITLE = "Results";

    private ViewGroup rootView;

    private VerticalViewPager viewPager;

    private PagerAdapter pagerAdapter;

    private ViewPager.OnPageChangeListener pageSynchronizer;

    private int currentItem;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.question_fragment, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.vertical_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        viewPager.setCurrentItem(currentItem);
        // have to do this in onResume, otherwise it gets eaten somehow
        Log.d(TAG, "Setting page synchronizer: " + pageSynchronizer);
        viewPager.setOnPageChangeListener(pageSynchronizer);
    }

    public void setViewingPage(int position) {
        currentItem = position;
        if(viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }

    public void setPageSynchronizer(ViewPager.OnPageChangeListener listener) {
        pageSynchronizer = listener;
    }

    public abstract Question getQuestion();

    public abstract void setQuestion(Question question);

    public abstract ResponseFragment getResponseFragment();

    public abstract ResultFragment getResultFragment();

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case QUESTION_RESPONSE_INDEX:
                    return getResponseFragment();
                case QUESTION_RESULTS_INDEX:
                    return getResultFragment();
            }
            throw new AssertionError("No fragment for position: " + position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case QUESTION_RESPONSE_INDEX:
                    return QUESTION_RESPONSE_TITLE;
                case QUESTION_RESULTS_INDEX:
                    return QUSTION_RESULTS_TITLE;
            }
            throw new AssertionError("No title for position: " + position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
