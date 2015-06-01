package edu.ucsd.studentpoll;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.common.util.concurrent.FutureCallback;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;


import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class AppSettingsActivity extends PreferenceActivity {

    private static final String TAG = "AppSettingsActivity";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        User user = User.getDeviceUser();

        final EditTextPreference usernamePreference = (EditTextPreference)findPreference("user_name");
        bindPreferenceSummaryToValue(usernamePreference, user.getName());

        final EditTextPreference userIdPreference = (EditTextPreference)findPreference("device.user.key");
        bindPreferenceSummaryToValue(userIdPreference, user.getId());

        Preference button = (Preference)findPreference("gen_user");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String username = usernamePreference.getSummary().toString();

                User.createDeviceUser(username, new FutureCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        Toast.makeText(AppSettingsActivity.this, "Hi, " + result.getName() + "!", Toast.LENGTH_LONG).show();
                        userIdPreference.setSummary(result.getId().toString());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(AppSettingsActivity.this, "Failed to create user :(", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
        });
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            preference.setSummary(stringValue);

            // handle changing the settings
            if(!preference.hasKey()) {
                // no key so just eat it
            }
            else if(preference.getKey().equals("user_name")) {
                Log.d(TAG, "got username: " + value);
                final String newName = value.toString();
                new AsyncTask<Object, Object, User>() {
                    @Override
                    protected User doInBackground(Object[] params) {
                        try {
                            return User.updateUserName(User.getDeviceUser(), newName);
                        }
                        catch (RESTException e) {
                            Log.w(TAG, e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(User result) {
                        super.onPostExecute(result);
                        if(result != null) {
                            Log.d(TAG, "Attempting to toast success");
                            Toast.makeText(AppSettingsActivity.this, "Successfully updated name to '" + result.getName() + "'", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "Attempting to toast failure");
                            Toast.makeText(AppSettingsActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
            else if(preference.getKey().equals("device.user.key")) {
                Log.d(TAG, "got user id: " + value);
                long newUserId = Long.valueOf(value.toString());
                User.setDeviceUserId(newUserId);
                syncSettings();
            }

            return true;
        }
    };

    private void syncSettings() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                User.getDeviceUser().refresh();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                findPreference("user_name").setSummary(User.getDeviceUser().getName());
            }
        }.execute();
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference, Object newValue) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        preference.setSummary(newValue.toString());
    }

}
