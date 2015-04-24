package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by kbuzsaki on 4/22/15.
 */
public class CreatePollChooseType extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_poll_choose_type);
    }

    public void createSingleChoicePoll(View view) {
        Intent intent = new Intent(this, CreateChoicePoll.class);
        intent.putExtra("allowMultiple", false);
        startActivity(intent);
    }

    public void createMultipleChoicePoll(View view) {
        Intent intent = new Intent(this, CreateChoicePoll.class);
        intent.putExtra("allowMultiple", true);
        startActivity(intent);
    }
}