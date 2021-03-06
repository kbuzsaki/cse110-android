package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import edu.ucsd.studentpoll.models.RankQuestion;
import edu.ucsd.studentpoll.view.NewlineInterceptor;

import java.util.ArrayList;
import java.util.List;


public class CreateRankQuestionActivity extends ActionBarActivity {

    private static final String TAG = "CreateRankQuestion";

    private static final String SAVED_TITLE = "savedTitle";
    private static final String SAVED_OPTIONS = "savedOptions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_rank_question);

        final EditText titleBox = (EditText) findViewById(R.id.titleBox);
        NewlineInterceptor.addInterceptor(titleBox, new NewlineInterceptor.OnInterceptListener() {
            @Override
            public void newlineIntercepted() {
                dismissKeyboardFrom(titleBox);
            }
        });

        Intent intent = getIntent();

        if(intent.hasExtra("question")) {
            loadExistingQuestion(intent);
        } else {

            EditText optionField = (EditText) findViewById(R.id.optionField);
            addOptionFieldEnterListener((LinearLayout) optionField.getParent());
        }

    }

    public void loadExistingQuestion(Intent intent) {
        RankQuestion rankQuestion = intent.getParcelableExtra("question");

        // load title
        EditText titleBox = (EditText) findViewById(R.id.titleBox);
        titleBox.setText(rankQuestion.getTitle());

        // load question options
        LinearLayout optionsContainer = (LinearLayout)findViewById(R.id.optionsLayout);
        optionsContainer.removeAllViews();
        for(String option : rankQuestion.getOptions()) {
            if(option.equals("")) {
                continue;
            }

            View newPollOption = LayoutInflater.from(this).inflate(R.layout.create_choice_poll_option, null);
            addOptionFieldEnterListener((LinearLayout)newPollOption);
            optionsContainer.addView(newPollOption);

            EditText optionField = (EditText)newPollOption.findViewById(R.id.optionField);
            optionField.setText(option);

            Button addButton = (Button)newPollOption.findViewById(R.id.optionAdd);
            addButton.setText("x");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeOption(view);
                }
            });
        }

        View newPollOption = LayoutInflater.from(this).inflate(R.layout.create_choice_poll_option, null);
        addOptionFieldEnterListener((LinearLayout) newPollOption);
        optionsContainer.addView(newPollOption);
        addOptionFieldEnterListener((LinearLayout) newPollOption);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String title = getQuestionTitle();
        outState.putString(SAVED_TITLE, title);

        ArrayList<String> options = getQuestionOptions();
        outState.putStringArrayList(SAVED_OPTIONS, options);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            return;
        }

        String title = savedInstanceState.getString(SAVED_TITLE);
        List<String> options = savedInstanceState.getStringArrayList(SAVED_OPTIONS);

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
                        removeOption(view);
                    }
                });
            }

            addOptionFieldEnterListener((LinearLayout) optionEntry);
            optionsLayout.addView(optionEntry);
        }
    }

    public void addOption(View view) {
        LinearLayout clickedOptionContainer = (LinearLayout)view.getParent();
        Button addButton = (Button)clickedOptionContainer.findViewById(R.id.optionAdd);
        addButton.setText("x");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOption(view);
            }
        });

        LinearLayout optionsContainer = (LinearLayout)clickedOptionContainer.getParent();
        View newPollOption = LayoutInflater.from(this).inflate(R.layout.create_choice_poll_option, null);
        addOptionFieldEnterListener((LinearLayout)newPollOption);
        optionsContainer.addView(newPollOption);
    }

    public void removeOption(View view) {
        LinearLayout clickedOptionContainer = (LinearLayout)view.getParent();
        LinearLayout optionsContainer = (LinearLayout)clickedOptionContainer.getParent();
        optionsContainer.removeView(clickedOptionContainer);
    }

    public void addQuestion(View view) {
        String name = getQuestionTitle();
        List<String> options = getQuestionOptions();

        if(name.length() == 0 ) {
            Toast.makeText(getApplicationContext(), "Please add a title", Toast.LENGTH_SHORT).show();
            return;
        } else if(options.size() == 1) {
            Toast.makeText(getApplicationContext(), "Please add more options.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "name: " + name);
        Log.d(TAG, "options: " + options);

        Intent returnIntent = new Intent();
        RankQuestion question = RankQuestion.makeTemporaryQuestion(name, options);

        returnIntent.putExtra("question", question);
        returnIntent.putExtra("index", getIntent().getIntExtra("index", 0));
        setResult(RESULT_OK, returnIntent);

        finish();
    }

    private void addOptionFieldEnterListener(LinearLayout optionFieldLayout) {
        final EditText optionField = (EditText) optionFieldLayout.findViewById(R.id.optionField);
        final Button addButton = (Button) optionFieldLayout.findViewById(R.id.optionAdd);
        focusKeyboardOn(optionField);

        optionField.setOnEditorActionListener(new NewlineInterceptor(new NewlineInterceptor.OnInterceptListener() {
            @Override
            public void newlineIntercepted() {
                dismissKeyboardFrom(optionField);
                if(addButton.getText().equals("+")) {
                    addButton.performClick();
                }
                else {
                    LinearLayout optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
                    View optionEntry = optionsLayout.getChildAt(optionsLayout.getChildCount() - 1);
                    Button lastAddButton = (Button) optionEntry.findViewById(R.id.optionAdd);
                    lastAddButton.performClick();
                }
            }
        }));
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
