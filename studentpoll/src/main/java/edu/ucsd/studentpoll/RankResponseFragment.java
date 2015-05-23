package edu.ucsd.studentpoll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.RankQuestion;
import edu.ucsd.studentpoll.models.RankResponse;
import edu.ucsd.studentpoll.rest.RESTException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class RankResponseFragment extends ResponseFragment {

    private static final String TAG = "RankResponseFragment";

    private static final String SAVED_QUESTION = TAG + ".question";

    View responseContent;

    private RankQuestion rankQuestion;

    private RankResponse latestResponse;

    private ResponseListener responseListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);

        responseContent = inflater.inflate(R.layout.rank_response_content, getContentContainer(), false);
        getContentContainer().addView(responseContent);

        return superView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_QUESTION, rankQuestion);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            rankQuestion = savedInstanceState.getParcelable(SAVED_QUESTION);
        }
    }

    @Override
    public Question getQuestion() {
        return rankQuestion;
    }

    @Override
    public void setQuestion(Question question) {
        if(question == null) {
            Log.w(TAG, "Setting a null question!");
        }
        else if(question instanceof RankQuestion) {
            this.rankQuestion = (RankQuestion) question;
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
        List<String> options = rankQuestion.getOptions();

        Log.i(TAG, "refreshView started");

        RadioGroup optionsGroup = (RadioGroup) rootView.findViewById(R.id.options_group);
        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);

        pollTitle.setText(rankQuestion.getTitle());

        optionsGroup.removeAllViews();
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

            new AsyncTask<Object, Object, RankResponse>() {
                @Override
                protected RankResponse doInBackground(Object[] params) {
                    try {
                        return RankResponse.putResponse(rankQuestion, choices);
                    }
                    catch(RESTException e) {
                        Log.e(TAG, "Failed to send response", e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(RankResponse rankResponse) {
                    super.onPostExecute(rankResponse);

                    if(rankResponse == null) {
                        Toast.makeText(getActivity(), "Failed to send vote.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        latestResponse = rankResponse;
                        onPutResponseListener.onResponsePut(latestResponse);
                    }
                }
            }.execute();
        }

    }

}
