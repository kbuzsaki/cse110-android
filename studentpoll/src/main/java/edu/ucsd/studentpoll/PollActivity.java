package edu.ucsd.studentpoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class PollActivity extends ActionBarActivity {
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    private SlidingTabLayout slidingTabLayout;

    private Poll poll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poll_activity);

        Intent intent = getIntent();
        Poll poll = intent.getParcelableExtra("poll");
        setPoll(poll);

        updateView();
    }

    public void setPoll(Poll poll)  {
        this.poll = poll;
    }

    public void updateView() {
        setTitle(poll.getName());

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Question question = poll.getQuestions().get(position);

            if(question instanceof ChoiceQuestion) {
                ChoiceQuestionFragment choiceQuestionFragment = new ChoiceQuestionFragment();
                Log.i("PollActivity",  "setting question:  " + question.getTitle());
                choiceQuestionFragment.setQuestion((ChoiceQuestion) question);
                Log.i("PollActivity", "got PollChoiceQuestionFragment for: " + position);

                return choiceQuestionFragment;
            }

            return new PollQuestionFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return poll.getQuestions().get(position).getTitle();
        }

        @Override
        public int getCount() {
            return poll.getQuestions().size();
        }
    }

}
