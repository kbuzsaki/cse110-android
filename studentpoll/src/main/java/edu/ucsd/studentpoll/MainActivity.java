package edu.ucsd.studentpoll;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.common.util.concurrent.FutureCallback;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.Model;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.NewlineInterceptor;
import edu.ucsd.studentpoll.view.RefreshRequestListener;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity implements RefreshRequestListener {

    private static Context globalContext;

    private static final String TAG = "MainActivity";

    private ViewPager viewPager;

    private MainPagerAdapter pagerAdapter;

    private SlidingTabLayout slidingTabLayout;

    public static Context getGlobalContext() {
        return globalContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(globalContext != null) {
            Log.w(TAG, "Overwriting global context that is not null!");
        }
        globalContext = this;

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(User.isDeviceUserInitialized()) {
            refreshContent(null);
        }
        else {
            User.createDeviceUser("NewUserName", new FutureCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    Toast.makeText(MainActivity.this, "Hi, " + result.getName() + "!", Toast.LENGTH_LONG);
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed to create user :(", Toast.LENGTH_LONG);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                settingsScreen();
                return true;
            case R.id.action_join:
                joinPollDialog();
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

    private void refreshContent(final Runnable callback) {
        Log.i(TAG, "Refresh request made...");
        new AsyncTask<Object, List<Group>, List<Poll>>() {
            @Override
            protected List<Poll> doInBackground(Object... params) {
                try {
                    final long thresholdTime = SystemClock.uptimeMillis();

                    User user = User.getDeviceUser();
                    user.refreshIfOlder(thresholdTime);
                    Log.d(TAG, "Loading polls for user: " + user);

                    List<Group> groups = user.getGroups();
                    Model.refreshAllIfOlder(groups, thresholdTime);

                    List<Poll> polls = new ArrayList<>();
                    for(Group group : groups) {
                        polls.addAll(group.getPolls());
                    }

                    Model.refreshAllIfOlder(polls, thresholdTime);

                    // publish groups after refreshing polls so that we have poll names
                    publishProgress(groups);

                    for(Poll poll : polls) {
                        Model.refreshAllIfOlder(poll.getQuestions(), thresholdTime);

                        for(Question question : poll.getQuestions()) {
                            Model.refreshAllIfOlder(question.getResponses(), thresholdTime);
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
                Log.i(TAG, "Refresh request completed.");
                if(polls != null) {
                    pagerAdapter.pollsFragment.updatePolls(polls);
                }
                else {
                    Log.w(TAG, "Failed to refresh polls...");
                    Toast.makeText(MainActivity.this, "Failed Refresh", Toast.LENGTH_SHORT);
                }
                if(callback != null) {
                    callback.run();
                }
            }

            @Override
            protected void onProgressUpdate(List<Group>... values) {
                List<Group> groups = values[0];
                pagerAdapter.homeFragment.updateGroups(groups);
            }
        }.execute();
    }

    private void createPoll() {
        Intent intent = new Intent(this, CreatePollActivity.class);
        startActivity(intent);
    }

    private void joinPollDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Access Code");

        final EditText accessCodeInput = new EditText(this);
        accessCodeInput.setHint("e.g. redpanda");

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setPadding(40, 0, 40, 0);
        frameLayout.addView(accessCodeInput);

        builder.setView(frameLayout);

        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissKeyboardFrom(accessCodeInput);
                String accessCode = accessCodeInput.getText().toString();
                handleAccessCode(accessCode);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // canceled
                dismissKeyboardFrom(accessCodeInput);
            }
        });

        final AlertDialog dialog = builder.show();
        focusKeyboardOn(accessCodeInput);
        NewlineInterceptor.addInterceptor(accessCodeInput, new NewlineInterceptor.OnInterceptListener() {
            @Override
            public void newlineIntercepted() {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            }
        });
    }

    private void settingsScreen() {
        Intent intent = new Intent(this, AppSettingsActivity.class);
        startActivity(intent);
    }

    private void handleAccessCode(final String accessCode) {
        new AsyncTask<Object, Object, Poll>() {
            @Override
            protected Poll doInBackground(Object... params) {
                try {
                    final long thresholdTime = SystemClock.uptimeMillis();

                    Poll poll = Poll.joinPoll(accessCode);

                    User.getDeviceUser().refreshIfOlder(thresholdTime);
                    poll.getGroup().refreshIfOlder(thresholdTime);

                    Model.refreshAllIfOlder(poll.getQuestions(), thresholdTime);
                    for(Question question : poll.getQuestions()) {
                        Model.refreshAllIfOlder(question.getResponses(), thresholdTime);
                    }

                    return poll;
                } catch(RESTException e) {
                    Log.e(TAG, "Failed to join poll: ", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Poll poll) {
                super.onPostExecute(poll);

                if(poll == null) {
                    AlertDialog errorDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Join Failed")
                            .setMessage("We couldn't find a poll for \"" + accessCode + "\". Did you enter it correctly?")
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    joinPollDialog();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create();
                    errorDialog.show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, PollActivity.class);
                    intent.putExtra("poll", poll);
                    startActivity(intent);
                }
            }
        }.execute();
    }

    private void focusKeyboardOn(View view) {
        if(view != null) {
            view.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void dismissKeyboardFrom(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void switchToJoinPoll() {
        viewPager.setCurrentItem(0);
    }

    public void switchToCreatePoll() {
        viewPager.setCurrentItem(2);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 2;

        private PollsFragment pollsFragment;
        private HomeFragment homeFragment;

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
            pollsFragment = new PollsFragment();
            homeFragment = new HomeFragment();
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return pollsFragment;
                case 1:
                    return homeFragment;
            }
            throw new AssertionError("Can't find item for index: " + position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "Polls";
                case 1:
                    return "Groups";
            }
            throw new AssertionError("Can't find title for index: " + position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
