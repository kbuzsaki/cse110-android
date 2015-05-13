package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.ChoiceResponse;
import edu.ucsd.studentpoll.models.Question;

import java.util.*;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceResultFragment extends ResultFragment {

    private static final String TAG = "ChoiceResultFragment";

    private LinearLayout resultList;

    private ChoiceQuestion choiceQuestion;

    private Map<String, Integer> results = Collections.emptyMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);

        resultList = (LinearLayout) inflater.inflate(R.layout.choice_result_content, getContentContainer(), false);
        getContentContainer().addView(resultList);

        return superView;
    }

    @Override
    public Question getQuestion() {
        return choiceQuestion;
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof ChoiceQuestion) {
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

        if(choiceQuestion != null) {
            this.results = ChoiceResponse.aggregateResponses(choiceQuestion.getResponses());
        }

        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);
        pollTitle.setText(choiceQuestion.getTitle());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        int totalCount = 0;
        for(int count : results.values()) {
            totalCount += count;
        }

        resultList.removeAllViews();
        for(String choice : choiceQuestion.getOptions()) {
            LinearLayout option = (LinearLayout) inflater.inflate(R.layout.choice_result_option, null, false);

            TextView choiceText = (TextView) option.findViewById(R.id.voteOption);
            ProgressBar choiceBar = (ProgressBar) option.findViewById(R.id.voteBar);
            TextView choiceCounter = (TextView) option.findViewById(R.id.voteCount);

            choiceText.setText(choice);
            choiceBar.setMax(totalCount);
            choiceBar.setProgress(getCountForOption(choice));
            choiceCounter.setText("" + getCountForOption(choice));

            resultList.addView(option);
        }
    }
}
