package edu.ucsd.studentpoll;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Model;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.view.CompositeOnPageChangeListener;
import edu.ucsd.studentpoll.view.QuestionViewPager;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class PollActivity extends ActionBarActivity {

    private static final String TAG = "PollActivity";

    private QuestionViewPager viewPager;

    private ScreenSlidePagerAdapter pagerAdapter;

    private SlidingTabLayout slidingTabLayout;

    private Poll poll;

    // keeps our QuestionFragments in vertical sync
    private ViewPager.OnPageChangeListener pageSynchronizer = new ViewPager.SimpleOnPageChangeListener() {

        // the last position change we got
        private int currentPosition = 0;

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "page selected: " + position + ", current: " + currentPosition);
            // only broadcast a position change if it's new
            // this prevents infinite looping
            if(position != currentPosition) {
                currentPosition = position;
                for(ChoiceQuestionFragment fragment : pagerAdapter.questionFragments) {
                    Log.d(TAG, "setting position " + position + " for fragment: " + fragment);
                    fragment.setViewingPage(position);
                }
            }
        }
    };

    private ViewPager.OnPageChangeListener pageRefresher = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            final QuestionFragment currentQuestionFragment = pagerAdapter.getItem(position);
            final Question currentQuestion = currentQuestionFragment.getQuestion();
            Log.d(TAG, "Selected page: " + position + ", title: " + currentQuestion.getTitle());

            new AsyncTask<Object, Object, Question>() {
                @Override
                protected Question doInBackground(Object... params) {
                    currentQuestion.refresh();
                    Model.refreshAll(currentQuestion.getResponses());
                    return currentQuestion;
                }

                @Override
                protected void onPostExecute(Question question) {
                    currentQuestionFragment.refreshView();
                }
            }.execute();
        }
    };

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

        viewPager = (QuestionViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        viewPager.setOnPageChangeListener(new CompositeOnPageChangeListener(pageRefresher, slidingTabLayout.getViewPagerListener()));
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private List<ChoiceQuestionFragment> questionFragments;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);

            questionFragments = new ArrayList<>(poll.getQuestions().size());
            for(Question question : poll.getQuestions()) {
                if(question instanceof ChoiceQuestion) {
                    ChoiceQuestionFragment choiceQuestionFragment = new ChoiceQuestionFragment();
                    Log.i("PollActivity", "setting question:  " + question.getTitle());
                    choiceQuestionFragment.setQuestion((ChoiceQuestion) question);
                    choiceQuestionFragment.setPageSynchronizer(pageSynchronizer);

                    questionFragments.add(choiceQuestionFragment);
                }
                else {
                    throw new AssertionError("Don't know how to create fragment for question: " + question);
                }
            }
        }

        @Override
        public QuestionFragment getItem(int position) {
            ChoiceQuestionFragment fragment = questionFragments.get(position);
            Log.i("PollActivity", "got PollChoiceQuestionFragment for: " + position);
            return fragment;
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
