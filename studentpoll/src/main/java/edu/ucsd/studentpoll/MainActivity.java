package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends Activity {

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
        for(int i = 0; i < 10; i++) {
            LayoutInflater.from(this).inflate(R.layout.poll_history_card, contentView);
            View cardView = contentView.getChildAt(i);
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = randInt(0, 7) + "/" + randInt(7, 10) + " votes";
            ((TextView)cardView.findViewById(R.id.time)).setText(timeText);
            ((TextView)cardView.findViewById(R.id.votes)).setText(voteText);
        }
    }

    public void joinPoll(View view) {
        Intent intent = new Intent(this, JoinPoll.class);
        startActivity(intent);
    }

    public void createPoll(View view) {
        Intent intent = new Intent(this, CreatePollChooseType.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
