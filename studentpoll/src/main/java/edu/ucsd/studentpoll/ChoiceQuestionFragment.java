package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Question;

/**
 * Created by kdhuynh on 5/8/15.
 */
public class ChoiceQuestionFragment extends QuestionFragment {

    private static final String TAG = "ChoiceQuestionFragment";

    private ChoiceResponseFragment responseFragment;

    private ChoiceResultFragment resultFragment;

    private ChoiceQuestion question;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        responseFragment = new ChoiceResponseFragment();
        resultFragment = new ChoiceResultFragment();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        responseFragment.setQuestion(question);
        resultFragment.setQuestion(question);
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof ChoiceQuestion) {
            this.question = (ChoiceQuestion) question;
        }
        else {
            throw new IllegalArgumentException("Question is not a ChoiceQuestion: " + question);
        }
    }

    @Override
    public ResponseFragment getResponseFragment() {
        return responseFragment;
    }

    @Override
    public ResultFragment getResultFragment() {
        return resultFragment;
    }

}
