package edu.ucsd.studentpoll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class JoinPoll extends Fragment {

    private ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.join_poll, container, false);

        final Button submitButton = (Button) rootView.findViewById(R.id.accessCodeSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinPoll(view);
            }
        });

        EditText accessCodeField = (EditText) rootView.findViewById(R.id.accessCodeField);
        accessCodeField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    submitButton.performClick();
                    return true;
                }
                return false;
            }
        });

        LinearLayout joinPollList = (LinearLayout) rootView.findViewById(R.id.joinPollList);
        inflater.inflate(R.layout.join_poll_card, joinPollList);
        inflater.inflate(R.layout.join_poll_card, joinPollList);

        return rootView;
    }

    private void joinPoll(View view) {
        String accessCode = ((EditText) rootView.findViewById(R.id.accessCodeField)).getText().toString();
        if (accessCode.equalsIgnoreCase("redpanda")) {
            Intent intent = new Intent(getActivity(), SingleChoicePoll.class);
            ArrayList<String> options = new ArrayList<>();
            options.addAll(Arrays.asList("Cheese", "Pepperoni", "Sausage", "Mushroom", "Onion"));
            intent.putExtra("options", options);
            startActivity(intent);
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Join Failed")
                    .setMessage("We couldn't find a poll for that code. Did you enter it correctly?")
                    .setNeutralButton("Ok", null)
                    .create();
            dialog.show();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_join_poll, menu);
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
}
