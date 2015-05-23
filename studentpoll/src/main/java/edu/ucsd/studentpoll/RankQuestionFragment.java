package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.RankQuestion;

/**
 * Created by kdhuynh on 5/8/15.
 */
public class RankQuestionFragment extends QuestionFragment {

    private static final String TAG = "RankQuestionFragment";

    private static final String SAVED_QUESTION = TAG + ".question";

    private RankResponseFragment responseFragment;

    private RankResultFragment resultFragment;

    private RankQuestion question;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        responseFragment = new RankResponseFragment();
        resultFragment = new RankResultFragment();

        if(question != null) {
            responseFragment.setQuestion(question);
            resultFragment.setQuestion(question);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_QUESTION, question);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            question = savedInstanceState.getParcelable(SAVED_QUESTION);
        }
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof RankQuestion) {
            this.question = (RankQuestion) question;

            if(responseFragment != null) {
                responseFragment.setQuestion(question);
            }
            if(resultFragment != null) {
                resultFragment.setQuestion(question);
            }
        }
        else {
            throw new IllegalArgumentException("Question is not a RankQuestion: " + question);
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
