package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Question;

import java.util.List;

/**
 * Created by kdhuynh on 5/1/15.
 */
public abstract class ResponseFragment extends Fragment {

    protected ViewGroup rootView;

    private View.OnClickListener resultsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.response_fragment, container, false);

        Button seeResultsButton = (Button) rootView.findViewById(R.id.resultsButton);
        seeResultsButton.setOnClickListener(resultsListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    protected ViewGroup getContentContainer() {
        ViewGroup responseContentFrame = (ViewGroup) rootView.findViewById(R.id.responseContentFrame);
        return responseContentFrame;
    }

    public abstract Question getQuestion();

    public abstract void setQuestion(Question question);

    public abstract void refreshView();

    public void setSeeResultsListener(View.OnClickListener resultsListener) {
        this.resultsListener = resultsListener;

        if(rootView != null) {
            Button seeResultsButton = (Button) rootView.findViewById(R.id.resultsButton);
            seeResultsButton.setOnClickListener(resultsListener);
        }
    }

}
