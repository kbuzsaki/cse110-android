package edu.ucsd.studentpoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by kbuzsaki on 4/22/15.
 */
public class CreatePollChooseType extends Fragment {

    private ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.create_poll_choose_type, container, false);

        Button singleChoiceButton = (Button) rootView.findViewById(R.id.create_single_choice_button);
        singleChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSingleChoicePoll(view);
            }
        });

        Button multiChoiceButton = (Button) rootView.findViewById(R.id.create_multi_choice_button);
        multiChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMultipleChoicePoll(view);
            }
        });

        Button scheduleChoiceButton = (Button) rootView.findViewById(R.id.create_schedule_choice_button);
        scheduleChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createScheduleChoicePoll(view);
            }
        });


        return rootView;
    }

    public void createSingleChoicePoll(View view) {
        Intent intent = new Intent(getActivity(), CreateChoiceQuestionActivity.class);
        intent.putExtra("allowMultiple", false);
        startActivity(intent);
    }

    public void createMultipleChoicePoll(View view) {
        Intent intent = new Intent(getActivity(), CreateChoiceQuestionActivity.class);
        intent.putExtra("allowMultiple", true);
        startActivity(intent);
    }

    public void createScheduleChoicePoll(View view) {
        Intent intent = new Intent(getActivity(), CreateSchedulePoll.class);
        startActivity(intent);
    }
}