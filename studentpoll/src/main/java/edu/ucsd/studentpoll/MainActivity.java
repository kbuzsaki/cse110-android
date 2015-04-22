package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
//
//        CardView cardView = new CardView(this);
//
//        EditText editText = new EditText(this);
//        editText.setText("card Text");
//
//        cardView.addView(editText);
//        cardView.att
//
//        contentView.addView(cardView);
    }

    public void joinPoll(View view) {
        Intent intent = new Intent(this, SingleChoicePoll.class);
        ArrayList<String> options = new ArrayList<>();
        options.addAll(Arrays.asList("Cheese", "Pepperoni", "Sausage", "Mushroom", "Onion"));
        intent.putExtra("options", options);
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
