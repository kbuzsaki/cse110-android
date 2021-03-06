package edu.ucsd.studentpoll;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.ChoiceResponse;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceResponseFragment extends ResponseFragment {

    private static final String TAG = "ChoiceResponseFragment";

    private static final String SAVED_QUESTION = TAG + ".question";

    View responseContent;

    private ChoiceQuestion choiceQuestion;

    private ChoiceResponse latestResponse;

    private List<String> choiceQuestionOptions;

    private ResponseListener responseListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);

        responseContent = inflater.inflate(R.layout.choice_response_content, getContentContainer(), false);
        getContentContainer().addView(responseContent);

        final RadioGroup optionsGroup = (RadioGroup) rootView.findViewById(R.id.options_group);
        if(choiceQuestion.getAllowCustom()) {
            Button button = new Button(getActivity());
            button.setText("Add New Option");
            button.setWidth(420);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openNewOptionDialog(optionsGroup);
                }
            });
            LinearLayout buttonContainer = (LinearLayout) rootView.findViewById(R.id.custom_option_container);
            buttonContainer.addView(button);
        }

        return superView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_QUESTION, choiceQuestion);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            choiceQuestion = savedInstanceState.getParcelable(SAVED_QUESTION);
        }
    }

    @Override
    public Question getQuestion() {
        return choiceQuestion;
    }

    @Override
    public void setQuestion(Question question) {
        if(question == null) {
            Log.w(TAG, "Setting a null question!");
        }
        else if(question instanceof ChoiceQuestion) {
            this.choiceQuestion = (ChoiceQuestion) question;
            setOptions();
        }
        else {
            throw new IllegalArgumentException("Question is not a choice question: " + question);
        }
    }

    private void setOptions() {
        choiceQuestionOptions = choiceQuestion.getOptions();
    }

    private void updateLatestResponse() {
        if(latestResponse == null) {
            User user = User.getDeviceUser();

            for(ChoiceResponse response : choiceQuestion.getResponses()) {
                if(response.getResponder() == user) {
                    latestResponse = response;
                    Log.d(TAG, "FOUND RESPONSE FOR CURRENT USER: " + response.getResponder().getName());
                }
            }
        }
    }

    public void refreshView() {
        if(rootView == null) {
            Log.w(TAG, "rootView null!");
            return;
        }
        // TODO: add null check for getActivity in this and other views like it

        responseListener = new ResponseListener();
        List<String> options = choiceQuestion.getOptions();

        Log.i(TAG, "refreshView started");
        updateLatestResponse();

        final RadioGroup optionsGroup = (RadioGroup) rootView.findViewById(R.id.options_group);
        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);

        pollTitle.setText(choiceQuestion.getTitle());

        optionsGroup.removeAllViews();

        for (String optionText : options) {
            addOption(optionText, optionsGroup);
        }

        if(!choiceQuestion.getAllowMultiple()) {
            optionsGroup.setOnCheckedChangeListener(responseListener);
        }

    }

    private void addOption(String optionText, RadioGroup optionsGroup) {
        if(choiceQuestion.getAllowMultiple()) {
            CheckBox button = new CheckBox(getActivity());
            if(latestResponse != null && latestResponse.getChoices().contains(optionText)) {
                button.setChecked(true);
                //optionsGroup.check(button.getId());
            }

            button.setText(optionText);
            button.setOnCheckedChangeListener(responseListener);

            optionsGroup.addView(button);

        } else {
            RadioButton button = new RadioButton(getActivity());

            button.setId(View.generateViewId());
            button.setText(optionText);
            optionsGroup.addView(button);

            Log.d(TAG, "Made button for option: " + optionText + ", with id: " + button.getId());
            if(latestResponse != null && latestResponse.getChoices().contains(optionText)) {
                Log.d(TAG, "Button with id: " + button.getId() + " is being selected.");
                optionsGroup.check(button.getId());
            }
        }
    }

    private void openNewOptionDialog(final RadioGroup optionsGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter New Option");

        final EditText newOption = new EditText(getActivity());

        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setPadding(40, 0, 40, 0);
        frameLayout.addView(newOption);

        builder.setView(frameLayout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissKeyboardFrom(newOption);
                String optionText = newOption.getText().toString();
                if(choiceQuestionOptions.contains(optionText)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Option already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    addOption(optionText, optionsGroup);
                    choiceQuestionOptions.add(optionText);
                    Toast.makeText(getActivity().getApplicationContext(), optionText + " added.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // canceled
                dismissKeyboardFrom(newOption);
            }
        });

        builder.show();
        focusKeyboardOn(newOption);
    }

    private void focusKeyboardOn(View view) {
        if(view != null) {
            view.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void dismissKeyboardFrom(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class ResponseListener implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

        private List<String> previousChoices = Collections.emptyList();

        // callback for radio buttons (single selection)
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedButton = (RadioButton) group.findViewById(checkedId);
            if(checkedButton == null) {
                Log.w(TAG, "Checked button null!");
                return;
            }

            String choice = checkedButton.getText().toString();
            updateResponse(Collections.singletonList(choice));
        }

        // callback for checkboxes (multiple selection)
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView == null) {
                Log.w(TAG, "ButtonView null!");
            }

            List<String> choices = new ArrayList<>(previousChoices);

            String choice = buttonView.getText().toString();
            if(isChecked) {
                choices.add(choice);
            }
            else {
                choices.remove(choice);
            }

            updateResponse(choices);
        }

        private void updateResponse(final List<String> choices) {
            previousChoices = choices;
            Log.d(TAG, "Updating response: " + choices);

            new AsyncTask<Object, Object, ChoiceResponse>() {
                @Override
                protected ChoiceResponse doInBackground(Object[] params) {
                    try {
                        return ChoiceResponse.putResponse(choiceQuestion, choices);
                    }
                    catch(RESTException e) {
                        Log.e(TAG, "Failed to send response", e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(ChoiceResponse choiceResponse) {
                    super.onPostExecute(choiceResponse);

                    if(choiceResponse == null) {
                        Toast.makeText(getActivity(), "Failed to send vote.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            latestResponse = choiceResponse;
                            onPutResponseListener.onResponsePut(latestResponse);
                        }
                        catch (NullPointerException e) {
                            Log.e(TAG, "Failed to update response listener", e);
                        }
                    }
                }
            }.execute();
        }

    }

}
