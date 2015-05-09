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
        addOptionFieldEnterListener((LinearLayout)optionField.getParent());
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
