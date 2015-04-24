package edu.ucsd.studentpoll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class JoinPoll extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_poll);

        final Button submitButton = (Button) findViewById(R.id.accessCodeSubmit);
        EditText accessCodeField = (EditText) findViewById(R.id.accessCodeField);

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

        LinearLayout joinPollList = (LinearLayout) findViewById(R.id.joinPollList);
        LayoutInflater.from(this).inflate(R.layout.join_poll_card, joinPollList);
        LayoutInflater.from(this).inflate(R.layout.join_poll_card, joinPollList);
    }

    public void joinPoll(View view) {
        String accessCode = ((EditText)findViewById(R.id.accessCodeField)).getText().toString();
        if (accessCode.equalsIgnoreCase("redpanda")) {
            Intent intent = new Intent(this, SingleChoicePoll.class);
            ArrayList<String> options = new ArrayList<>();
            options.addAll(Arrays.asList("Cheese", "Pepperoni", "Sausage", "Mushroom", "Onion"));
            intent.putExtra("options", options);
            startActivity(intent);
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Join Failed")
                    .setMessage("We couldn't find a poll for that code. Did you enter it correctly?")
                    .setNeutralButton("Ok", null)
                    .create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
