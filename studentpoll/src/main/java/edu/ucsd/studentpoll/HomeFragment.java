package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    private ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.home, container, false);

//        Button joinButton = (Button) rootView.findViewById(R.id.create_single_choice_button);
//        singleChoiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createSingleChoicePoll(view);
//            }
//        });
//
//        Button multiChoiceButton = (Button) rootView.findViewById(R.id.create_multi_choice_button);
//        multiChoiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createMultipleChoicePoll(view);
//            }
//        });

        Button joinButton = (Button) rootView.findViewById(R.id.join_poll_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchToJoinPoll();
            }
        });

        Button createButton = (Button) rootView.findViewById(R.id.new_poll_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchToCreatePoll();
            }
        });

        LinearLayout contentView = (LinearLayout) rootView.findViewById(R.id.contentView);
        for(int i = 0; i < 10; i++) {
            inflater.inflate(R.layout.poll_history_card, contentView);
            View cardView = contentView.getChildAt(i);
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = randInt(0, 7) + "/" + randInt(7, 10) + " votes";
            ((TextView)cardView.findViewById(R.id.time)).setText(timeText);
            ((TextView)cardView.findViewById(R.id.votes)).setText(voteText);
        }

        return rootView;
    }

//    public void joinPoll(View view) {
//        Intent intent = new Intent(getActivity(), JoinPoll.class);
//        startActivity(intent);
//    }
//
//    public void createPoll(View view) {
//        Intent intent = new Intent(getActivity(), CreatePollChooseType.class);
//        startActivity(intent);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if(id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }
}
