package edu.ucsd.studentpoll;

import android.content.Intent;
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
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.view.SlidingTabLayout;


public class GroupActivity extends ActionBarActivity {

    private static final String TAG = "GroupActivity";

    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
