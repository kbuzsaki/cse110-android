package edu.ucsd.studentpoll;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
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
                return true;
            case R.id.action_join:
                joinPollDialog();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_create:
                createPoll();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

        builder.show();
        focusKeyboardOn(accessCodeInput);
    }

    private void handleAccessCode(String accessCode) {
        if (accessCode.equalsIgnoreCase("redpanda")) {
            Intent intent = new Intent(this, SingleChoicePoll.class);
            ArrayList<String> options = new ArrayList<>();
            options.addAll(Arrays.asList("Cheese", "Pepperoni", "Sausage", "Mushroom", "Onion"));
            intent.putExtra("options", options);
            startActivity(intent);
        }
        else {
            AlertDialog errorDialog = new AlertDialog.Builder(this)
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
                    Log.d(TAG, "Creating Polls Fragment");
                    return new PollsFragment();
                case 1:
                    Log.d(TAG, "Creating Home Fragment");
                    return new HomeFragment();
                case 2:
                    Log.d(TAG, "Creating Create Poll Fragment");
                    return new CreatePollChooseType();
            }
            return new HomeFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "Polls";
                case 1:
                    return "Groups";
                case 2:
                    return "Create";
            }
            return "wat";
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
