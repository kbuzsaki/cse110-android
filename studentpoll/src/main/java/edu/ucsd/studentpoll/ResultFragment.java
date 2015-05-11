package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.Question;

import java.util.Map;

/**
 * Created by kdhuynh on 5/1/15.
 */
public abstract class ResultFragment extends Fragment {

    public abstract Question getQuestion();

    public abstract void setQuestion(Question question);

}
