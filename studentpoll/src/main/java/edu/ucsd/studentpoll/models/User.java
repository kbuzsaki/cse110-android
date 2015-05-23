package edu.ucsd.studentpoll.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import edu.ucsd.studentpoll.MainActivity;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class User extends Model {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            Long id = source.readLong();
            return User.getOrStub(id);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static final ModelInstantiator<User> INSTANTIATOR = new ModelInstantiator<User>() {
        @Override
        public User fromId(Long id) {
            return User.getOrStub(id);
        }

        @Override
        public User fromJson(JSONObject object) throws JSONException {
            return User.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
        }
    };

    private static final String TAG = "User";
    private static final Map<Long, User> CACHE = new HashMap<>();

    private static final String DEVICE_USER_PREFERENCES = "device.user";
    private static final String DEVICE_USER_KEY = "device.user.key";

    private String name;

    private Drawable avatar;

    private List<Group> groups;

    private User() {
    }

    private User(Long id) {
        super(id);
    }

    public static User getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new User(id));
        }

        return CACHE.get(id);
    }

    @Override
    public void inflate() {
        if(this.id == UNINITIALIZED) {
            throw new AssertionError("Attempting to inflate uninitialized Model!");
        }

        if(!inflated) {
            AndrestClient client = new AndrestClient();
            JSONObject response = client.get(RestRouter.getUser(id));
            initFromJson(response);
        }
    }

    @Override
    User initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            name = json.getString("name");
            avatar = null;
            groups = Model.ripModelList(json.optJSONArray("groups"), Group.INSTANTIATOR);

            markRefreshed();
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        return this;
    }

    @Override
    JSONObject toJson() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public static void createDeviceUser(final String name, final FutureCallback<User> callback) {
        new AsyncTask<Object, Object, User>() {
            @Override
            protected User doInBackground(Object... params) {
                try {
                    AndrestClient client = new AndrestClient();
                    ImmutableMap<String, String> data = ImmutableMap.of("name", name);
                    String url = RestRouter.postUser() + AndrestClient.escapeParameters(data);
                    JSONObject response = client.post(url, Collections.<String, JSONObject>emptyMap());
                    try {
                        return User.INSTANTIATOR.fromJson(response);
                    }
                    catch (JSONException e) {
                        Log.wtf(TAG, e);
                        throw new RESTException(e);
                    }
                }
                catch (RESTException|UnsupportedEncodingException e) {
                    Log.e(TAG, "Failed to post user!", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if(user != null) {
                    SharedPreferences.Editor editor = getDeviceUserPreferences().edit();
                    editor.putLong(DEVICE_USER_KEY, user.getId());
                    editor.commit();
                    callback.onSuccess(user);
                }
                else {
                    callback.onFailure(new RuntimeException("Failed to create user!"));
                }
            }
        }.execute();
    }

    public static User getDeviceUser() {
        return User.getOrStub(getDeviceUserId());
    }

    private static long getDeviceUserId() {
        if(!isDeviceUserInitialized()) {
            throw new IllegalStateException("Cannot get Device User Id when Device User is uninitialized!");
        }
        SharedPreferences sharedPreferences = getDeviceUserPreferences();
        return sharedPreferences.getLong(DEVICE_USER_KEY, -1);
    }

    public static boolean isDeviceUserInitialized() {
        SharedPreferences sharedPreferences = getDeviceUserPreferences();
        return sharedPreferences.contains(DEVICE_USER_KEY);
    }

    private static SharedPreferences getDeviceUserPreferences() {
        return MainActivity.getGlobalContext().getSharedPreferences(DEVICE_USER_PREFERENCES, Context.MODE_PRIVATE);
    }

}
