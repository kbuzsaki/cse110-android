package edu.ucsd.studentpoll;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.Model;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.RefreshRequestListener;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;


public class GroupActivity extends ActionBarActivity implements RefreshRequestListener {

    private static final String TAG = "GroupActivity";

    private ViewPager viewPager;

    private GroupPagerAdapter pagerAdapter;

    private SlidingTabLayout slidingTabLayout;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);

        Intent intent = getIntent();
        group = intent.getParcelableExtra("group");

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new GroupPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        // FIXME: race condition! child fragments are not ready to be inflated at this step
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inflateContent();
            }
        }, 50);
        refreshContent(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_create:
                createPoll();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshRequested(Runnable callback) {
        refreshContent(callback);
    }

    private void inflateContent() {
        if(group != null && group.isInitialized()) {
            List<User> members = group.getMembers();
            if(members != null && !members.isEmpty() && members.get(members.size() - 1).isInitialized()) {
                pagerAdapter.membersFragment.updateMembers(group.getMembers());
            }

            List<Poll> polls = group.getPolls();
            if(polls != null && !polls.isEmpty() && polls.get(polls.size() - 1).isInitialized()) {
                pagerAdapter.pollsFragment.updatePolls(group.getPolls());
            }
        }
    }

    private void refreshContent(final Runnable callback) {
        new AsyncTask<Object, List<User>, List<Poll>>() {
            @Override
            protected List<Poll> doInBackground(Object... params) {
                try {
                    group.refresh();

                    Model.refreshAll(group.getMembers());
                    publishProgress(group.getMembers());

                    List<Poll> polls = group.getPolls();
                    Model.refreshAll(polls);
                    for(Poll poll : polls) {
                        Model.refreshAll(poll.getQuestions());

                        for(Question question : poll.getQuestions()) {
                            Model.refreshAll(question.getResponses());
                        }
                    }

                    return polls;
                }
                catch (RESTException e) {
                    Log.e(TAG, "Failed to reload polls", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Poll> polls) {
                super.onPostExecute(polls);
                if(polls != null) {
                    pagerAdapter.pollsFragment.updatePolls(polls);
                }
                else {
                    Log.w(TAG, "Failed to refresh polls...");
                    Toast.makeText(GroupActivity.this, "Failed Refresh", Toast.LENGTH_SHORT);
                }
                if(callback != null) {
                    callback.run();
                }
            }

            @Override
            protected void onProgressUpdate(List<User>... values) {
                List<User> members = values[0];
                pagerAdapter.membersFragment.updateMembers(members);
            }
        }.execute();
    }

    private void createPoll() {
        Intent intent = new Intent(this, CreatePollActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    private class GroupPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 2;

        private PollsFragment pollsFragment;
        private MembersFragment membersFragment;

        public GroupPagerAdapter(FragmentManager fm) {
            super(fm);
            pollsFragment = new PollsFragment();
            membersFragment = new MembersFragment();
            pollsFragment.setGroup(group);
            membersFragment.setGroup(group);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    Log.d(TAG, "Creating Polls Fragment");
                    return pollsFragment;
                case 1:
                    Log.d(TAG, "Creating Members Fragment");
                    return membersFragment;
            }
            throw new AssertionError("Can't find item for index: " + position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "Polls";
                case 1:
                    return "Members";
            }
            throw new AssertionError("Can't find title for index: " + position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
