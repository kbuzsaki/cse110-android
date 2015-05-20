package edu.ucsd.studentpoll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.ChoiceResponse;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.Response;
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

    private ResponseListener responseListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);

        responseContent = inflater.inflate(R.layout.choice_response_content, getContentContainer(), false);
        getContentContainer().addView(responseContent);

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
        }
        else {
            throw new IllegalArgumentException("Question is not a choice question: " + question);
        }
    }

    public void refreshView() {
        if(rootView == null) {
            Log.w(TAG, "rootView null!");
            return;
        }

        responseListener = new ResponseListener();
        List<String> options = choiceQuestion.getOptions();

        Log.i(TAG, "refreshView started");

        RadioGroup optionsGroup = (RadioGroup) rootView.findViewById(R.id.options_group);
        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);

        pollTitle.setText(choiceQuestion.getTitle());

        optionsGroup.removeAllViews();

        if (choiceQuestion.getAllowMultiple()) {
            for (String option : options) {
                CheckBox button = new CheckBox(getActivity());
                button.setText(option);
                if(latestResponse != null && latestResponse.getChoices().contains(option)) {
                    button.setChecked(true);
                }
                button.setOnCheckedChangeListener(responseListener);
                optionsGroup.addView(button);
            }
        }
        else {
            optionsGroup.setOnCheckedChangeListener(responseListener);
            for (String option : options) {
                RadioButton button = new RadioButton(getActivity());
                if(latestResponse != null && latestResponse.getChoices().contains(option)) {
                    button.setChecked(true);
                }
                button.setText(option);
                optionsGroup.addView(button);
            }
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
                        latestResponse = choiceResponse;
                        onPutResponseListener.onResponsePut(latestResponse);
                    }
                }
            }.execute();
        }

    }

}
