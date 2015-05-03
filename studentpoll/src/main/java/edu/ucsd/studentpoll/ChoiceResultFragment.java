package edu.ucsd.studentpoll;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class ChoiceResultFragment extends Fragment {
    private ViewGroup rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.choice_result, container, false);

        Intent intent = getActivity().getIntent();
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Cat", "Dog", "Bird", "Fun", "No Fun"));

        LinearLayout contentView = (LinearLayout) rootView.findViewById(R.id.resultList);

        for(int i = 0; i < 5; i++) {
            inflater.inflate(R.layout.choice_result_option, contentView);
            View resultList = contentView.getChildAt(i);
            ((TextView)resultList.findViewById(R.id.voteOption)).setText(options.get(i));
            int random = randInt(20, 99);
            ((ProgressBar)resultList.findViewById(R.id.voteBar)).setProgress(random);
            ((TextView)resultList.findViewById(R.id.voteCount)).setText(""+random);
             Log.i("ChoiceResultFragment", "adding choice result options - " + i);
        }

        return rootView;
    }
    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }
}
