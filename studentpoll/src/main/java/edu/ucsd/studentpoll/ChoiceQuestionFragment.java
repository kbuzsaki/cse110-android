package edu.ucsd.studentpoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceQuestionFragment extends Fragment {
    private ViewGroup rootView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.choice_question, container, false);

        Intent intent = getActivity().getIntent();
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Cat", "Dog", "Bird", "Fun", "No Fun"));

        RadioGroup optionsGroup = (RadioGroup) rootView.findViewById(R.id.options_group);
        optionsGroup.removeAllViews();

        for(String option : options) {
            RadioButton button = new RadioButton(getActivity());
            button.setText(option);
            optionsGroup.addView(button);
        }

        return rootView;
    }
}
