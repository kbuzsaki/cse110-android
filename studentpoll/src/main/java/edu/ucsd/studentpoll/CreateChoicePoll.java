package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CreateChoicePoll extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_choice_poll);

        Intent intent = getIntent();

        boolean allowMultiple = intent.getBooleanExtra("allowMultiple", false);
        boolean allowCustom = intent.getBooleanExtra("allowCustom", false);

        ((CheckBox)findViewById(R.id.allow_multiple_checkbox)).setChecked(allowMultiple);
        ((CheckBox)findViewById(R.id.allow_custom_checkbox)).setChecked(allowCustom);

        EditText optionField = (EditText) findViewById(R.id.optionField);
        addOptionFieldEnterListener((LinearLayout)optionField.getParent());
    }

    public void addPollOption(View view) {
        LinearLayout clickedOptionContainer = (LinearLayout)view.getParent();
        Button addButton = (Button)clickedOptionContainer.findViewById(R.id.optionAdd);
        addButton.setText("x");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePollOption(view);
            }
        });

        LinearLayout optionsContainer = (LinearLayout)clickedOptionContainer.getParent();
        View newPollOption = LayoutInflater.from(this).inflate(R.layout.create_choice_poll_option, null);
        addOptionFieldEnterListener((LinearLayout)newPollOption);
        optionsContainer.addView(newPollOption);
    }

    public void removePollOption(View view) {
        LinearLayout clickedOptionContainer = (LinearLayout)view.getParent();
        LinearLayout optionsContainer = (LinearLayout)clickedOptionContainer.getParent();
        optionsContainer.removeView(clickedOptionContainer);
    }

    private void addOptionFieldEnterListener(LinearLayout optionFieldLayout) {
        EditText optionField = (EditText) optionFieldLayout.findViewById(R.id.optionField);
        final Button addButton = (Button) optionFieldLayout.findViewById(R.id.optionAdd);

        optionField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    addButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_choice_poll, menu);
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
