package edu.ucsd.studentpoll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.OscillatorAnimatedView;

/**
 * Created by kdhuynh on 5/22/15.
 */
public class BroadcastPollActivity extends ActionBarActivity {

    private static final String TAG = "BroadcastPollActivity";

    OscillatorAnimatedView mOscillatorView;

    Poll poll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_poll_activity);

        mOscillatorView = (OscillatorAnimatedView) findViewById(R.id.oscillator);

        poll = getIntent().getParcelableExtra("poll");
        setPollName(poll.getName());
        setAccessCode("loading...");

        startBroadcast();
    }

    public void startBroadcast() {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object[] params) {
                try {
                    return Poll.startBroadcast(poll);
                }
                catch (RESTException e) {
                    Log.w(TAG, "Failed start broadcasting poll", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String accessCode) {
                try {
                    setAccessCode(accessCode);
                }
                catch (NullPointerException e) {
                    Log.e(TAG, "Failed to initialize broadcast", e);
                }
                Toast.makeText(getApplicationContext(), "Now Broadcasting", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public void stopBroadcast(View view) {
        Poll.stopBroadcast(poll);
        finish();
    }

    public void setAccessCode(String code) {
        ((TextView)findViewById(R.id.accessCode)).setText(code);
    }

    public void setPollName(String title) {
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
