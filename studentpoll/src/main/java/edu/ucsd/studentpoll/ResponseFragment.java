package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public abstract Question getQuestion();

    public abstract void setQuestion(Question question);

}
