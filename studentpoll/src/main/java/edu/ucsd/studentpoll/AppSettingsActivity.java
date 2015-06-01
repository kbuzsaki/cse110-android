package edu.ucsd.studentpoll;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.common.util.concurrent.FutureCallback;
import edu.ucsd.studentpoll.misc.ImageUtils;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;


import java.io.FileNotFoundException;
import java.io.InputStream;

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
    private static final int LOAD_IMAGE_RESULTS = 1;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        User user = User.getDeviceUser();

        final EditTextPreference usernamePreference = (EditTextPreference)findPreference("user_name");
        bindPreferenceSummaryToValue(usernamePreference, user.getName());

        final Preference avatarPreference = findPreference("user_avatar");
        final Resources resources = getResources();
        Drawable avatar = user.getAvatar() != null ? user.getDrawableAvatar(resources) : resources.getDrawable(R.drawable.pollr_bear);
        avatarPreference.setIcon(avatar);
        avatarPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, LOAD_IMAGE_RESULTS);
                return true;
            }
        });

        final Preference resetAvatar = findPreference("avatar_reset");
        resetAvatar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                uploadAndSetAvatar(ImageUtils.drawableToBitmap(resources.getDrawable(R.drawable.pollr_bear)));
                return true;
            }
        });

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
                        updateSettings(result);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Log.d(TAG, "Got uri: " + imageUri);

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Drawable drawableAvatar = Drawable.createFromStream(inputStream, imageUri.toString());
                final Bitmap bitmapAvatar = ImageUtils.drawableToBitmap(drawableAvatar);
                final Bitmap resized = ImageUtils.resizeIfNecessary(bitmapAvatar);

                uploadAndSetAvatar(resized);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Failed to load avatar", e);
                Toast.makeText(this, "Failed to load avatar.", Toast.LENGTH_SHORT);
            }
        }
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
                            Log.e(TAG, "Failed to update username", e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(User result) {
                        super.onPostExecute(result);
                        if(result != null) {
                            updateSettings(result);
                            Toast.makeText(AppSettingsActivity.this, "Successfully updated name to '" + result.getName() + "'", Toast.LENGTH_SHORT).show();
                        }
                        else {
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
        new AsyncTask<Object, Object, User>() {
            @Override
            protected User doInBackground(Object[] params) {
                try {
                    User user = User.getDeviceUser();
                    user.refresh();
                    return user;
                }
                catch (RESTException e) {
                    Log.e(TAG, "Failed to refresh user", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if(user == null) {
                    Toast.makeText(AppSettingsActivity.this, "Failed to refresh user", Toast.LENGTH_SHORT);
                    return;
                }

                updateSettings(user);
            }
        }.execute();
    }

    private void updateSettings(User user) {
        findPreference("device.user.key").setSummary(user.getId().toString());
        findPreference("user_name").setSummary(user.getName());
        final Resources resources = getResources();
        Drawable avatar = user.getAvatar() != null ? user.getDrawableAvatar(resources) : resources.getDrawable(R.drawable.pollr_bear);
        findPreference("user_avatar").setIcon(avatar);
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

    private void uploadAndSetAvatar(final Bitmap avatar) {
        final Preference avatarPreference = findPreference("user_avatar");

        new AsyncTask<Object, Object, User>() {
            @Override
            protected User doInBackground(Object... params) {
                try {
                    return User.updateUserAvatar(User.getDeviceUser(), avatar);
                }
                catch (RESTException e) {
                    Log.e(TAG, "Failed to upload avatar", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);

                if(user != null) {
                    Toast.makeText(AppSettingsActivity.this, "Avatar uploaded!", Toast.LENGTH_SHORT);
                    avatarPreference.setIcon(user.getDrawableAvatar(getResources()));
                }
                else {
                    Toast.makeText(AppSettingsActivity.this, "Failed to upload avatar.", Toast.LENGTH_SHORT);
                }
            }
        }.execute();
    }

}
