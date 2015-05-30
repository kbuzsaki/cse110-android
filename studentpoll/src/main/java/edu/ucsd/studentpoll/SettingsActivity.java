package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class SettingsActivity extends Activity implements OnClickListener {

    // Image loading result to pass to startActivityForResult method.
    private static int LOAD_IMAGE_RESULTS = 1;


    EditText nickname;  //user nickname
    Button button;      //change avatar button
    ImageView avatar;   //user avatar




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //'general' preferences, defined in the XML file
        //addPreferencesFromResource(R.layout.pref_general);

        // Attaching an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_nickname_key)));

        //nickname = (EditText) findViewById(R.id.nicknameID);
        //Log.e(" ","+++++++++++++++++++++++++++++++++");
        setContentView(R.layout.settings);

        button = (Button)findViewById(R.id.button);
        avatar = (ImageView)findViewById(R.id.avatar);
        nickname = (EditText)findViewById(R.id.nicknameID);

        //String str = nickname.getText().toString();

        loadSavedPreferences();

        button.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {

            //Read image data - its URI
            Log.e("SettingsActivity","!!!!!!!!!!!!!!!!!");
            Uri pickedImage = data.getData();
            Log.e("SettingsActivity", "!!!!!!!!!!!!!!!!!" + pickedImage.toString());

            //Read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Log.e("SettingsActivity", "@@@@@@@@@@@@@@@@@" + MediaStore.Images.Media.DATA);
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            Log.e("SettingsActivity", "@@@@@@@@@=======");
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            Log.e("SettingsActivity", "===============");

            cursor.close();

            //Need to set GUI ImageView data with data read from the picked file
            avatar.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            Log.e("SettingsActivity", "&&&&&&&&&&&&&&&&");

            //SharedPreferences sharedPreferences = getSharedPreferences("strings", Context.MODE_PRIVATE);

            //cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        //Create Intent for Image Gallery
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //startActivity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the image Gallery
        startActivityForResult(i, LOAD_IMAGE_RESULTS);
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("nickname", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nickname",nickname.getText().toString());
        //editor.putString("nickname",str);
        editor.commit();
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    /*private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }
*/
}