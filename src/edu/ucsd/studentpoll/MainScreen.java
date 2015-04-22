package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainScreen extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
}
