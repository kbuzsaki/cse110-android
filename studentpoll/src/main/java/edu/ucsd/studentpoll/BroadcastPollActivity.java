package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import edu.ucsd.studentpoll.view.OscillatorAnimatedView;

/**
 * Created by kdhuynh on 5/22/15.
 */
public class BroadcastPollActivity extends ActionBarActivity {

    OscillatorAnimatedView mOscillatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_poll_activity);

        mOscillatorView = (OscillatorAnimatedView) findViewById(R.id.oscillator);

    }

    public void stopBroadcast(View view) {
        finish();
    }

    public void setAccessCode(String code) {
        ((TextView)findViewById(R.id.accessCode)).setText(code);
    }

    public void setPollTitle(String title) {
        ((TextView)findViewById(R.id.pollName)).setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOscillatorView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mOscillatorView.stop();
    }
}
