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
import edu.ucsd.studentpoll.view.SlidingTabLayout;
import edu.ucsd.studentpoll.view.VerticalViewPager;

/**
 * Created by kdhuynh on 5/8/15.
 */
public class ChoiceQuestionFragment extends Fragment {

    private static final String TAG = "ChoiceQuestionFragment";

    private static final int QUESTION_RESPONSE_INDEX = 0;
    private static final int QUESTION_RESULTS_INDEX = 1;
    private static final int NUM_PAGES = 2;

    private ViewGroup rootView;

    private VerticalViewPager viewPager;

    private PagerAdapter pagerAdapter;

    private ChoiceQuestion question;

    private ViewPager.OnPageChangeListener pageSynchronizer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.choice_question_fragment, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.vertical_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // have to do this in onResume, otherwise it gets eaten somehow
        Log.d(TAG, "Setting page synchronizer: " + pageSynchronizer);
        viewPager.setOnPageChangeListener(pageSynchronizer);
    }

    public void setQuestion(ChoiceQuestion question) {
        this.question = question;
    }

    public void setViewingPage(int position) {
        viewPager.setCurrentItem(position);
    }

    public void setPageSynchronizer(ViewPager.OnPageChangeListener listener) {
        pageSynchronizer = listener;
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    ChoiceResponseFragment choiceResponseFragment = new ChoiceResponseFragment();
                    choiceResponseFragment.setQuestion(question);

                    return choiceResponseFragment;
                case 1:
                    ChoiceResultFragment choiceResultFragment = new ChoiceResultFragment();
                    choiceResultFragment.setQuestion(question);

                    return choiceResultFragment;
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
            return NUM_PAGES;
        }
    }
}
