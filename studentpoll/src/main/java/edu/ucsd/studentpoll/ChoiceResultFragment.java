package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Question;

import java.util.*;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceResultFragment extends ResultFragment {

    private ViewGroup rootView;

    private ChoiceQuestion choiceQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.choice_result_fragment, container, false);

        refreshView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public Question getQuestion() {
        return choiceQuestion;
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof ChoiceQuestion) {
            this.choiceQuestion = (ChoiceQuestion) question;
        }
        else {
            throw new AssertionError("Question is not a choice question: " + question);
        }
    }

    public void refreshView() {
        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);
        pollTitle.setText(choiceQuestion.getTitle());

        LinearLayout responseList = (LinearLayout) rootView.findViewById(R.id.resultList);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Map<String, Integer> responses = ChoiceQuestion.fakeResponses();

        int totalCount = 0;

        for(int count : responses.values()) {
            totalCount += count;
        }

        responseList.removeAllViews();
        for(String choice : responses.keySet()) {
            LinearLayout option = (LinearLayout) inflater.inflate(R.layout.choice_result_option, null, false);

            TextView choiceText = (TextView) option.findViewById(R.id.voteOption);
            ProgressBar choiceBar = (ProgressBar) option.findViewById(R.id.voteBar);
            TextView choiceCounter = (TextView) option.findViewById(R.id.voteCount);

            choiceText.setText(choice);
            choiceBar.setMax(totalCount);
            choiceBar.setProgress(responses.get(choice));
            choiceCounter.setText(responses.get(choice).toString());

            responseList.addView(option);
        }
    }
}
