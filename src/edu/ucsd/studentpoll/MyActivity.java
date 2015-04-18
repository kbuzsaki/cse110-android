package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Context context = getApplicationContext();
        String result = GoogleIntegration.getAccountNames(context);

        TextView tv = (TextView) findViewById(R.id.text_view);
        tv.setText(result);
    }
}
