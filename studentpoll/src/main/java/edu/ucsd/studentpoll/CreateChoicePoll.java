package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.rest.RESTException;

import java.util.ArrayList;
import java.util.List;


public class CreateChoicePoll extends Activity {

    private static final String TAG = "CreateChoicePoll";

    private static final String SAVED_TITLE = "savedTitle";
    private static final String SAVED_OPTIONS = "savedOptions";
    private static final String SAVED_ALLOW_MULTIPLE = "savedAllowMultiple";
    private static final String SAVED_ALLOW_CUSTOM = "savedAllowCustom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_choice_poll);

        Intent intent = getIntent();

        boolean allowMultiple = intent.getBooleanExtra("allowMultiple", false);
        boolean allowCustom = intent.getBooleanExtra("allowCustom", false);

        ((CheckBox)findViewById(R.id.allow_multiple_checkbox)).setChecked(allowMultiple);
        ((CheckBox)findViewById(R.id.allow_custom_checkbox)).setChecked(allowCustom);

        EditText optionField = (EditText) findViewById(R.id.optionField);
        addOptionFieldEnterListener((LinearLayout) optionField.getParent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String title = getQuestionTitle();
        outState.putString(SAVED_TITLE, title);

        ArrayList<String> options = getQuestionOptions();
        outState.putStringArrayList(SAVED_OPTIONS, options);

        boolean allowMultiple = getAllowMultiple();
        outState.putBoolean(SAVED_ALLOW_MULTIPLE, allowMultiple);

        boolean allowCustom = getAllowCustom();
        outState.putBoolean(SAVED_ALLOW_CUSTOM, allowCustom);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            return;
        }

        String title = savedInstanceState.getString(SAVED_TITLE);
        List<String> options = savedInstanceState.getStringArrayList(SAVED_OPTIONS);
        boolean allowMultiple = savedInstanceState.getBoolean(SAVED_ALLOW_MULTIPLE);
        boolean allowCustom = savedInstanceState.getBoolean(SAVED_ALLOW_CUSTOM);

        EditText titleBox = (EditText) findViewById(R.id.titleBox);
        titleBox.setText(title);

        // if we have saved state, then clear the default stuff
        LinearLayout optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
        optionsLayout.removeAllViews();

        for(int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            View optionEntry = LayoutInflater.from(this).inflate(R.layout.create_choice_poll_option, null);
            EditText optionText = (EditText) optionEntry.findViewById(R.id.optionField);
            optionText.setText(option);
            // set all but the last button to have an x for closing
            if(i != options.size() - 1) {
                Button addButton = (Button)optionEntry.findViewById(R.id.optionAdd);
                addButton.setText("x");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removePollOption(view);
                    }
                });
            }

            addOptionFieldEnterListener((LinearLayout) optionEntry);
            optionsLayout.addView(optionEntry);
        }

        ((CheckBox)findViewById(R.id.allow_multiple_checkbox)).setChecked(allowMultiple);
        ((CheckBox)findViewById(R.id.allow_custom_checkbox)).setChecked(allowCustom);
    }

    public void addPollOption(View view) {
        LinearLayout clickedOptionContainer = (LinearLayout)view.getParent();
        Button addButton = (Button)clickedOptionContainer.findViewById(R.id.optionAdd);
        addButton.setText("x");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePollOption(view);
            }
        });

        LinearLayout optionsContainer = (LinearLayout)clickedOptionContainer.getParent();
        View newPollOption = LayoutInflater.from(this).inflate(R.layout.create_choice_poll_option, null);
        addOptionFieldEnterListener((LinearLayout)newPollOption);
        optionsContainer.addView(newPollOption);
    }

    public void removePollOption(View view) {
        LinearLayout clickedOptionContainer = (LinearLayout)view.getParent();
        LinearLayout optionsContainer = (LinearLayout)clickedOptionContainer.getParent();
        optionsContainer.removeView(clickedOptionContainer);
    }

    public void submitPoll(View view) {
        String name = ((EditText)findViewById(R.id.titleBox)).getText().toString();

        LinearLayout optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
        List<String> options = new ArrayList<>();
        for(int childIndex = 0; childIndex < optionsLayout.getChildCount(); childIndex++) {
            View child =optionsLayout.getChildAt(childIndex);
            String option = ((EditText) child.findViewById(R.id.optionField)).getText().toString();
            options.add(option);
        }

        Log.d(TAG, "name: " + name);
        Log.d(TAG, "options: " + options);

        final Poll poll = new Poll.Builder().withTitle("My Title").withChoiceQuestion(name, options).build();

        new AsyncTask<Object, Object, Poll>() {
            @Override
            protected Poll doInBackground(Object[] params) {
                try {
                    return Poll.postPoll(poll);
                }
                catch (RESTException e) {
                    Log.w(TAG, "Failed to post poll", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Poll poll) {
                if(poll == null) {
                    Toast.makeText(getApplicationContext(), "Failed to make poll.", Toast.LENGTH_SHORT).show();
                }
                else {

                }
            }
        }.execute();

        finish();
    }

    private void addOptionFieldEnterListener(LinearLayout optionFieldLayout) {
        EditText optionField = (EditText) optionFieldLayout.findViewById(R.id.optionField);
        final Button addButton = (Button) optionFieldLayout.findViewById(R.id.optionAdd);

        optionField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    addButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private String getQuestionTitle() {
        EditText titleBox = (EditText) findViewById(R.id.titleBox);
        return titleBox.getText().toString();
    }

    private ArrayList<String> getQuestionOptions() {
        ArrayList<String> options = new ArrayList<>();

        LinearLayout optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
        for(int i = 0; i < optionsLayout.getChildCount(); i++) {
            View optionEntry = optionsLayout.getChildAt(i);
            EditText optionBox = (EditText) optionEntry.findViewById(R.id.optionField);
            options.add(optionBox.getText().toString());
        }

        return options;
    }

    private boolean getAllowMultiple() {
        CheckBox allowMultipleCheckbox = (CheckBox) findViewById(R.id.allow_multiple_checkbox);
        return allowMultipleCheckbox.isChecked();
    }

    private boolean getAllowCustom() {
        CheckBox allowCustomCheckbox = (CheckBox) findViewById(R.id.allow_custom_checkbox);
        return allowCustomCheckbox.isChecked();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_choice_poll, menu);
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
}
