package edu.ucsd.studentpoll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.ChoiceResponse;
import edu.ucsd.studentpoll.models.Model;
import edu.ucsd.studentpoll.models.Question;

import java.util.*;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceResultFragment extends ResultFragment {

    private static final String TAG = "ChoiceResultFragment";

    private static final String SAVED_QUESTION = TAG + ".question";

    private LinearLayout resultList;

    private ChoiceQuestion choiceQuestion;

    private Map<String, Integer> results = Collections.emptyMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);

        resultList = (LinearLayout) inflater.inflate(R.layout.choice_result_content, getContentContainer(), false);
        getContentContainer().addView(resultList);

        Button refreshButton = (Button) superView.findViewById(R.id.refresh_results);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "refresh async going out");
                new AsyncTask<Object, Object, Question>() {
                    @Override
                    protected Question doInBackground(Object... params) {
                        long thresholdTime = SystemClock.uptimeMillis();
                        getQuestion().refreshIfOlder(thresholdTime);
                        Model.refreshAllIfOlder(getQuestion().getResponses(), thresholdTime);
                        return getQuestion();
                    }

                    @Override
                    protected void onPostExecute(Question question) {
                        try {
                            choiceQuestion = (ChoiceQuestion) question;
                            refreshView();
                        }
                        catch (NullPointerException e) {
                            Log.e(TAG, "Failed to refresh choice result view", e);
                        }
                    }
                }.execute();
            }
        });

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
            results = ChoiceResponse.aggregateResponses(choiceQuestion.getResponses());
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
            this.results = ChoiceResponse.aggregateResponses(choiceQuestion.getResponses());
        }
        else {
            throw new AssertionError("Question is not a choice question: " + question);
        }
    }

    private int getCountForOption(String option) {
        if(results.containsKey(option)) {
            return results.get(option);
        }
        else {
            return 0;
        }
    }

    public void refreshView() {
        if(rootView == null) {
            Log.w(TAG, "rootView null!");
            return;
        }
        if(getActivity() == null) {
            Log.w(TAG, "detached fragment!");
            return;
        }
        if(choiceQuestion == null) {
            Log.w(TAG, "attempting to render ");
            return;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();

        this.results = ChoiceResponse.aggregateResponses(choiceQuestion.getResponses());

        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);
        pollTitle.setText(choiceQuestion.getTitle());

        int largestCount = 0;
        for(int count : results.values()) {
            if(count > largestCount) {
                largestCount = count;
            }
        }

        resultList.removeAllViews();
        for(String choice : choiceQuestion.getOptions()) {
            LinearLayout option = (LinearLayout) inflater.inflate(R.layout.choice_result_option, null, false);

            TextView choiceText = (TextView) option.findViewById(R.id.voteOption);
            ProgressBar choiceBar = (ProgressBar) option.findViewById(R.id.voteBar);
            TextView choiceCounter = (TextView) option.findViewById(R.id.voteCount);

            choiceText.setText(choice);
            choiceBar.setMax(largestCount);
            choiceBar.setProgress(getCountForOption(choice));
            choiceCounter.setText("" + getCountForOption(choice));

            resultList.addView(option);
        }
    }
}
