package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

/**
 * Created by kbuzsaki on 4/22/15.
 */
public class SingleChoicePoll extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_choice_poll);

        Intent intent = getIntent();
        ArrayList<String> options = intent.getStringArrayListExtra("options");

        RadioGroup optionsGroup = (RadioGroup) findViewById(R.id.options_group);
        optionsGroup.removeAllViews();

        for(String option : options) {
            RadioButton button = new RadioButton(this);
            button.setText(option);
            optionsGroup.addView(button);
        }
    }
}