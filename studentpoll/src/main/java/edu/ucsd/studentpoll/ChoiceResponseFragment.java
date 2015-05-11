package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.ChoiceResponse;
import edu.ucsd.studentpoll.models.Question;

import java.util.List;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceResponseFragment extends ResponseFragment {

    private ViewGroup rootView;

    private ChoiceQuestion choiceQuestion;

    private ChoiceResponse latestResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.choice_response_fragment, container, false);

        refreshView();

        return rootView;
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
            throw new IllegalArgumentException("Question is not a choice question: " + question);
        }
    }

    public void refreshView() {
        List<String> options = choiceQuestion.getOptions();

        Log.i("ChoiceQuestionFragment", "refreshView started");

        RadioGroup optionsGroup = (RadioGroup) rootView.findViewById(R.id.options_group);
        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);

        pollTitle.setText(choiceQuestion.getTitle());

        optionsGroup.removeAllViews();

        if (choiceQuestion.getAllowMultiple()) {
            for (String option : options) {
                CheckBox button = new CheckBox(getActivity());
                button.setText(option);
                optionsGroup.addView(button);
            }
        }
        else {
            for (String option : options) {
                RadioButton button = new RadioButton(getActivity());
                button.setText(option);
                optionsGroup.addView(button);
            }
        }
    }

}
